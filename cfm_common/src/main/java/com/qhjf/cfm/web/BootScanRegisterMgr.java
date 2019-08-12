package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 系统启动时，扫描环境类注册服务管理器
 * 注册的服务有：系统路由、optype、workBook(生成列表导出excel)
 */
public class BootScanRegisterMgr {

    private static final Logger logger = LoggerFactory.getLogger(BootScanRegisterMgr.class);

    private static final String base_pkg_name = "com.qhjf.cfm.web";

    private static final String routeSufix = "Routes.class";

    private static final String optypeSufix = "OptypeMgr.class";

    private static final String workBookSufix = "WorkBook.class";

    private static final String sqlTemplateSufix = "_cfm.sql";

    private static final String sqlTemplaterPrefix = "sql/"+ GlobalConfigSection.getInstance().dbType();

    private List<String> sqlTemplates = new ArrayList<>();

    private Routes routes;

    private AbstractOptypeMgr optypeMgr;


    private static final BootScanRegisterMgr INSTANCE = new BootScanRegisterMgr();

    public static BootScanRegisterMgr getInstance() {
        return INSTANCE;
    }

    private BootScanRegisterMgr() {
        this.routes = null;
        this.optypeMgr = new AbstractOptypeMgr() {
            @Override
            public void registe() {

            }
        };
    }

    public void registRoute(Routes routes) {
    	//sqlTemplates.add("sql/sqlserver/oa.sql");
    	//sqlTemplates.add("sql/sqlserver/poolAcc_cfm.sql");
    	//sqlTemplates.add("sql/sqlserver/poolAcc.sql");
    	//sqlTemplates.add("sql/sqlserver/common_cfm.sql");
    	//sqlTemplates.add("sql/sqlserver/account_cfm.sql");
    	//sqlTemplates.add("sql/sqlserver/workflow_cfm.sql");
    	//sqlTemplates.add("sql/sqlserver/wfprocess.sql");

        this.routes = routes;
        init();
    }


    private void init() {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String strFile = CommKit.convertPkg2Path(base_pkg_name);
            Enumeration<URL> urls = loader.getResources(strFile);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    String pkgPath = url.getPath();
                    if ("file".equals(protocol)) {
                        processFile(base_pkg_name, pkgPath);
                    } else if ("jar".equals(protocol)) {
                        processJar(url, strFile);
                    } else if ("wsjar".equals(protocol)) {
                        //针对于was liberty 的处理
                        logger.debug("url toString() is :" + url.toString());
                        String jarPath = url.toString().substring(2);
                        logger.debug("jarPath is:" + jarPath);
                        URL jarURL = new URL(jarPath);
                        processJar(jarURL, strFile);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processFile(String basePkgName, String pkgPath) throws Exception {
        if (basePkgName == null) {
            return;
        }
        // 接收 .class 文件 或 类文件夹
        File[] files = new File(pkgPath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return fileNameValidate(file) || file.isDirectory();
            }
        });
        for (File file : files) {
            if (file.isFile()) {
                String clz_name = getConstantClassName(basePkgName, file.getName());
                judgeClz(clz_name);
            } else {
                processFile(basePkgName + "." + file.getName(), pkgPath + File.separator + file.getName());
            }
        }
    }


    /**
     * 校验文件名是否为Routes.class和OptypeMrg.class结尾
     *
     * @param file
     * @return
     */
    private boolean fileNameValidate(File file) {
        if (file != null) {
            if (file.isFile()) {
                return fileNameValidate(file.getName());
            }
        }
        return false;
    }

    /**
     * 校验文件名是否为Routes.class和OptypeMrg.class结尾
     *
     * @param filename
     * @return
     */
    private boolean fileNameValidate(String filename) {
        if (filename != null && !"".endsWith(filename)) {
            return filename.endsWith(routeSufix) || filename.endsWith(optypeSufix) || filename.endsWith(workBookSufix) || filename.endsWith(sqlTemplateSufix);
        }
        return false;
    }


    private String getConstantClassName(String pkgName, String fileName) {
        int endIndex = fileName.lastIndexOf(".");
        return endIndex > 0 ? pkgName + "." + fileName.substring(0, endIndex) : "";
    }


    private void processJar(URL url, String strFile) throws Exception {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                //如果是以/开头的
                if (name.charAt(0) == '/') {
                    //获取后面的字符串
                    name = name.substring(1);
                }
                //对于SQL模板的特殊处理
                if (name.startsWith(sqlTemplaterPrefix) && fileNameValidate(name)) {
                    logger.debug("add sql template: "+name);
                    sqlTemplates.add(name);
                } else if (name.startsWith(strFile) && fileNameValidate(name)) {
                    int idx = name.lastIndexOf('/');
                    String packagnName = CommKit.convertPath2Pkg(name.substring(0, idx));
                    String className = packagnName + "." + name.substring(idx + 1, name.length() - 6);
                    judgeClz(className);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void judgeClz(String clz_name) {
        logger.debug("Enter into judgeClz(String " + clz_name + ")...");
        Class clz = CommKit.getClz(clz_name);
        if (!CommKit.isNullOrEmpty(clz)) {
            if (Routes.class.isAssignableFrom(clz)) {
                try {
                    this.routes.add((Routes) clz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (AbstractOptypeMgr.class.isAssignableFrom(clz) && AbstractOptypeMgr.class != clz) {
                try {
                    this.optypeMgr.add((AbstractOptypeMgr) clz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (AbstractWorkBook.class.isAssignableFrom(clz) && AbstractWorkBook.class != clz) {
                try {
                    AbstractWorkBook.registerWorkBook((AbstractWorkBook) clz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.debug("Leave judgeClz(String " + clz_name + ")");
        }
    }


    public AbstractOptypeMgr getOptypeMgr() {
        return optypeMgr;
    }


    public List<String> getSqlTemplates() {
        return sqlTemplates;
    }
}
