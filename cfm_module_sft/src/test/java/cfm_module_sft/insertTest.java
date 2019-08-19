package cfm_module_sft;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class insertTest {
	private static final Logger logger = LoggerFactory.getLogger(insertTest.class);

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
	public void findMasterByBatchNoTest() throws InterruptedException {
		
		//LA组批   pay_legal_data  la_pay_legal_data_ext
				final List<Record> res = new ArrayList<>();
				final List<Record> las = new ArrayList<>();
					for (int i = 550000  ; i < 1000000; i++) {
						Record re = new Record();
						Record la = new Record();					
						re.set("source_sys", 0);
						re.set("origin_id", i+1);
						re.set("pay_code", i+1);
						re.set("channel_id", 4);
						re.set("org_id", 1);
						re.set("org_code", "1");
						re.set("amount", 10);
						re.set("recv_acc_no", "zrE4U33fMjEaJyW5IfKE/+ovJrY+nNJKxzNTL6xvclM=");
						re.set("recv_acc_name", "1111");
						re.set("recv_cert_code", "11");
						re.set("recv_bank_name", "111");
						re.set("recv_bank_type", 1);
						re.set("create_time", new Date());					
						la.set("legal_id", i+1) ;
						la.set("origin_id", i+1) ;
						la.set("branch_code", "1") ;
						la.set("org_code", 1) ;
						la.set("preinsure_bill_no", "11") ;
						la.set("insure_bill_no", "11") ;
						la.set("pay_mode", "C") ;
						la.set("bank_key", "111") ;
						res.add(re)	;
						las.add(la);			
					}
					logger.info("============数据准备完成了");
					/*boolean deleteById = Db.deleteById("pay_legal_data", "id", 1);
					System.out.println(deleteById);*/
					int[] batchSave = Db.batchSave("pay_legal_data",  res , 1000);			
					int[] batchSave1 = Db.batchSave("la_pay_legal_data_ext", las ,1000);
					//Thread.sleep(1000*60*10);			
				/*boolean tx = Db.tx(new IAtom() {
					
					@Override
					public boolean run() throws SQLException {
						if(ArrayUtil.checkDbResult(batchSave)) {
							
							
							return  ArrayUtil.checkDbResult(batchSave1);
						}
						return false;
					}
				});*/
				
			
 }
}