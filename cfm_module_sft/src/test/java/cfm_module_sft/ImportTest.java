package cfm_module_sft;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportTest {
	private static final Logger logger = LoggerFactory.getLogger(ImportTest.class);

	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void start() {
		dp = new DruidPlugin("jdbc:sqlserver://10.164.24.147:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/sysinter_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/sft_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
		dp.start();
		arp.start();
	}
	
	@Test
	public void findMasterByBatchNoTest() {
		//excel文件路径
		String excelPath = "D:\\Company\\工作安排\\2018-12-24 收付费\\资金平台通收付费生产配置0329.xlsx";

		try {
			//String encoding = "GBK";
			File excel = new File(excelPath);
			if (excel.isFile() && excel.exists()) {   //判断文件是否存在

				String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！
				Workbook wb;
				//根据文件后缀（xls/xlsx）进行判断
				if ( "xls".equals(split[1])){
					FileInputStream fis = new FileInputStream(excel);   //文件流对象
					wb = new HSSFWorkbook(fis);
				}else if ("xlsx".equals(split[1])){
					wb = new XSSFWorkbook(excel);
				}else {
					System.out.println("文件类型错误!");
					return;
				}

				//开始解析
				Sheet sheet = wb.getSheetAt(6);     //读取sheet 0
				List<Record> list = new ArrayList<>();

				int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
				int lastRowIndex = sheet.getLastRowNum();

				for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
					Row row = sheet.getRow(rIndex);
					List<Record> orgList = Db.find("select org.org_id from ebs_org_mapping ebs,organization org\n" +
                            "where ebs.tmp_org_code = org.code");
					for(Record orgRecord : orgList){
                        Record record = new Record();
                        if (row != null) {
                            row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                            String channelCode = row.getCell(0).getStringCellValue();
                            String org = row.getCell(2).getStringCellValue();
                            String bankkey = row.getCell(3).getStringCellValue();
                            String bankkeydesc = row.getCell(4).getStringCellValue();
                            String status = row.getCell(5).getStringCellValue();
                            String ossource = row.getCell(6).getStringCellValue();
                            String payAttr = row.getCell(7).getStringCellValue();
                            String tuikuan = row.getCell(8).getStringCellValue();
                            String bank = row.getCell(9).getStringCellValue();
                            String subordchannel = row.getCell(10).getStringCellValue();
                            record.set("os_source", "LA".equals(ossource)?"0":"1");
                            record.set("org_id", orgRecord.getStr("org_id"));
                            record.set("bankkey", bankkey);
                            record.set("bankkey_desc", bankkeydesc);
                            record.set("pay_mode", "收".equals(payAttr)?0:1);
                            record.set("bankkey_status", "停用".equals(status)?0:1);
                            record.set("channel_id", Db.findById("channel_setting", "channel_code", channelCode).getStr("id"));
                            record.set("is_source_back", "是".equals(tuikuan)?1:0);
                            String bank_type = Db.findById("const_bank_type", "name", bank).getStr("code");
                            record.set("bank_type", bank_type);
                            if("全部".equals(subordchannel)){
                                record.set("subordinate_channel", 0);
                            }else if("代理人".equals(subordchannel)){
                                record.set("subordinate_channel", 1);
                            }else if("银保".equals(subordchannel)){
                                record.set("subordinate_channel", 2);
                            }else if("网销客服".equals(subordchannel)){
                                record.set("subordinate_channel", 3);
                            }else if("电话行销".equals(subordchannel)){
                                record.set("subordinate_channel", 4);
                            }
                            record.set("persist_version", 0);
                            list.add(record);
                        }
                    }
				}
				logger.info("list.size=【"+list.size()+"】条");
//				for(int i=0; i<list.size(); i++){
//					Record rc = list.get(i);
//					logger.info("org_id=【"+rc.get("org_id")+"】"+"bankkey=【"+rc.getStr("bankkey")+"】");
//					Db.save("bankkey_setting",list.get(i));
//				}
				Db.batchSave("bankkey_setting",list,1000);
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
