package com.qhjf.cfm.web.config;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.bankinterface.api.utils.LoadAtomicInterfaceUtils;
import com.qhjf.cfm.excel.config.load.ILoadSheetConfigUtil;
import com.qhjf.cfm.excel.config.load.impl.LoadSheetConfigUtil;
import com.qhjf.cfm.excel.config.parse.ICellConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.IColumnConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.impl.CellConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.impl.ColumnConfigParseUtil;
import com.qhjf.cfm.queue.QueuePlugin;
import com.qhjf.cfm.utils.ElectronicTemplateTool;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.BootScanRegisterMgr;
import com.qhjf.cfm.web.controller.GlobalController;
import com.qhjf.cfm.web.dialect.MySqlServerDialect;
import com.qhjf.cfm.web.handler.CFSHandler;
import com.qhjf.cfm.web.handler.XssHandler;
import com.qhjf.cfm.web.interceptor.DatagramInterceptor;
import com.qhjf.cfm.web.json.CFMMixedJsonFactory;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.plugins.jwt.AuthInterceptor;
import com.qhjf.cfm.web.plugins.jwt.JwtKit;
import com.qhjf.cfm.web.plugins.jwt.JwtTokenPlugin;
import com.qhjf.cfm.web.plugins.log.LogbackLogFactory;
import com.qhjf.cfm.web.quartzs.config.MyQuartzPlugin;
import com.qhjf.cfm.web.services.UserLoginService;
import com.qhjf.cfm.web.webservice.ebs.queue.EbsQueuePlugin;
import com.qhjf.cfm.web.webservice.la.queue.LaQueuePlugin;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvQueuePlugin;
import com.qhjf.cfm.web.webservice.oa.server.processQueue.WebServiceQueuePlugin;
import com.qhjf.cfm.workflow.api.WfApprovePermissionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFMAppConfig extends JFinalConfig {

    private static final GlobalConfigSection iniMgr = GlobalConfigSection.getInstance();
    private static Logger log = LoggerFactory.getLogger(CFMAppConfig.class);
    /*private SocketServer socketServer = null;
    private PreSocketServer preSocketServer = null;*/

    private RedisPlugin cfmRedis = null;
    private static final BootScanRegisterMgr bootScanRegisterMgr = BootScanRegisterMgr.getInstance();

    public static void main(String[] args) {
        JFinal.start("cfm_web/src/main/webapp", 8080, "/cfm");
    }

    @Override
    public void configConstant(Constants constants) {
        log.debug("Enter into configConstant(Constants constants)  .....");
        constants.setDevMode(true);
        constants.setEncoding("utf-8");
        constants.setJsonFactory(CFMMixedJsonFactory.me());
        constants.setLogFactory(new LogbackLogFactory());
        log.debug("Finsh configConstant(Constants constants)");
    }

    @Override
    public void configRoute(Routes routes) {
        log.debug("Enter into configRoute(Routes routes)  .....");
        routes.add("/", GlobalController.class);
        bootScanRegisterMgr.registRoute(routes);
        log.debug("Finsh configRoute(Routes routes) !");

    }

    @Override
    public void configEngine(Engine engine) {
        log.debug("Enter into configEngine(Engine engine)  .....");


        log.debug("Finsh configEngine(Engine engine) !");

    }

    @Override
    public void configPlugin(Plugins plugins) {
        log.debug("Enter into configPlugin(Plugins plugins)  .....");
        log.debug("dburl is: {}" , StringKit.removeControlCharacter(iniMgr.getDbURL()));
        DruidPlugin dp = new DruidPlugin(iniMgr.getDbURL(), iniMgr.getDbUser(), iniMgr.getDbPwd());
        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        slf4jLogFilter.setConnectionLogEnabled(false);
        slf4jLogFilter.setStatementLogEnabled(true);
        slf4jLogFilter.setStatementLogErrorEnabled(true);
        slf4jLogFilter.setResultSetLogEnabled(false);
        dp.addFilter(slf4jLogFilter);
        plugins.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setShowSql(true);
        if (iniMgr.dbType().equals("sqlserver")) {
            arp.setDialect(new MySqlServerDialect());
        } else if (iniMgr.dbType().equals("mysql")) {
            arp.setDialect(new MysqlDialect());
        }
        arp.getEngine().setDevMode(true);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());

        //加载自身的sql template
        arp.addSqlTemplate("sql/" + iniMgr.dbType() + "/all_cfm.sql");
        //加载其他模块的sql template
        for (String sqlTemplate : bootScanRegisterMgr.getSqlTemplates()) {
            log.info("arp.addSqlTemplate({})",StringKit.removeControlCharacter(sqlTemplate));
            arp.addSqlTemplate(sqlTemplate);
        }


        plugins.add(arp);

        
        log.debug("添加reids plugin！");
        if (iniMgr.hasConfig(IConfigSectionType.DefaultConfigSectionType.Redis)) {
            RedisCacheConfigSection redisSection = iniMgr.getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
            cfmRedis = new CfmRedisPlugin(redisSection.getCacheName(), redisSection.getIp());
            cfmRedis.setSerializer(new JdkSerializer());
            plugins.add(cfmRedis);
        }


        log.debug("添加Jwtplugin！");
        plugins.add(new JwtTokenPlugin(UserLoginService.me));


        if(CFMQuartzConfigSection.getInstance().isQuartzOpen()){
        	log.debug("添加quartz！");
        //    plugins.add(MyQuartzPlugin.getInstance());
        }
        

        log.debug("添加消费者队列插件！");

        plugins.add(new QueuePlugin());
        plugins.add(new LaQueuePlugin());
        plugins.add(new EbsQueuePlugin());
        plugins.add(new LaRecvQueuePlugin());


        if(iniMgr.hasConfig(IConfigSectionType.DDHConfigSectionType.DDHOAWS)){
            plugins.add(new WebServiceQueuePlugin());
        }
        

        if(iniMgr.hasConfig(IConfigSectionType.DDHConfigSectionType.DDHNCWS)){
            plugins.add(new com.qhjf.cfm.web.webservice.nc.server.processQueue.WebServiceQueuePlugin());
        }
        log.debug("Finsh configPlugin(Plugins plugins) !");
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        log.debug("Enter into configInterceptor(Routes routes)  .....");
        interceptors.add(new DatagramInterceptor());
        interceptors.add(new AuthInterceptor());
        log.debug("Finsh configInterceptor(Routes routes) !");
    }

    @Override
    public void configHandler(Handlers handlers) {
        log.debug("Enter into configHandler(Routes routes)  .....");
        handlers.add(new XssHandler(" "));
        handlers.add(new CFSHandler());
        handlers.add(new UrlSkipHandler(".*/services.*", false));
        log.debug("Finsh configHandler(Routes routes) !");
    }

    public void afterJFinalStart() {
/*    	String address = "http://127.0.0.1:9090/oa";
        Endpoint.publish(address, new OAServer());*/
        
        LoadAtomicInterfaceUtils.getInstance();
        if (iniMgr.hasConfig(IConfigSectionType.DefaultConfigSectionType.Redis)) {
            JwtKit.setMode(JwtKit.Mode.REDIS);
        }

        //加载Excel校验配置信息
        loadExcelConfig();
        WfApprovePermissionTool.getINSTANCE();
        //电子回单模版
        ElectronicTemplateTool.getINSTANCE();
        
        

    }

    @Override
    public void beforeJFinalStop() {
        if (null != cfmRedis) cfmRedis.stop();
    }

    /**
     * 加载Excel配置文件
     */
    private void loadExcelConfig() {
        ILoadSheetConfigUtil loader = LoadSheetConfigUtil.getInstance();
        String rootClassPath = PathKit.getRootClassPath();//获取根路径
        IColumnConfigParseUtil columnParser = new ColumnConfigParseUtil();//获取列配置解析器
        ICellConfigParseUtil cellParser = new CellConfigParseUtil();//获取单元格配置解析器
        loader.loadconfig(rootClassPath, columnParser, cellParser);//解析Excel配置文件
    }

}
