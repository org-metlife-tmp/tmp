#namespace("ebs_cfm")
#sql("getEBSCallBackOriginList")
  select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,tmp_status,tmp_err_message,persist_version
  from ebs_origin_pay_data
  where tmp_status in (?,?) and la_callback_status in (?,?)
#end

#sql("getEBSUnCheckedOriginList")
  select id,source_sys,pay_code,org_code,insure_type,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,sale_code,sale_name,op_code,op_name,persist_version
  from ebs_origin_pay_data
  where is_process = ?
#end

#sql("getOrg")
  select m.ebs_org_code,m.tmp_org_code,o.org_id
  from ebs_org_mapping m,organization o
  where m.tmp_org_code = o.code and m.ebs_org_code = ?
#end

#sql("getCertType")
  select ebs_cert_type,tmp_cert_type
  from ebs_cert_type_mapping
  where ebs_cert_type = ?
#end

#sql("getChannel")
  select c.id as channel_id,c.channel_code,c.channel_desc
  from bankkey_setting b,channel_setting c
  where b.channel_id = c.id and b.os_source = ? and b.org_id = ? and b.bankkey = ? and b.bankkey_status = ? and c.is_checkout = ?
#end

#sql("updEbsOriginStatus")
  update ebs_origin_pay_data set is_process = ?,tmp_status = ?,tmp_err_message = ?, persist_version = persist_version + 1
  where id = ? and persist_version = ?
#end

#sql("updEbsOriginProcess")
  update ebs_origin_pay_data set is_process = ?, persist_version = persist_version + 1
  where id = ? and persist_version = ?
#end

#sql("getChannelOnline")
  select channel_code from channel_online
  where channel_code = ?
#end

#sql("getRecvBank")
  select b.code,b.name from ebs_bank_mapping e,const_bank_type b
  where e.tmp_bank_code = b.code and e.ebs_bank_code = ?
#end

#sql("getRecvBankCode")
  select * from ebs_bank_mapping 
  where tmp_bank_code = ?
#end

#end
