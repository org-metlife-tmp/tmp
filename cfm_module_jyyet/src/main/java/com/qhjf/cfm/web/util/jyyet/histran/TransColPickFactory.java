package com.qhjf.cfm.web.util.jyyet.histran;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.IPlugin;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommKit;

/**
 * 从不同银行的历史交易导入模板中抽取 acc_his_transaction需要的字段： acc_no，
 * opp_acc_no，opp_acc_name，opp_acc_bank， direction， amount，
 * trans_date，trans_time， summary，post_script
 * 
 * @author CHT
 *
 */
public class TransColPickFactory {
	private static Logger log = LoggerFactory.getLogger(TransColPickFactory.class);
	private static List<ATransColPickStrategy> strategyList = new ArrayList<>();

	private TransColPickFactory() {
		findClasses(ATransColPickStrategy.class);
	}

	public ATransColPickStrategy getStrategyByPk(int pk) throws ReqDataException {
		ATransColPickStrategy result = null;
		for (ATransColPickStrategy strategy : strategyList) {
			if (strategy.getPk() == pk) {
				result = strategy;
			}
		}
		if (null == result) {
			throw new ReqDataException("银行历史交易导入模板，不支持pk=" + pk);
		}
		return result;
	}

	private void findClasses(Class<?> clazz) {
		List<Class<?>> allClasses = new ArrayList<Class<?>>();

		String packageName = clazz.getPackage().getName();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> enumeration = null;
		try {
			enumeration = classLoader.getResources(CommKit.convertPkg2Path(packageName));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();
			String protocol = url.getProtocol();
			String path = url.getPath();
			log.debug("加载文件：{}-{}", protocol, path);
			if ("file".equals(protocol)) {
				allClasses.addAll(findResources(clazz, new File(path), packageName));
			} else if ("jar".equals(protocol)) {
				allClasses.addAll(findJarResource(clazz, url, packageName));
			} else if ("wsjar".equals(protocol)) {
                //针对于was liberty 的处理
                log.debug("wsjar url toString() is :" + url.toString());
                String jarPath = url.toString().substring(2);
                log.debug("wsjar sub jarPath is:" + jarPath);
                try {
					URL jarURL = new URL(jarPath);
					allClasses.addAll(findJarResource(clazz, jarURL, packageName));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
            }

		}

		newInstance(allClasses);
	}

	@SuppressWarnings("unchecked")
	private static List<Class<?>> findResources(Class<?> clazz, File dir, String parentPath) {

		List<Class<?>> result = new ArrayList<Class<?>>();
		if (!dir.exists()) {
			return Collections.EMPTY_LIST;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (!file.getName().contains(".")) {
					result.addAll(findResources(clazz, file, parentPath.concat(".").concat(file.getName())));
				}
			} else if (file.getName().endsWith(".class")) {
				Class<?> c = null;
				try {
					c = Class.forName(
							parentPath.concat(".").concat(file.getName().substring(0, file.getName().length() - 6)));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if (clazz.isAssignableFrom(c) && !clazz.equals(c)) {
					result.add(c);
				}
			}
		}
		return result;
	}

	private static List<Class<?>> findJarResource(Class<?> clazz, URL url, String strFile) {
		String path = CommKit.convertPkg2Path(strFile);
		List<Class<?>> result = new ArrayList<Class<?>>();
		JarFile jar;
		try {
			jar = ((JarURLConnection) url.openConnection()).getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.charAt(0) == '/') {
					name = name.substring(1);
				}
				if (name.startsWith(path) && name.endsWith(".class")) {
					int idx = name.lastIndexOf('/');
					String packagnName = CommKit.convertPath2Pkg(name.substring(0, idx));
					String className = packagnName + "." + name.substring(idx + 1, name.length() - 6);

					Class<?> c = CommKit.getClz(className);
					
					if (clazz.isAssignableFrom(c) && !clazz.equals(c)) {
						result.add(c);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void newInstance(List<Class<?>> allClasses) {
		if (null != allClasses && allClasses.size() > 0) {
			for (Class<?> clazz : allClasses) {
				log.debug("实例化历史交易加载策略：{}", clazz.getName());
				try {
					strategyList.add((ATransColPickStrategy) clazz.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static TransColPickFactory getInstance() {
		return InnerClass.INSTANCE;
	}

	private static class InnerClass {
		public static final TransColPickFactory INSTANCE = new TransColPickFactory();
	}

	public static void main(String[] args) {
		TransColPickFactory.getInstance().findClasses(ATransColPickStrategy.class);
		System.out.println(TransColPickFactory.strategyList);
	}

}
