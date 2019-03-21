package com.qhjf.test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;

public class EncryptTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;
	@Before
	public void start(){
//		dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_test", "sa", "Admin123");
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/sysinter_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/test_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/quartzs_job_cfm.sql");
        dp.start();
        arp.start();
	}
	
	@After
	public void stop(){
		this.dp.stop();
		this.arp.stop();
	}
	/**
	 * 对称加密
	 */
	/*@Test
	public void encrypt(){
		Map<String, String> map = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;

			{
		       	put("pay_legal_data", "recv_acc_no");
		       	put("la_check_doubtful", "recv_acc_no");
		       	put("ebs_check_doubtful", "recv_acc_no");
		       	put("pay_batch_detail", "recv_acc_no");
		       	put("batch_pay_instr_queue_detail", "recv_account_no");
		       	put("pay_offerDocument_detail", "recv_acc_no");
		       	
		       	put("recv_legal_data", "pay_acc_no");
		       	put("la_recv_check_doubtful", "pay_acc_no");
		       	put("recv_batch_detail", "pay_acc_no");
		       	put("batch_recv_instr_queue_detail", "pay_account_no");
		       	put("recv_offerDocument_detail", "pay_acc_no");
			}
		};
		Set<Entry<String,String>> entrySet = map.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			Db.tx(new IAtom() {
				
				@Override
				public boolean run() throws SQLException {
					String tb = entry.getKey();
					String column = entry.getValue();
//					List<String> accNos = Db.query("select distinct ? from ?", column, tb);
					SqlPara sel = Db.getSqlPara("test_cfm.getAccNo", Kv.create().set("tb", tb).set("col", column));
					List<String> accNos = Db.query(sel.getSql());
					for (String no : accNos) {
						String plain = null;
						try {
							plain = SymmetricEncryptUtil.getInstance().encrypt(no);
							System.out.println(no +"：加密："+plain);
							
//							Db.update("update ? set ? = ? where ? = ?", tb, column, plain, column, no);
							SqlPara upd = Db.getSqlPara("test_cfm.updAccNo", Kv.create().set("tb", tb).set("col", column));
							Db.update(upd.getSql(), plain, no);
						} catch (EncryAndDecryException e) {
							e.printStackTrace();
							return false;
						}
					}
					return true;
				}
			});
		}
	}*/
	/**
	 * 数据库加密
	 */
	/*@Test
	public void dbEncrypt(){
		Map<String, String> map = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
//				put("la_origin_pay_data", "recv_acc_no");
		       	put("ebs_origin_pay_data", "recv_acc_no");
		       	put("la_origin_recv_data", "pay_acc_no");
			}
		};
		Set<Entry<String,String>> entrySet = map.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			Db.tx(new IAtom() {
				
				@Override
				public boolean run() throws SQLException {
					String tb = entry.getKey();
					String column = entry.getValue();
					SqlPara sel = Db.getSqlPara("test_cfm.getAccNo", Kv.create().set("tb", tb).set("col", column));
					List<String> accNos = Db.query(sel.getSql());
					for (String no : accNos) {
						String plain = null;
						plain = DDHSafeUtil.encrypt(no);
						
						System.out.println(no +"：加密："+plain);
						if (null == plain || "".equals(plain.trim())) {
							return false;
						}
						SqlPara upd = Db.getSqlPara("test_cfm.updAccNo", Kv.create().set("tb", tb).set("col", column));
						Db.update(upd.getSql(), plain, no);
					}
					
					return true;
				}
			});
		}
	}*/
	@Test
	public void t1(){
		Map<String, String> map = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put("la_origin_pay_data", "recv_acc_no");
		       	put("ebs_origin_pay_data", "recv_acc_no");
		       	put("la_origin_recv_data", "pay_acc_no");
			}
		};
		Set<Entry<String,String>> entrySet = map.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			String tb = entry.getKey();
			System.out.println(tb);
			String column = entry.getValue();
			SqlPara sel = Db.getSqlPara("test_cfm.getAccNo", Kv.create().set("tb", tb).set("col", column));
			List<String> accNos = Db.query(sel.getSql());
			for (String no : accNos) {
				System.out.println(no+":解密:"+DDHSafeUtil.decrypt(no));
			}
		}
	}
	@Test
	public void t2(){
		Map<String, String> map = new HashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put("pay_legal_data", "recv_acc_no");
		       	put("la_check_doubtful", "recv_acc_no");
		       	put("ebs_check_doubtful", "recv_acc_no");
		       	put("pay_batch_detail", "recv_acc_no");
		       	put("batch_pay_instr_queue_detail", "recv_account_no");
		       	put("pay_offerDocument_detail", "recv_acc_no");
		       	
		       	put("recv_legal_data", "pay_acc_no");
		       	put("la_recv_check_doubtful", "pay_acc_no");
		       	put("recv_batch_detail", "pay_acc_no");
		       	put("batch_recv_instr_queue_detail", "pay_account_no");
		       	put("recv_offerDocument_detail", "pay_acc_no");
			}
		};
		Set<Entry<String,String>> entrySet = map.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			String tb = entry.getKey();
			System.out.println(tb);
			String column = entry.getValue();
			SqlPara sel = Db.getSqlPara("test_cfm.getAccNo", Kv.create().set("tb", tb).set("col", column));
			List<String> accNos = Db.query(sel.getSql());
			for (String no : accNos) {
				try {
					System.out.println(no+":解密:"+SymmetricEncryptUtil.getInstance().decryptToStr(no));
				} catch (EncryAndDecryException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@Test
	public void tt(){
		//9jn8rsPKCMnv/NegfnMOohj/nlLQfSmO9jrgKIqgxYU=
		try {
			System.out.println(SymmetricEncryptUtil.getInstance().decryptToStr("9jn8rsPKCMnv/NegfnMOohj/nlLQfSmO9jrgKIqgxYU="));
		} catch (EncryAndDecryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void tt2(){
		String s[] = {"0x00B84F0E73331A29DBEB513B4EFB192101000000E45CD14DFC44A7CC517C45A686A99C87CBDF525210E3EC1B589B2073A015E94CD82F94386D21941BB9E4129FE9159DF2",
				"0x00B84F0E73331A29DBEB513B4EFB192101000000EC20005F9E4D8DDE1DC7F4D5AE2794494294E234040917C5F1B47EB2BD26D2D7352130E1E501BA6FD3153C0511A32C61",
				"0x00B84F0E73331A29DBEB513B4EFB19210100000021C77BCDC9B718F674241024A432DCED21E3466A03AFB4EB620AAE82A64BD0C5FD0B606B22342C7E95D524EF499D78F8",
				"0x00B84F0E73331A29DBEB513B4EFB19210100000017346A70C0934E30815A86B9A425B1A6D7F84DCE42817E9C20CE886A287926433AAF4E72E419008EB23C10A70DF187B0"};
		try {
			for (String ss : s) {
				System.out.println(DDHSafeUtil.decrypt(ss));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
