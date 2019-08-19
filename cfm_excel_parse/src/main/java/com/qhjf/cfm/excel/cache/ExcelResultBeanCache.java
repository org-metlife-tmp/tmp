package com.qhjf.cfm.excel.cache;

import com.jfinal.log.Log;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存Excel校验成功后的结果工具类
 * 该类已过期，已把解析结果放入redis缓存
 * @author CHT
 *
 */
@Deprecated
public class ExcelResultBeanCache {
	private final static Log logger = LogbackLog.getLog(ExcelResultBeanCache.class);
	/**
	 * excel解析结果的缓存
	 * key:excel流的byte数组的MD5
	 * value：excel的校验成功后的解析结果
	 */
	private static Map<String, ExcelResultBean> cache = new ConcurrentHashMap<>();
	private ExcelResultBeanCache(){
		startTimer();
	}
	/**
	 * 获取单例
	 * @return
	 */
	public static ExcelResultBeanCache getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/**
	 * 实现单例
	 * @author CHT
	 */
	private static class SingletonHolder {
        private static final ExcelResultBeanCache INSTANCE = new ExcelResultBeanCache();
    }
	/**
	 * 通过文件流的byte数组的MD5获取excel最终的解析结果
	 * @param contentMd5 文件流的byte数组的MD5
	 * @return
	 */
	public ExcelResultBean getExcelResultBean(String contentMd5){
		return cache.get(contentMd5);
	}
	public void saveExcelResultBean(String md5, ExcelResultBean bean){
		cache.put(md5, bean);
	}
	public void removeExcelResultBean(String md5){
		cache.remove(md5);
	}
	
	private void startTimer(){
		Timer timer = new Timer();
        //前一次执行程序结束后 720000ms 后开始执行下一次程序
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	logger.debug("清理超时的Excel上传解析结果的内存数据...start...");
            	List<String> deadBeanList = new ArrayList<String>();
            	long now  = new Date().getTime();
            	Set<String> keySet = cache.keySet();
            	//找到超时的bean
            	for (String md5 : keySet) {
            		ExcelResultBean bean = cache.get(md5);
            		long createTime = bean.getCreateTime();
            		if ((now - createTime) > BasicTypeConstant.SERVIVAL_TIME) {
            			deadBeanList.add(md5);
					}
				}
            	//删除超时的bean
            	for (String md5 : deadBeanList) {
            		cache.remove(md5);
				}
            	logger.debug("清理超时的Excel上传解析结果的内存数据...end...");
            }
        }, BasicTypeConstant.TIMER_DELAY_TIME, BasicTypeConstant.SERVIVAL_TIME);
	}
	
	public static void main(String[] args) {
		new ExcelResultBeanCache();
	}
	
}
