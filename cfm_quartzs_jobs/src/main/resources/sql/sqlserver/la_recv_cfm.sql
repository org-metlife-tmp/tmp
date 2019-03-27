#namespace("la_recv_cfm")

	
#sql("getLARecvCallBackOriginList")
  select 
  	*
  from la_origin_recv_data
  where tmp_status in (?,?) and la_callback_status in (?,?)
#end

#sql("getLARecvUnCheckedOriginList")
  select 
	top 1000 
	id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,
	recv_date,amount,pay_acc_name,pay_cert_type,pay_cert_code,pay_bank_name,pay_acc_no,bank_key,
	sale_code,sale_name,op_code,op_name,persist_version 
  from 
	la_origin_recv_data 
  where 
	job_no = ?
	and is_process = ?
#end

#sql("updLARecvTotle")
	update 
		ZDDHPF 
	set [STATUS] = 4 
	where 
		JOBNO = ? and 
		[STATUS]=1
#end

#sql("updLaOriginStatus")
  update la_origin_recv_data set is_process = ?,tmp_status = ?,tmp_err_message = ?, persist_version = persist_version + 1
  where id = ? and persist_version = ?
#end



#sql("updLaRecvOriginStatus")
  update 
  	la_origin_recv_data 
  set 
  	is_process = ?,tmp_status = ?,tmp_err_message = ?, persist_version = persist_version + 1
  where 
  	id = ? and persist_version = ?
#end

#sql("updLaRecvOriginProcess")
  update 
  	la_origin_recv_data 
  set 
  	is_process = ?, persist_version = persist_version + 1
  where 
  	id = ? and persist_version = ?
#end

#sql("updLaRecvOrigin")
  update 
  	la_origin_recv_data 
  set 
  	bankcode = ?
  where 
  	id = ? and persist_version = ?
#end

#sql("getrecvlegal")
  SELECT
    legal.id,
    legal.origin_id
  FROM
    recv_legal_data legal,
    la_recv_legal_data_ext ext
  WHERE
    legal.id = ext.legal_id
    AND legal.source_sys = 0
    AND legal.status = 0
    AND ext.insure_bill_no =?
    AND legal.pay_acc_name =?
    AND legal.amount =?
    AND ext.recv_date =?
#end
#sql("dellarecvlegalext")
  delete from la_recv_legal_data_ext where legal_id=?
#end

#sql("getrecvcheck")
  SELECT
	*
FROM
	la_recv_check_doubtful
WHERE is_doubtful = 1
	AND insure_bill_no =?
	AND recv_acc_name =?
	AND amount =?
	AND ext.recv_date =?
#end

#end
