#namespace("la_cfm")
#sql("getLACallBackOriginList")
  select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,tmp_status,tmp_err_message,persist_version
  from la_origin_pay_data
  where tmp_status in (?,?) and la_callback_status in (?,?)
#end

#sql("getLAUnCheckedOriginList")
  select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,persist_version
  ,create_time
  from la_origin_pay_data
  where is_process = ?
#end

#sql("getOrg")
  select m.la_org_code,m.la_branch_code,m.tmp_org_code,o.org_id
  from la_org_mapping m,organization o
  where m.tmp_org_code = o.code and m.la_org_code = ? and m.la_branch_code = ?
#end

#sql("getCertType")
  select la_cert_type,tmp_cert_type
  from la_cert_type_mapping
  where la_cert_type = ?
#end

#sql("getChannel")
  select 
  	c.id as channel_id,c.channel_code,c.channel_desc,b.bank_type,d.name
  	,b.bankkey_status, c.is_checkout, c.bankcode
  from 
  	bankkey_setting b,channel_setting c,const_bank_type d
  where 
  	b.channel_id = c.id and 
  	b.bank_type=d.code and 
  	b.os_source = ? and 
  	b.org_id = ? and 
  	b.bankkey = ? and
  	b.bankkey_status = 1 and 
  	c.is_checkout = 1 and 
  	c.pay_attr = ?
#end

#sql("updLaOriginStatus")
  update la_origin_pay_data set is_process = ?,tmp_status = ?,tmp_err_message = ?, persist_version = persist_version + 1
  where id = ? and persist_version = ?
#end

#sql("updLaOriginProcess")
  update la_origin_pay_data set is_process = ?, persist_version = persist_version + 1
  where id = ? and persist_version = ?
#end

#sql("getChannelOnline")
  select channel_code from channel_online
  where channel_code = ?
#end

#sql("getpaylegal")
  SELECT
    legal.id,
    legal.origin_id,
    legal.pay_code
  FROM
    pay_legal_data legal,
    la_pay_legal_data_ext ext
  WHERE
    legal.id = ext.legal_id
    AND legal.source_sys = 0
    AND legal.status = 0
    AND ext.insure_bill_no =?
    AND legal.recv_acc_name =?
    AND legal.amount =?
    AND CONVERT(varchar(100), legal.create_time, 112) = ?
#end
#sql("dellapaylegalext")
  delete from la_pay_legal_data_ext where legal_id=?
#end

#sql("getpaycheck")
  SELECT
	*
FROM
	la_check_doubtful
WHERE is_doubtful = 1
  AND status = 0
	AND insure_bill_no =?
	AND recv_acc_name =?
	AND amount =?
	AND CONVERT(varchar(100), create_time, 112) = ?
#end

#sql("qryBankTypeByAccNo")
  	SELECT
		*
	FROM 
		all_bank_info 
	WHERE 
		cnaps_code in(
			select bank_cnaps_code from account where acc_no=?
		)
#end


#end
