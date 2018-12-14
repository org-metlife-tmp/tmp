package com.qhjf.cfm.excel.analyze.validator.impl;

import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.adapter.POIWorkbookVersionAdapter;
import com.qhjf.cfm.excel.analyze.preprocess.PreProcessLog;
import com.qhjf.cfm.excel.analyze.preprocess.PreProcessUtil;
import com.qhjf.cfm.excel.analyze.validator.IValidatorService;
import com.qhjf.cfm.excel.analyze.validator.cell.CellValueGetterFactory;
import com.qhjf.cfm.excel.analyze.validator.cell.CellValueGetterStrategy;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.excel.bean.ResultBean;
import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.config.convertor.IConvertor;
import com.qhjf.cfm.excel.config.statistic.IStatistics;
import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.encrypt.ExcelEncryptFactory;
import com.qhjf.cfm.excel.encrypt.ExcelEncryptStrategy;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;
import com.qhjf.cfm.excel.exception.DisposeException;
import com.qhjf.cfm.excel.exception.StatisticsAssertionException;
import com.qhjf.cfm.excel.util.ExcelPositionTransUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomUtil;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.utils.comm.file.tool.FileTransToolFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * 默认Excel校验服务
 * 
 * @author CHT
 *
 */
public class DefaultValidatorService implements IValidatorService {
	private static final Logger logger = LoggerFactory.getLogger(DefaultValidatorService.class);
//	private final static String HSSF_WORK_BOOK_CLASS = HSSFWorkbook.class.getName();
//	private final static String XSSF_WORK_BOOK_CLASS = XSSFWorkbook.class.getName();
//	private final static String EXCEL2007_VERSION_TEXT = "EXCEL2007";
//	private final static String EXCEL97_VERSION_TEXT = "EXCEL97";
	private final static String INVALID_FORMAT = "文档格式错误";
	private final static String TRANSFER_ERROR = "数据传输错误";
	private final static String EMPTY_DATA = "excel文档无数据";
	private final static String INVALID_CONTENT = "文档内容不符合要求";
	private final static String ENCRYPTED_DOCUMENT = "文档已经被加密";
//	private final static String FORMAT_ERROR_TIPS = "格式错误,请在%s列填写%s数据";
	private final static String CELL_FORMAT_ERROR = "单元格类型无法处理";
	private final static String EXCEL_PARSE_SUCCESS = "文档解析完成";
//	private final static String NOT_ALLOWED_CELL_FORMAT_TIPS = "\"%s\" 列格式错误,系统不能使用公式类型";
//	private final static String NOT_BLANK = "单元格不能为空";

//	private final static String BOOLEAN_CLASSNAME = java.lang.Boolean.class.getName();
//	private final static String STRING_CLASSANME = java.lang.String.class.getName();
//	private final static String INTEGER_CLASSANME = java.lang.Integer.class.getName();
//	private final static String DOUBLE_CLASSANME = java.lang.Double.class.getName();
//	private final static String FLOAT_CLASSANME = java.lang.Float.class.getName();
//	private final static String SHORT_CLASSANME = java.lang.Short.class.getName();
//	private final static String BYTE_CLASSANME = java.lang.Byte.class.getName();
//	private final static String CHAR_CLASSANME = java.lang.Character.class.getName();
//	private final static String LONG_CLASSANME = java.lang.Long.class.getName();
//	private final static String DATE_CLASSANME = java.util.Date.class.getName();

//	private final static Map<String, String> TIPS_MAP = new HashMap<String, String>();
//	private final static Map<String, String> VERSION_MAP = new HashMap<>();

	private static RedisCacheConfigSection configSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

	private POIWorkbookVersionAdapter workbookVersionAdapter;
	private Map<Integer, List<Object>> statisticMap;
	private ExcelSheetConfig sheetConfig;
	private List<Map<String, Object>> result;// Excel解析结果：规则行列数据
	private Map<String, Object> cellData;//Excel解析结果：不规则单元格
	private ResultBean resultBean;
	private Workbook workbook;
	private boolean marked;
	private Sheet sheet;

	/*
	 * 类加载阶段进行初始化工作 完成 1 - WorkBook接口的具体适配 2 - 单元格类型与java基本数据类型的适配 3 -
	 * 提示信息的数据类型适配
	 */
//	static {
//		VERSION_MAP.put(HSSF_WORK_BOOK_CLASS, EXCEL97_VERSION_TEXT);
//		VERSION_MAP.put(XSSF_WORK_BOOK_CLASS, EXCEL2007_VERSION_TEXT);

//		TIPS_MAP.put(STRING_CLASSANME, "文本类型");
//		TIPS_MAP.put(BOOLEAN_CLASSNAME, "逻辑类型");
//		TIPS_MAP.put(DATE_CLASSANME, "日期类型");
//		TIPS_MAP.put(INTEGER_CLASSANME, "数字类型");
//		TIPS_MAP.put(DOUBLE_CLASSANME, "浮点数类型");
//		TIPS_MAP.put(FLOAT_CLASSANME, "浮点数类型");
//		TIPS_MAP.put(LONG_CLASSANME, "数字类型");
//	}

