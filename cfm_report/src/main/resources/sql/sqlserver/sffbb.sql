#sql("sffbbGMSList")
SELECT
    recv.insure_bill_no,
    recv.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    recv.recv_mode as pay_mode,
    trans.check_date,
    recv.is_checked,
    case recv.use_funds  when '0' then '客户账户' when '1' then '新单签发' when '2' then '保全收费'
    when '3' then '定期结算收费' when '4' then '续期收费' when '5' then '不定期收费' when '6' then '保单暂记'
    when '7' then '追加投资悬账' end biz_type,
    recv.bank_code,
    bank.name bank_name,
    trans.check_user_name,
    voucher.accounting_period,
    org1.name org_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	recv_counter_checked as chec,
	recv_counter_bill AS recv,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher
where
    trans.id = voucher.trans_id and
    chec.batch_id=recv.batchNo and
    chec.trans_id=trans.tradingNo and
    org.org_id = recv.bill_org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    acc.org_id = org1.org_id and
    trans.direction = 2
#end
#sql("sffbbGMFList")
SELECT
    la.insure_bill_no,
    legal.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    la.pay_mode as pay_mode,
    channel.channel_code,
    channel.channel_desc,
    trans.check_date,
    channel.net_mode,
    gmf.is_checked,
    biz.type_name as biz_type,
    channel.bankcode,
    org1.name org_name,
    trans.check_user_name,
    voucher.accounting_period,
    bank.name bank_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	pay_gmf_checked as chec,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher,
	gmf_bill gmf,
    la_pay_legal_data_ext la,
    pay_legal_data legal,
    channel_setting AS channel,
    la_biz_type biz
where
    trans.id = voucher.trans_id and
    chec.bill_id=gmf.id and
    chec.trans_id=trans.tradingNo and
    org.org_id = gmf.org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    channel.id = legal.channel_id AND
    acc.org_id = org1.org_id
    and gmf.legal_id = la.legal_id
    AND gmf.legal_id = legal.id
    AND la.biz_type = biz.type_code
    AND legal.org_id = org.org_id
    AND la.pay_mode = '0'
    AND gmf.delete_num = 0
    AND gmf.source_sys = 0
    AND biz.type = 1
    and trans.direction = 1
union all
SELECT
    ebs.insure_bill_no,
    legal.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    ebs.pay_mode as pay_mode,
    channel.channel_code,
    channel.channel_desc,
    trans.check_date,
    channel.net_mode,
    gmf.is_checked,
    biz.type_name as biz_type,
    channel.bankcode,
    org1.name org_name,
    trans.check_user_name,
    voucher.accounting_period,
    bank.name bank_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	pay_gmf_checked as chec,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher,
    ebs_pay_legal_data_ext ebs,
    pay_legal_data legal,
    channel_setting AS channel,
    la_biz_type biz
where
    trans.id = voucher.trans_id and
    chec.bill_id=gmf.id and
    chec.trans_id=trans.tradingNo and
    org.org_id = gmf.org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    channel.id = legal.channel_id AND
    acc.org_id = org1.org_id and
    gmf.legal_id = ebs.legal_id
    AND gmf.legal_id = legal.id
    AND legal.org_id = org.org_id
    AND gmf.delete_num = 0
    AND gmf.source_sys = 1
    AND ebs.pay_mode = '0'
    and trans.direction = 1
#end
#sql("sffbbPLYList")
SELECT
    ext.insure_bill_no,
    legal.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    ext.pay_mode as pay_mode,
    channel.channel_code,
    channel.channel_desc,
    trans.check_date,
    pay.back_on,
    pay.child_batchno,
    channel.net_mode,
    pay.success_amount,
    pay.fail_amount,
    pay.is_checked,
    biz.type_name as biz_type,
    channel.bankcode,
    org1.name org_name,
    trans.check_user_name,
    voucher.accounting_period,
    bank.name bank_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	pay_batch_checked as chec,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher,
    pay_batch_total pay,
    pay_batch_total_master master,
    channel_setting AS channel,
    la_biz_type biz,
    pay_batch_detail detail,
    pay_legal_data legal,
	la_pay_legal_data_ext ext
where
    trans.id = voucher.trans_id and
    chec.bill_id=master.id and
    chec.trans_id=trans.tradingNo and
    org.org_id = ext.org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    acc.org_id = org1.org_id and
    master.channel_id = channel.id
	and pay.master_batchno = master.master_batchno
    and legal.id = detail.legal_id
    AND legal.id = ext.legal_id
    AND ext.biz_type = biz.type_code
    AND detail.base_id = pay.id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
    and trans.direction = 1
    union all
SELECT
    ext.insure_bill_no,
    legal.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    ext.pay_mode as pay_mode,
    channel.channel_code,
    channel.channel_desc,
    trans.check_date,
    pay.back_on,
    pay.child_batchno,
    channel.net_mode,
    pay.success_amount,
    pay.fail_amount,
    pay.is_checked,
    biz.type_name as biz_type,
    channel.bankcode,
    org1.name org_name,
    trans.check_user_name,
    voucher.accounting_period,
    bank.name bank_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	pay_batch_checked as chec,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher,
    pay_batch_total pay,
    pay_batch_total_master master,
    channel_setting AS channel,
    la_biz_type biz,
    pay_batch_detail detail,
    pay_legal_data legal,
    ebs_pay_legal_data_ext ext
where
    trans.id = voucher.trans_id and
    chec.bill_id=master.id and
    chec.trans_id=trans.tradingNo and
    org.org_id = ext.org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    acc.org_id = org1.org_id and
    master.channel_id = channel.id
	and pay.master_batchno = master.master_batchno
    and legal.id = detail.legal_id
    AND legal.id = ext.legal_id
    AND ext.biz_type = biz.type_code
    AND detail.base_id = pay.id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
    and trans.direction = 1
#end
#sql("sffbbPLSList")
SELECT
    ext.insure_bill_no,
    legal.pay_code,
    org.name  AS bill_org_name ,
    case trans.direction when '1' then '付款' when '2' then '收款' else '其他' end direction,
    ext.pay_mode as pay_mode,
    channel.channel_code,
    channel.channel_desc,
    trans.check_date,
    pay.back_on,
    pay.child_batchno,
    channel.net_mode,
    pay.success_amount,
    pay.fail_amount,
    pay.is_checked,
    biz.type_name as biz_type,
    channel.bankcode,
    org1.name org_name,
    trans.check_user_name,
    voucher.accounting_period,
    bank.name bank_name,
    trans.acc_no,
    trans.check_service_number,
    chec.create_on
FROM
	recv_batch_checked as chec,
	acc_his_transaction as trans,
	organization AS org,
	organization AS org1,
	account acc,
	all_bank_info bank,
	sun_voucher_data voucher,
    recv_batch_total pay,
    recv_batch_total_master master,
    channel_setting AS channel,
    la_biz_type biz,
    recv_batch_detail detail,
    recv_legal_data legal,
    la_recv_legal_data_ext ext
where
    trans.id = voucher.trans_id and
    chec.bill_id=master.id and
    chec.trans_id=trans.tradingNo and
    org.org_id = ext.org_id AND
    trans.acc_id = acc.acc_id AND
    acc.bank_cnaps_code = bank.cnaps_code and
    acc.org_id = org1.org_id and
    master.channel_id = channel.id
	and pay.master_batchno = master.master_batchno
    and legal.id = detail.legal_id
    AND legal.id = ext.legal_id
    AND ext.biz_type = biz.type_code
    AND detail.base_id = pay.id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
    and trans.direction = 1
#end
