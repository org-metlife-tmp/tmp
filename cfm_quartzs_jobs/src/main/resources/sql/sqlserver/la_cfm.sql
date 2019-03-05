#namespace("la_cfm")
#sql("getLACallBackOriginList")
  select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,tmp_status,tmp_err_message,persist_version
  from la_origin_pay_data
  where tmp_status in (?,?) and la_callback_status in (?,?)
#end

#sql("getLAUnCheckedOriginList")
  select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,persist_version
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
  	,b.bankkey_status, c.is_checkout
  from 
  	bankkey_setting b,channel_setting c,const_bank_type d
  where 
  	b.channel_id = c.id and 
  	b.bank_type=d.code and 
  	b.os_source = ? and 
  	b.org_id = ? and 
  	b.bankkey = ?
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


#end
