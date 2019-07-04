package cfm_webservice_sft;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.*;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupAdvanceReceiptStatusQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccConfirmRespBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class EbsGroupCounterRecvTest {
	private static Logger log = LoggerFactory.getLogger(EbsGroupCounterRecvTest.class);

	DruidPlugin dp = null;
	RedisPlugin cfmRedis = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void before() {
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin",
				"User123$");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/auto_recv_counter_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
		cfmRedis = new CfmRedisPlugin("cfm", "10.164.26.48");
		cfmRedis.setSerializer(new JdkSerializer());
		dp.start();
		arp.start();
		cfmRedis.start();
	}

	@After
	public void after() {
		if (null != arp) arp.stop();
		if (null != dp) dp.stop();
		if (null != dp) dp.stop();
	}
	/**
	 * 客户账户查询
	 */
	@Test
	public void ebsCustomerAccQry() {
		List<Record> find = Db.find(Db.getSql("auto_recv_counter.confirm_group_list"));
		if (null == find || find.size() == 0) {
			log.info("====不存在需要重试的团单确认支付数据====");
			return;
		}
		for (int i = 0; i < find.size(); i++) {
			Record record = find.get(i);
			int persist_version = TypeUtils.castToInt(record.get("persist_version"));
			int confrim_try_times = TypeUtils.castToInt(record.get("confrim_try_times"));
			for (int j = 0; j < 5; j++) {
				confrim_try_times = confrim_try_times + 1;
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口开始====");
				int useFunds = TypeUtils.castToInt(record.get("use_funds"));
				RecvCounterRemoteCall recvCounter = new RecvCounterRemoteCall();
				//1，调用查询接口
				try {
					GroupAdvanceReceiptStatusQryReqBean groupAdvanceReceiptStatusQryReqBean = new GroupAdvanceReceiptStatusQryReqBean(
							TypeUtils.castToString(record.get("zj_flow_number")));
					GroupAdvanceReceiptStatusQryRespBean groupAdvanceReceiptStatusQryRespBean = recvCounter.ebsConfirmStatusQry(groupAdvanceReceiptStatusQryReqBean);
					if ("SUCCESS".equals(groupAdvanceReceiptStatusQryRespBean.getResultCode())) {
						//返回成功,更新数据
						log.error("===团单新增查询接口返回成功===");
						if (CommonService.updateRows("recv_counter_bill",
								new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times),
								new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
							persist_version++;
							return;
						}
					}
				} catch (ReqDataException e) {
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口异常====");
					continue;
				}
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口结束====");

				//2，调用确认接口
				try {
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询确认开始====");
					if (useFunds == WebConstant.SftRecvGroupCounterUseFunds.KHZH.getKey()) {
						//客户账号
						GroupCustomerAccConfirmReqBean groupCustomerAccConfirmReqBean = new GroupCustomerAccConfirmReqBean();
						groupCustomerAccConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
						groupCustomerAccConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错
						groupCustomerAccConfirmReqBean.setPayWay(WebConstant.Sft_RecvGroupCounter_Recvmode.
								getSft_RecvGroupCounter_RecvmodeByKey(Integer.valueOf(TypeUtils.castToString(record.get("recv_mode")))).getKeyc());  //缴费方式 2-现金缴款单 3-支票 4-转账汇款
						groupCustomerAccConfirmReqBean.setPayMoney(TypeUtils.castToBigDecimal(record.get("amount")).setScale(2).
								stripTrailingZeros().toPlainString());  //交费金额 单位元，2位小数
						groupCustomerAccConfirmReqBean.setChequeNo(TypeUtils.castToString(record.get("bill_number")));  //票据号码 3-支票时必传
						groupCustomerAccConfirmReqBean.setChequeDate(DateFormatThreadLocal.format("yyyy-MM-dd",
								TypeUtils.castToDate(record.get("bill_date"))));  //支票日期,YYYY-MM-DD 3-支票时必传
						Record faccount = Db.findById("account", "bankcode", TypeUtils.castToString(record.get("recv_bank_name")));
						Record ftmpBank = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(faccount.get("bank_cnaps_code")));
						Record febsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(ftmpBank.get("bank_type")));
						groupCustomerAccConfirmReqBean.setBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
						groupCustomerAccConfirmReqBean.setBankAccNo("consumer_acc_no");  //客户付款银行账号;缴费方式 2和3时 必传
						groupCustomerAccConfirmReqBean.setBankAccName(TypeUtils.castToString(faccount.get("acc_name")));  //客户付款银行户名;缴费方式 2和3时 必传
						//根据bankcode做映射
						Record stmpBank = Db.findById("const_bank_type", "code", TypeUtils.castToString(record.get("consumer_bank_name")));
						Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
						groupCustomerAccConfirmReqBean.setInBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
						groupCustomerAccConfirmReqBean.setInBankAccNo("consumer_acc_no");  //大都会收款银行账号

						GroupCustomerAccConfirmRespBean groupCustomerAccConfirmRespBean = recvCounter.ebsCustomerAccConfirm(groupCustomerAccConfirmReqBean);
						if ("SUCCESS".equals(groupCustomerAccConfirmRespBean.getResultCode())) {
							//返回成功
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times)
											.set("back_on", new Date()).set("is_sunvouder", 1),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								return;
							}
						} else {
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 0).set("persist_version", persist_version + 1)
											.set("confirm_msg", groupCustomerAccConfirmRespBean.getResultMsg()).set("confrim_try_times", confrim_try_times),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								continue;
							}
						}
					} else {
						//XDQF(1, "新单签发"), BQSF(2, "保全收费"), DQJSSF(3, "定期结算收费"), XQSF(4, "续期收费"), BDQSF(5, "不定期收费")
						GroupBizPayConfirmReqBean groupBizPayConfirmReqBean = new GroupBizPayConfirmReqBean();
						groupBizPayConfirmReqBean.setBussinessNo("bussiness_no");
						groupBizPayConfirmReqBean.setBussinessType(WebConstant.SftRecvGroupCounterUseFunds.getSftRecvGroupCounterUseFundsByKey(useFunds).getKeyc());
						groupBizPayConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
						groupBizPayConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错
						groupBizPayConfirmReqBean.setPayWay(WebConstant.Sft_RecvGroupCounter_Recvmode.
								getSft_RecvGroupCounter_RecvmodeByKey(Integer.valueOf(Integer.valueOf(TypeUtils.castToString(record.get("recv_mode"))))).getKeyc());  //缴费方式 2-现金缴款单 3-支票 4-转账汇款
						groupBizPayConfirmReqBean.setPayMoney(TypeUtils.castToBigDecimal(record.get("amount")).setScale(2).
								stripTrailingZeros().toPlainString());  //交费金额 单位元，2位小数
						groupBizPayConfirmReqBean.setChequeNo(TypeUtils.castToString(record.get("bill_number")));  //票据号码 3-支票时必传
						groupBizPayConfirmReqBean.setChequeDate(DateFormatThreadLocal.format("yyyy-MM-dd",
								TypeUtils.castToDate(record.get("bill_date"))));  //支票日期,YYYY-MM-DD 3-支票时必传
						Record faccount = Db.findById("account", "bankcode", TypeUtils.castToString(record.get("recv_bank_name")));
						Record ftmpBank = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(faccount.get("bank_cnaps_code")));
						Record febsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(ftmpBank.get("bank_type")));
						groupBizPayConfirmReqBean.setBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
						groupBizPayConfirmReqBean.setBankAccNo("consumer_acc_no");  //客户付款银行账号;缴费方式 2和3时 必传
						groupBizPayConfirmReqBean.setBankAccName(TypeUtils.castToString(faccount.get("acc_name")));  //客户付款银行户名;缴费方式 2和3时 必传
						//根据bankcode做映射
						Record stmpBank = Db.findById("const_bank_type", "name", TypeUtils.castToString(record.get("consumer_bank_name")));
						Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
						groupBizPayConfirmReqBean.setInBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
						groupBizPayConfirmReqBean.setInBankAccNo("consumer_acc_no");  //大都会收款银行账号

						GroupBizPayConfirmRespBean groupBizPayConfirmRespBean = recvCounter.ebsBizPayConfirm(groupBizPayConfirmReqBean);
						if ("SUCCESS".equals(groupBizPayConfirmRespBean.getResultCode())) {
							//返回成功
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times)
											.set("back_on", new Date()).set("is_sunvouder", 1),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								return;
							}
						} else {
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 0).set("persist_version", persist_version + 1)
											.set("confirm_msg", groupBizPayConfirmRespBean.getResultMsg()).set("confrim_try_times", confrim_try_times),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								continue;
							}
						}
					}
				} catch (ReqDataException e) {
					log.error("===团单新增确认接口异常===");
					e.printStackTrace();
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次确认接口异常====");
					continue;
				}
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次确认接口结束====");
			}
		}
	}


}
