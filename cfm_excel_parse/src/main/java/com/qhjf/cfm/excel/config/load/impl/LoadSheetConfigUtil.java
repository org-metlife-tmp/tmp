package com.qhjf.cfm.excel.config.load.impl;

import com.qhjf.cfm.excel.config.CellConfig;
import com.qhjf.cfm.excel.config.ColumnConfig;
import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.config.load.ILoadSheetConfigUtil;
import com.qhjf.cfm.excel.config.parse.ICellConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.IColumnConfigParseUtil;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;
import com.qhjf.cfm.excel.util.KeyValueSplitUtil;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 加载/缓存Excel配置文件工具类
 * @author CHT
 *
 */
public class LoadSheetConfigUtil implements ILoadSheetConfigUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoadSheetConfigUtil.class);
	private LoadSheetConfigUtil(){}
	/**
	 * 缓存所有Excel的配置文件
	 * key:配置文件PK
	 * value:excel配置信息
	 */
	private static Map<String, ExcelSheetConfig> excelSheetConfigMap = new HashMap<>();
	
    public void loadconfig(String rootClassPath, IColumnConfigParseUtil columnParser, ICellConfigParseUtil cellParser) {
    	File excelDir = new File(rootClassPath, "/excel");
    	if (!excelDir.exists() || !excelDir.isDirectory()) {
			logger.error(BasicTypeConstant.SPLIT_LINE);
			logger.error("************Excel配置文件目录不存在,请在resources目录新建excel文件夹**************");
			logger.error(BasicTypeConstant.SPLIT_LINE);
			return;
		}
    	File[] excelConfigs = getFiles(excelDir);
    	
    	for (File file : excelConfigs) {
    		//解析excel配置文件
			ExcelSheetConfig excelSheetConfig = resolveExcelConfigFile(file, columnParser, cellParser);
			if (null != excelSheetConfig && excelSheetConfig.getPk() != null) {
				excelSheetConfigMap.put(excelSheetConfig.getPk(), excelSheetConfig);
			}else {
				logger.error(BasicTypeConstant.SPLIT_LINE);
				logger.error("加载Excel配置文件失败，请修改正确再次启动应用程序");
				logger.error(BasicTypeConstant.SPLIT_LINE);
				return;
			}
		}

        for (String key : excelSheetConfigMap.keySet()) {
            logger.info("Excel配置信息：{}：{}", key, excelSheetConfigMap.get(key));
        }
    }
    /**
     * 提取Excel配置文件路径里的文件
     * @param excelDir
     * @return
     */
    private File[] getFiles(File excelDir){
    	File[] listFiles = excelDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isFile()) {
					return true;
				}
				return false;
			}
		});
    	return listFiles;
    }
    /**
     * 解析Excel配置文件
     * @param configFile Excel配置文件
     * @return
     */
    public ExcelSheetConfig resolveExcelConfigFile(File configFile, IColumnConfigParseUtil columnParser, ICellConfigParseUtil cellParser) {
    	logger.debug("开始加载Excel配置文件：{}",configFile.getAbsolutePath());
    	InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		ExcelSheetConfig excelSheetConfig = new ExcelSheetConfig(0);//默认加载Excel的第一个sheet页
		List<Config> columnConfigs = new ArrayList<>();
		Map<String, Config> cellConfigs = new HashMap<>();
		BufferedReader bufferedReader = null;
		String buffer = "";
		int line = 0;
		try {
			inputStream = new FileInputStream(configFile);
			inputStreamReader = new InputStreamReader(inputStream, BasicTypeConstant.DEFAULT_ENCODE);
			bufferedReader = new BufferedReader(inputStreamReader);
			//一行一行解析配置文件
			while((buffer = bufferedReader.readLine()) != null) {
				logger.debug(String.format("%d	%s, [%s]", ++line, buffer, (buffer.trim().length())));
				if(buffer.trim().length()==0) {
					logger.debug(String.format("*line:%d 为空白行", line));
					continue;
				}
				if(buffer.trim().startsWith("##")) {
					logger.debug(String.format("*line:%d 为注释行", line));
					continue;
				}
				
				String[] kv = KeyValueSplitUtil.splitEqualSign(buffer);
				String value = kv[1];
				//解析pk
				if("pk".equals(kv[0].toLowerCase())) {
					if (StringUtils.isBlank(value)) {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】配置项pk为空", configFile.getName());
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
					excelSheetConfig.setPk(value);
					continue;
				}
				//解析templateId
				if("templateid".equals(kv[0].toLowerCase())) {
					if (!StringUtils.isBlank(value)) {
						logger.info(String.format("【excel配置文件解析】配置文件名：%s", configFile.getName()));
						logger.info(String.format("【excel配置文件解析】读取到[templateId]=%s", value));
						String templateIdMd5 = MD5Kit.string2MD5(value);
						excelSheetConfig.setTemplateId(templateIdMd5);
					}
					continue;
				}
				//解析startRow
				if("startrow".equals(kv[0].toLowerCase())) {
					if(StringUtils.isNumeric(value)) {
						excelSheetConfig.setStartRow(Integer.parseInt(value));
						continue;
					} else {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】配置项startRow配置信息错误", configFile.getName());
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
				}
				//解析startColumn
				if("startcolumn".equals(kv[0].toLowerCase())) {
					if(StringUtils.isNumeric(value)) {
						excelSheetConfig.setStartColumn(Integer.parseInt(value));
						continue;
					} else {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】配置项startColumn配置信息错误", configFile.getName());
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
				}
				//解析excelDataType
				if("exceldatatype".equals(kv[0].toLowerCase())) {
					if(StringUtils.isNumeric(value)) {
						excelSheetConfig.setExcelDataType(Integer.parseInt(value));
						continue;
					} else {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】配置项excelDataType配置信息错误", configFile.getName());
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
				}
				//解析sheetNumber
				if("sheetnumber".equals(kv[0].toLowerCase())) {
					if (StringUtils.isNumeric(value)) {
						excelSheetConfig.setSheetNumber(Integer.parseInt(value));
					}
					continue;
				}
				//解析sheetName
				if("sheetname".equals(kv[0].toLowerCase())) {
					if (StringUtils.isNumeric(value)) {
						excelSheetConfig.setSheetName(value);
					}
					continue;
				}
				//列配置
				if ("[columnconfigs]".equals(kv[0].toLowerCase())) {
					if (StringUtils.isBlank(kv[1])) {
						continue;
					}
					ColumnConfig columnConfig = null;
					try {
						columnConfig = columnParser.parse(kv[1]);
					} catch (ExcelConfigParseException e) {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】第{}列配置信息解析错误:{}", configFile.getName(), columnConfigs.size()+1, e.getErrorMessage());
						e.printStackTrace();
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
					columnConfigs.add(columnConfig);
					continue;
				}
				//单元格配置
				if ("[cellconfigs]".equals(kv[0].toLowerCase())) {
					if (StringUtils.isBlank(kv[1])) {
						continue;
					}
					CellConfig cellconfig = null;
					try {
						cellconfig = cellParser.parse(kv[1]);
					} catch (ExcelConfigParseException e) {
						logger.error(BasicTypeConstant.SPLIT_LINE);
						logger.error("配置文件【{}】第{}个单元格配置信息解析错误:{}", configFile.getName(), cellConfigs.size()+1, e.getErrorMessage());
						e.printStackTrace();
						logger.error(BasicTypeConstant.SPLIT_LINE);
						return null;
					}
					cellConfigs.put(cellconfig.getPosition(), cellconfig);
					continue;
				}
			}
			
			excelSheetConfig.setCellConfigs(cellConfigs);
			excelSheetConfig.setColumnConfigs(columnConfigs);
			
			//保存在mongoDb中的文件名
			if (StringUtils.isBlank(excelSheetConfig.getTemplateId())) {
				String md5 = MD5Kit.string2MD5(configFile.getName());
				logger.info(String.format("【excel配置文件解析】配置文件名：%s", configFile.getName()));
				logger.info(String.format("【excel配置文件解析】未配置[templateId]，把配文件文件名的Md5赋给templateId = %s", md5));
				excelSheetConfig.setTemplateId(md5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		return excelSheetConfig;
	}

	public static Map<String, ExcelSheetConfig> getExcelSheetConfigMap() {
		return excelSheetConfigMap;
	}

	public static void setExcelSheetConfigMap(Map<String, ExcelSheetConfig> excelSheetConfigMap) {
		LoadSheetConfigUtil.excelSheetConfigMap = excelSheetConfigMap;
	}
	
	public static LoadSheetConfigUtil getInstance(){
		return SingletonClass.INSTANCE;
	}
	private static class SingletonClass{
		private static final LoadSheetConfigUtil INSTANCE = new LoadSheetConfigUtil();
	}
}