	@Override
	public ResultBean doValidate(UploadFileScaffold ufs, ExcelSheetConfig sheetConfig) throws StatisticsAssertionException, DisposeException {
		logger.debug("开始校验excel：{}",ufs.getFilename());
		// 创建Excel对象
		this.sheetConfig = sheetConfig;
		this.resultBean = new ResultBean();
		this.resultBean.setSuccess(true);
		ByteArrayInputStream source = new ByteArrayInputStream(ufs.getContent());
		try {
			//使用WorkbookFactory.create 创建Excel，兼容xls与xlsx的创建
			this.workbook = WorkbookFactory.create(source);
		} catch (EncryptedDocumentException e) {
			resultBean.setMessage(ENCRYPTED_DOCUMENT);
			resultBean.setSuccess(false);
			return this.resultBean;
		} catch (InvalidFormatException e) {
			resultBean.setMessage(INVALID_FORMAT);
			resultBean.setSuccess(false);
			return this.resultBean;
		} catch (IOException e) {
			resultBean.setMessage(TRANSFER_ERROR);
			resultBean.setSuccess(false);
			return this.resultBean;
		}
		this.workbookVersionAdapter = POIWorkbookVersionAdapter.adapt(this.workbook);
		this.resultBean.setMessage(String.format("已经读取到%s版本的excel文档", workbookVersionAdapter.getVersionText()));

		
		//判断是否对上传的Excel加密
    	Map<String, String> params = ufs.getParams();
    	if (params.containsKey("is_encrypt") && "1".equals(params.get("is_encrypt"))) {
    		String password = ufs.getParams().get("password");
    		if (StringUtils.isBlank(password)) {
				this.marked = true;
				this.resultBean.setMessage("Excel加密密码为空");
				this.resultBean.setSuccess(false);
				this.resultBean.setContent(null);
				return this.resultBean;
			}
    		ExcelEncryptStrategy encryptor = ExcelEncryptFactory.createEncryptor(this.workbookVersionAdapter.getVersionClass());
    		byte[] clone = Arrays.clone(ufs.getContent());
    		byte[] encrypteContent = encryptor.encryptExcel(clone, password);
//    		String encryptObjectId = MongoGridFSTool.getInstance().addNewFileToGridFS(MD5Kit.byteArrayToMD5(encrypteContent), encrypteContent);
    		String encryptObjectId = null;
			try {
				encryptObjectId = FileTransToolFactory.getInstance()
						.addNewFileByArray(MD5Kit.byteArrayToMD5(encrypteContent).concat(RandomUtil.currentTimeStamp()), encrypteContent);
			} catch (BusinessException e) {
				e.printStackTrace();
				this.marked = true;
				this.resultBean.setMessage(BasicTypeConstant.ENCRYPT_EXCEL_SAVE_FAIL);
				this.resultBean.setSuccess(false);
				this.resultBean.setContent(null);
				return this.resultBean;
			}
    		this.resultBean.setEncryptObjectId(encryptObjectId);
		}
    	
    	
		result = new ArrayList<>();
		cellData = new HashMap<>();

		// 创建Excel的sheet页对象：优先以sheet页名字创建
		String sheetName = this.sheetConfig.getSheetName();
		if (StringUtils.isBlank(sheetName)) {
			this.sheet = this.workbook.getSheetAt(this.sheetConfig.getSheetNumber());
		} else {
			this.sheet = this.workbook.getSheet(this.sheetConfig.getSheetName());
		}

		//对Excel进行预处理
		PreProcessLog preProcessLog = PreProcessUtil.proImport(this.sheetConfig, this.sheet);
		logger.debug("重置单元格:{};填充空单元格:{};删除批注:{}", preProcessLog.getResetCell(), preProcessLog.getBlankCell(),
				preProcessLog.getCommentCell());

		
		// 读取每个单元格并校验
		executeHandle();
		logger.info("excel校验结束，结果：{}", this.resultBean);

		
		if (marked) { // 解析过程中发现错误,单元格被标记
			ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream();
			try {
				//把excel数据写入到输出流
				this.workbook.write(resultOutputStream);
				this.resultBean.setSuccess(false);
				this.resultBean.setMessage(INVALID_CONTENT);
				this.resultBean.setContent(resultOutputStream.toByteArray());
			} catch (IOException e) {
				this.resultBean.setSuccess(false);
				this.resultBean.setMessage(TRANSFER_ERROR);
				this.resultBean.setContent(null);
			}
		} else { // 解析没问题,将结果序列化后返回给用户
			if (result.size() < 1) { // 如果解析的结果是0则认为是失败的
				this.resultBean.setMessage(EMPTY_DATA);
				this.resultBean.setSuccess(false);
				this.resultBean.setContent(null);
				return this.resultBean;
			}

			ByteArrayOutputStream target = new ByteArrayOutputStream();
			byte[] objectContent = new byte[0];
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(target);
				out.writeObject(result);
				out.flush();
				objectContent = target.toByteArray();
				this.resultBean.setSuccess(true);
				this.resultBean.setContent(objectContent);
				this.resultBean.setMessage(EXCEL_PARSE_SUCCESS);
				this.resultBean.setSheetName(this.sheetConfig.getSheetName());
				
				//将解析结果存入Redis中
				ExcelResultBean excelResultBean = new ExcelResultBean(sheetConfig.getExcelDataType(), result, cellData);
				String redisDb = configSection.getCacheName();
				Redis.use(redisDb).setex(ufs.getContentMd5(), 10 * 60, excelResultBean);
				logger.debug("excel解析结果存入redis：【{}：{}】", ufs.getContentMd5(), excelResultBean);
			} catch (IOException e) {
				this.resultBean.setSuccess(false);
				this.resultBean.setMessage(TRANSFER_ERROR);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (target != null) {
					try {
						target.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return this.resultBean;
	}

	// 核心处理
	private void executeHandle() throws StatisticsAssertionException, DisposeException {
		//excel行列校验
		List<Config> columnConfigs = this.sheetConfig.getColumnConfigs();
		if (null != columnConfigs && !columnConfigs.isEmpty()) {
			columnHandle();
		}
		
		//excel单元格校验
		Map<String, Config> cellConfigs = this.sheetConfig.getCellConfigs();
		if(null != cellConfigs && !cellConfigs.isEmpty()){
			cellHandle();
		}
		
	}
	
	/**
	 * Excel规范行列校验
	 * @throws DisposeException 
	 * @throws StatisticsAssertionException 
	 */
	private void columnHandle() throws DisposeException, StatisticsAssertionException{
		int colNumber = this.sheetConfig.getStartColumn();
		int rowNumber = this.sheetConfig.getStartRow();
		
		// 首先循环配置信息，如果存在统计信息则填充statisticMap
		int configItemIndex = 0;
		statisticMap = new HashMap<>();
		for (Config item : this.sheetConfig.getColumnConfigs()) {
			if (item.getStatistics() != null) {
				List<Object> contents = new ArrayList<>(0);
				statisticMap.put(configItemIndex, contents);
			}
			++configItemIndex;
		}
		
		List<IValidator> validatorList = null;
		IConvertor convertor = null;
		marked = false;
		
		List<Config> configList = this.sheetConfig.getColumnConfigs();
		int sheetLastRowNum = this.sheet.getLastRowNum();
		int sheetLastColNum = configList.size();
		
		//行遍历
		for (int index = rowNumber; index <= sheetLastRowNum; index++) {
			
			Row row = this.sheet.getRow(index);// 获取行
			//一行数据放在map中，key为数据库字段，value为值
			Map<String, Object> items = new HashMap<>();
			if (null != row) {
				//列遍历,itemIndex结束条件控制，单元格序号
				for (int colIndex = colNumber, itemIndex = 0; itemIndex < sheetLastColNum;  itemIndex++, colIndex++) {
					
					Object cellValue = null;
					//获取excel当前列的配置对象
					Config item = configList.get(itemIndex);
					//获取Excel单元格对象
					Cell cell = row.getCell(colIndex);
					
					//获取excel单元格 取值策略对象
					CellValueGetterStrategy strategy = CellValueGetterFactory.createStrategy(cell);
					//获取单元格的值
					Object originalCellValue = null;
					try {
						cellValue = strategy.getCellValue(item, cell);
						originalCellValue = strategy.getCellValue(item, cell);
					} catch (CellValueUndesirableException e1) {
						doCellComment(cell, e1.getErrorMessage());
						continue;
					}
					
					//校验单元格的值
					validatorList = item.getValidatorList();
					if (null != validatorList) {
						if (validate(validatorList, cell, originalCellValue)) {
							break;
						}
					}
					
					//转换单元格的值
					convertor = item.getConvertor();
					if (null != convertor) {
						cellValue = convertor.convert(originalCellValue);
					}
					
					//统计单元格的值
					if (null != item.getStatistics()) {
						List<Object> target = statisticMap.get(itemIndex);
						if (target == null) {
							target = new ArrayList<>();
							target.add(originalCellValue);
							statisticMap.put(itemIndex, target);
						} else {
							target.add(originalCellValue);
						}
					}
					
					// 补偿一次,尝试强行用string类型进行处理
					if (item.isRequired() && cellValue == null) {
						try {
							cellValue = cell.getStringCellValue();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							// 如果还是空,则抛异常
							if (item.isRequired() && cellValue == null) {
								throw new DisposeException(CELL_FORMAT_ERROR);
							}
						}
					}
					
					items.put(item.getDbColumnName(), cellValue);
				}
			}
			result.add(items);
		}
		
		int sItemIndex = 0;
		if (null != configList) {
			for (Config item : configList) {
				if (null != item.getStatistics()) {
					IStatistics statistics = item.getStatistics();
					Number value = statistics.doStatistic(statisticMap.get(sItemIndex));
					if (!statistics.compare(value)) {
						throw new StatisticsAssertionException(statistics.getErrorMessage());
					}
				}
				++sItemIndex;
			}
		}
		
	}
	
	/**
	 * Excel不规则的单个单元格校验
	 * @throws DisposeException 
	 * @throws StatisticsAssertionException 
	 */
	private void cellHandle() throws DisposeException, StatisticsAssertionException{
		//获取单元格校验规则
		Map<String, Config> cellConfigs = this.sheetConfig.getCellConfigs();
		Set<String> configKeySet = cellConfigs.keySet();
		
		Config config = null;
		
		for (String configKey : configKeySet) {
			//获取当前单元格的配置对象
			config = cellConfigs.get(configKey);
			
			Object cellValue = null;//单元格的值
			//excel坐标转换为行列序号
			int rowNum = ExcelPositionTransUtil.positionTransToRow(configKey);
			int columnNum = ExcelPositionTransUtil.positionTransToColumn(configKey);
			
			Row row = this.sheet.getRow(rowNum);
			Cell cell = row.getCell(columnNum);
			
			//获取excel单元格 取值策略对象
			CellValueGetterStrategy strategy = CellValueGetterFactory.createStrategy(cell);
			//获取单元格的值
			Object originalCellValue = null;
			try {
				cellValue = strategy.getCellValue(config, cell);
				originalCellValue = strategy.getCellValue(config, cell);
			} catch (CellValueUndesirableException e1) {
				doCellComment(cell, e1.getErrorMessage());
				continue;
			}
			
			//校验单元格的值
			List<IValidator> validatorList = config.getValidatorList();
			if (null != validatorList) {
				if (validate(validatorList, cell, originalCellValue)) {
					continue;
				}
			}
			
			//转换单元格的值
			IConvertor convertor = config.getConvertor();
			if (null != convertor) {
				cellValue = convertor.convert(originalCellValue);
			}
			
			//统计比较
			IStatistics statistics = config.getStatistics();
			if (null != statistics) {
				List<Object> list = new ArrayList<Object>();
				list.add(originalCellValue);
				Number staRes = statistics.doStatistic(list);
				if (!statistics.compare(staRes)) {
					throw new StatisticsAssertionException(statistics.getErrorMessage());
				}
			}
			
			cellData.put(configKey.toUpperCase(), cellValue);
		}
		
	}
	
	/**
	 * 校验单元格的值
	 * @param validatorList	校验器列表
	 * @param cell	Excel单元格
	 * @param cellValue	Excel单元格的值
	 * @return
	 */
	private boolean validate(List<IValidator> validatorList, Cell cell, Object cellValue){
		boolean validatorFlag = false;
		for (IValidator validator : validatorList) {
			if (null != validator && !validator.doValidat(cellValue, cell)) {//校验单元格数据
				doCellComment(cell, validator.getErrorMessage());//给单元格做标记
				validatorFlag = true;
				break;
			}
		}
		return validatorFlag;
	}

	/**
	 * 给单元格作标记
	 */
	private void doCellComment(Cell cell, String text) {
		ClientAnchor anchor = this.workbookVersionAdapter.createClientAnchor();
		anchor.setDx1(0);
		anchor.setDx2(0);
		anchor.setDy1(0);
		anchor.setDy2(0);
		anchor.setCol1(0);
		anchor.setRow1(0);
		anchor.setCol2(0);
		anchor.setRow2(0);
		Drawing<?> drawing = this.sheet.createDrawingPatriarch();
		Comment comment = drawing.createCellComment(anchor);
		RichTextString richText = this.workbookVersionAdapter.createRichTextString(text);
		comment.setString(richText);
		cell.setCellComment(comment);
		this.marked = true;
	}
}
