package cfm_module_sft;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchPayInter;

public class DiskSendingServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(DiskSendingServiceTest.class);

	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void start() {
		dp = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_pretest", "sa", "Admin123");
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
		long id = 2L;
		Record cbRecord = Db.findById("pay_batch_total", "id", id);
		System.out.println(cbRecord);
	}

	@Test
	public void findInstrTotalTest() throws ReqDataException {
		// 1. 根据前端传入 子批次id查询子批次
		final long id = 2L;
		final Record cbRecord = Db.findById("pay_batch_total", "id", id);
		if(cbRecord == null){
			throw new ReqDataException("未找到相应的子批次！");
		}
		//2. 根据子批次记录中的主批次号查询 主批次
		Record mbRecord = Db.findFirst(Db.getSql("disk_downloading.findMasterByBatchNo"),
				TypeUtils.castToString(cbRecord.get("master_batchno")));
		if(mbRecord == null){
			throw new ReqDataException("未找到相应的主批次！");
		}
		//3. 根据子批次id查询pay_batch_detail所有的明细
		final List<Record> detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), id);
		
		Map<String, String> accountAndBankInfo = getPayAccountAndBankInfo(mbRecord.getStr("pay_acc_no"));
		
		List<Record> list = new ArrayList<>();
		for(Record r : detailRecords){
			Record l = new Record();
			//汇总表信息
			l.set("source_ref", "pay_batch_total");
			l.set("bill_id", id);
			l.set("pay_account_no",TypeUtils.castToString(mbRecord.get("pay_acc_no")));
			l.set("pay_account_name",TypeUtils.castToString(mbRecord.get("pay_acc_name")));
			l.set("pay_account_bank",TypeUtils.castToString(mbRecord.get("pay_bank_name")));
			l.set("pay_account_cur", accountAndBankInfo.get("iso_code"));
			l.set("pay_bank_cnaps", accountAndBankInfo.get("bank_cnaps_code"));
			l.set("pay_bank_prov", accountAndBankInfo.get("province"));
			l.set("pay_bank_city", accountAndBankInfo.get("city"));
			l.set("pay_bank_type", accountAndBankInfo.get("bank_type"));
			
			//明细表信息
			Map<String, String> detailAccountAndBankInfo = getRecvAccountAndBankInfo(r.getStr("recv_acc_no"));
			l.set("id", r.get("id"));
			l.set("package_seq", r.get("package_seq"));
			l.set("amount", r.get("amount"));
			l.set("recv_account_no", r.getStr("recv_acc_no"));
			l.set("recv_account_name", r.getStr("recv_acc_name"));
			l.set("recv_account_cur", detailAccountAndBankInfo.get("iso_code"));
			l.set("recv_account_bank", r.getStr("recv_bank_name"));
			l.set("recv_bank_cnaps", detailAccountAndBankInfo.get("bank_cnaps_code"));
			l.set("recv_bank_prov", detailAccountAndBankInfo.get("province"));
			l.set("recv_bank_city", detailAccountAndBankInfo.get("city"));
			l.set("recv_bank_type", detailAccountAndBankInfo.get("bank_type"));
			
			list.add(l);
		}
		
		Record instrRecord = new Record().set("list", list);
		
		String shortPayCnaps = accountAndBankInfo.get("bank_cnaps_code").substring(0, 3);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(shortPayCnaps, "BatchPay");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
		}
		final SysBatchPayInter sysInter = new SysBatchPayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(instrRecord);
        if (instr == null) {
        	throw new ReqDataException("锁定批次单据失败！");
		}
		
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//保存锁定表
				boolean saveIntr = sysInter.saveIntr();
				if(saveIntr){
					return true;
				}
				return false;
			}
		});

		if (flag) {
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            throw new ReqDataException("发送失败，请联系管理员！");
        }
	}

	/**
	 * 通过银行账号，获取付方账号的cnaps、币种、开卡省份、开卡市、开卡银行类型
	 * @param accNo	银行账号
	 * @return
	 * @throws ReqDataException
	 */
	private Map<String, String> getPayAccountAndBankInfo(String accNo) throws ReqDataException {
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
	
	/**
	 * 通过银行账号，获取收方账号的cnaps、币种、开卡省份、开卡市、开卡银行类型
	 * @param accNo	银行账号
	 * @return
	 * @throws ReqDataException
	 */
	private Map<String, String> getRecvAccountAndBankInfo(String accNo) {
		Map<String, String> result = new HashMap<>();
		// 1. 通过pay_acc_no查询 cnaps号
		Map<String, Object> supplier = TableDataCacheUtil.getInstance().getARowData("supplier_acc_info", "acc_no", accNo);
		if (supplier == null) {
			return result;
		}
		String bankCnapsCode = supplier.get("cnaps_code").toString();
		result.put("bank_cnaps_code", bankCnapsCode);
		// 2. 通过curr_id查询付款账户币种
		Map<String, Object> currency = TableDataCacheUtil.getInstance().getARowData("currency", "id",
				TypeUtils.castToString(supplier.get("curr_id")));
		String isoCode = currency == null ? "CNY" : TypeUtils.castToString(currency.get("iso_code"));
		result.put("iso_code", isoCode);
		// 3. 通过cnaps号查询银行信息
		Map<String, Object> allBankInfo = TableDataCacheUtil.getInstance().getARowData("all_bank_info", "cnaps_code",
				bankCnapsCode);
		if (allBankInfo == null) {
			return result;
		}
		String province = TypeUtils.castToString(allBankInfo.get("province"));
		String city = TypeUtils.castToString(allBankInfo.get("city"));
		String bankType = TypeUtils.castToString(allBankInfo.get("bank_type"));
		result.put("province", province);
		result.put("city", city);
		result.put("bank_type", bankType);
		return result;
	}
}
