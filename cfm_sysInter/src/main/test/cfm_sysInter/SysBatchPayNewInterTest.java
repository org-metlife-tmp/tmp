package cfm_sysInter;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchPayNewInter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class SysBatchPayNewInterTest {
	private static final Logger logger = LoggerFactory.getLogger(SysBatchPayNewInterTest.class);
	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void start() {
		dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_test", "sa", "Admin123");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/sysinter_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
		dp.start();
		arp.start();
	}

	@After
	public void stop() {
		this.dp.stop();
		this.arp.stop();
	}
	
	@Test
	public void encryptAccNo() throws EncryAndDecryException{
		String a1 = "6225880280003345";
		String a2 = "6225880230001175";
		String a11 = SymmetricEncryptUtil.getInstance().encrypt(a1);
		String a12 = SymmetricEncryptUtil.getInstance().encrypt(a2);
		System.out.println(a11);
		System.out.println(a12);
		System.out.println(Db.update("update pay_batch_detail set recv_acc_no=? where recv_acc_no=?", a11, a1));;
		System.out.println(Db.update("update pay_batch_detail set recv_acc_no=? where recv_acc_no=?", a12, a2));;
	}

	@Test
	public void sendTest() throws ReqDataException {
		final List<Record> detailRecords = Db.find("select * from pay_batch_detail where base_id=?", 2);
		

		Map<String, String> accountAndBankInfo = getAccountAndBankInfo("591902896710201");

		List<Record> list = new ArrayList<>();
		for (Record r : detailRecords) {
			Record l = new Record();
			// 汇总表信息
			l.set("source_ref", "pay_batch_total");
			l.set("bill_id", 2L);
			l.set("pay_account_no", TypeUtils.castToString("591902896710201"));
			l.set("pay_account_name", TypeUtils.castToString("银企直连专用账户9"));
			l.set("pay_account_bank", TypeUtils.castToString("福州分行"));
			l.set("pay_account_cur", accountAndBankInfo.get("iso_code"));
			l.set("pay_bank_cnaps", accountAndBankInfo.get("bank_cnaps_code"));
			l.set("pay_bank_prov", accountAndBankInfo.get("province"));
			l.set("pay_bank_city", accountAndBankInfo.get("city"));
			l.set("pay_bank_type", accountAndBankInfo.get("bank_type"));

			// 明细表信息
			l.set("id", r.get("id"));
			l.set("package_seq", r.get("package_seq"));
			l.set("amount", r.get("amount"));
			l.set("recv_account_no", r.getStr("recv_acc_no"));
			l.set("recv_account_name", r.getStr("recv_acc_name"));
			l.set("recv_account_bank", r.getStr("recv_bank_name"));

			list.add(l);
		}

		Record instrRecord = new Record().set("list", list);

		String shortPayCnaps = accountAndBankInfo.get("bank_cnaps_code").substring(0, 3);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(shortPayCnaps, "BatchPayNew");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
		}
		final SysBatchPayNewInter sysInter = new SysBatchPayNewInter();
		sysInter.setChannelInter(channelInter);
		final Record instr = sysInter.genInstr(instrRecord);
		if (instr == null) {
			throw new ReqDataException("锁定批次单据失败！");
		}

		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 保存锁定表
				boolean saveIntr = sysInter.saveIntr();
				if (saveIntr) {
					// 子批次汇总表状态改为：4已发送未回盘
					int updPayBatchTotal = CommonService.updateRows("pay_batch_total",
							new Record().set("service_status", 4)
									.set("persist_version", 99)
									.set("send_user_name", "xx").set("send_on", new Date()),
							new Record().set("id", 2053));
					if (updPayBatchTotal == 1) {
						return true;
					} else {
						logger.error("批付单据发送时，更新子批次汇总表状态为‘已发送未回盘’失败。子批次汇总表id={}", 2053);
						return false;
					}
				}
				logger.error("批付单据发送时，保存批收指令表失败。子批次汇总表id={}", 2);
				return false;
			}
		});

		if (flag) {
			QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
			String jsonStr;
			try {
				jsonStr = ProcessEntrance.getInstance().process(channelInter.getInter(), bean.getParams());
				sysInter.callBack(jsonStr);
			} catch (BankInterfaceException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new ReqDataException("发送失败，请联系管理员！");
		}
	}
	
	public static Map<String, String> getAccountAndBankInfo(String accNo) throws ReqDataException {
		// 1. 通过pay_acc_no查询 cnaps号
		Map<String, Object> account = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", accNo);
		if (account == null) {
			throw new ReqDataException(String.format("未查到直连账户[%s]的信息", accNo));
		}
		String bankCnapsCode = account.get("bank_cnaps_code").toString();
		// 2. 通过curr_id查询付款账户币种
		Map<String, Object> currency = TableDataCacheUtil.getInstance().getARowData("currency", "id",
				TypeUtils.castToString(account.get("curr_id")));
		String isoCode = currency == null ? "CNY" : TypeUtils.castToString(currency.get("iso_code"));
		// 3. 通过cnaps号查询银行信息
		Map<String, Object> allBankInfo = TableDataCacheUtil.getInstance().getARowData("all_bank_info", "cnaps_code",
				bankCnapsCode);
		if (allBankInfo == null) {
			throw new ReqDataException(String.format("通过CNAPS号=[%s]未查到银行信息信息", bankCnapsCode));
		}
		String province = TypeUtils.castToString(allBankInfo.get("province"));
		String city = TypeUtils.castToString(allBankInfo.get("city"));
		String bankType = TypeUtils.castToString(allBankInfo.get("bank_type"));
		
		Map<String, String> result = new HashMap<>();
		result.put("bank_cnaps_code", bankCnapsCode);
		result.put("iso_code", isoCode);
		result.put("province", province);
		result.put("city", city);
		result.put("bank_type", bankType);
		return result;
	}
}
