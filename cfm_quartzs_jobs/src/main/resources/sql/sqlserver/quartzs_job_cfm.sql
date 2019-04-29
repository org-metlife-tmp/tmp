#namespace("quartzs_job_cfm")
  #sql("get_account_list")
    select a.acc_id,a.acc_no,a.acc_name,a.acc_attr,a.acc_purpose,a.open_date,a.lawfull_man,a.cancel_date,
           a.curr_id,a.bank_cnaps_code,a.org_id,a.interactive_mode,a.memo,a.is_activity,a.is_virtual,a.status,
           a.is_close_confirm,a.deposits_mode,a.subject_code 
    from account a where a.status != 2 and a.interactive_mode = 1 and a.is_activity = 1
  #end
  
  #sql("get_account")
    select a.acc_id,a.acc_no,a.acc_name,a.acc_attr,a.acc_purpose,a.open_date,a.lawfull_man,a.cancel_date,
           a.curr_id,a.bank_cnaps_code,a.org_id,a.interactive_mode,a.memo,a.is_activity,a.is_virtual,a.status,
           a.is_close_confirm,a.deposits_mode,a.subject_code  
    from account a where a.acc_id = ?
  #end
  
  #sql("get_bal_lock")
    select id,acc_id,query_date,pre_query_time from bal_query_instr_queue_lock where acc_id = ? and query_date = ?
  #end
  
  #sql("get_trans_lock")
    select id,acc_id,start_date,end_date,pre_query_time from trans_query_instr_queue_lock where acc_id = ? and start_date = ? and end_date = ?
  #end
  
  #sql("get_electronic_lock")
    select id,acc_id,start_date,end_date,pre_query_time from electronic_bill_query_lock where acc_id = ? and start_date = ? and end_date = ?
  #end
  
  #sql("get_account_cur_balance")
    select id,acc_id,acc_no,acc_name,bank_type,bal,available_bal,frz_amt,data_source,bal_date,import_time
    from acc_cur_balance where acc_id = ? and bal_date = ?
  #end
  
  #sql("get_account_his_balance")
    select id,acc_id,acc_no,acc_name,bank_type,bal,available_bal,frz_amt,data_source,bal_date,import_time
    from acc_his_balance where acc_id = ? and bal_date = ?
  #end
  
  #sql("get_instr_list")
    select id,bank_serial_number,pay_bank_cnaps as bank_cnaps_code,repeat_count,source_ref,bill_id,ref_attr,status,trade_date,process_bank_type 
    from single_pay_instr_queue where status = ? and init_resp_time is not null
  #end
  
  #sql("get_trade_result_list")
    select id,bank_serial_number,source_ref,bill_id,process_bank_type,pre_query_time 
    from trade_result_query_instr_queue_lock where process_bank_type = ? and bank_serial_number = ?
  #end
  
  #sql("update_instr_status")
    update single_pay_instr_queue set status = ? where id = ?
  #end
  
  #sql("bal_from_cur_to_his")
    INSERT into acc_his_balance(acc_id,acc_no,acc_name,bank_type,bal,available_bal,frz_amt,data_source,bal_date,import_time)
    select b.acc_id,b.acc_no,b.acc_name,b.bank_type,b.bal,b.available_bal,b.frz_amt,b.data_source,b.bal_date,b.import_time
    from acc_cur_balance b,account a  
    where b.acc_id=a.acc_id and a.interactive_mode != 1
  #end
  
   #sql("trans_from_cur_to_his")
    INSERT into acc_his_transaction(acc_id,acc_no,acc_name,bank_type,direction,amount,opp_acc_no,opp_acc_name,opp_acc_bank,summary,
           post_script,trans_date,trans_time,data_source,identifier,import_time,is_checked,is_refund_scan)
    select b.acc_id,b.acc_no,b.acc_name,b.bank_type,b.direction,b.amount,b.opp_acc_no,b.opp_acc_name,b.opp_acc_bank,b.summary,
           b.post_script,b.trans_date,b.trans_time,b.data_source,b.identifier,getdate(),0,0
    from acc_cur_transaction b,account a 
    where b.acc_id=a.acc_id and a.interactive_mode !=1
  #end
  
  #sql("delete_cur_bal")
    delete from  acc_cur_balance  
  #end
  
   #sql("delete_cur_his")
    delete from acc_cur_transaction
  #end
  
  #sql("delete_cur_bal_wave")
    delete from acc_cur_balance_wave
  #end
  
  #sql("get_account_list_balance")
    select a.acc_id,a.acc_no,a.acc_name,a.acc_attr,a.acc_purpose,a.open_date,a.lawfull_man,a.cancel_date,
           a.curr_id,a.bank_cnaps_code,a.org_id,a.interactive_mode,a.memo,a.is_activity,a.is_virtual,a.status,
           a.is_close_confirm,a.deposits_mode,a.subject_code 
    from account a 
    where a.status != 2 and a.interactive_mode = 1 
    	and a.is_activity = 1
  #end
  #sql("get_account_list_balance_batch")
    select a.acc_id,a.acc_no,a.acc_name,a.acc_attr,a.acc_purpose,a.open_date,a.lawfull_man,a.cancel_date,
           a.curr_id,a.bank_cnaps_code,a.org_id,a.interactive_mode,a.memo,a.is_activity,a.is_virtual,a.status,
           a.is_close_confirm,a.deposits_mode,a.subject_code 
    from account a 
    where a.status != 2 and a.interactive_mode = 1 
    	and a.is_activity = 1 
    	#if(cnpas != null)
    		and (
    		#for(x: cnpas)
    			#if(for.index > 0)
                	#("or")
           		#end
    			bank_cnaps_code like concat(#(x), '%')
    		#end
    		)
    	#end
    order by acc_id ASC
  #end
  
  #sql("get_account_cur_balance_byaccno")
    select id,acc_id,acc_no,acc_name,bank_type,bal,available_bal,frz_amt,data_source,bal_date,import_time
    from acc_cur_balance where acc_no = ? and bal_date = ?
  #end
  
  #sql("get_account_byaccno")
    select a.acc_id,a.acc_no,a.acc_name,a.acc_attr,a.acc_purpose,a.open_date,a.lawfull_man,a.cancel_date,
           a.curr_id,a.bank_cnaps_code,a.org_id,a.interactive_mode,a.memo,a.is_activity,a.is_virtual,a.status,
           a.is_close_confirm,a.deposits_mode,a.subject_code  
    from account a where a.acc_no = ?
  #end
  
  #sql("get_account_his_balance_byaccno")
    select id,acc_id,acc_no,acc_name,bank_type,bal,available_bal,frz_amt,data_source,bal_date,import_time
    from acc_his_balance where acc_no = ? and bal_date = ?
  #end

  #sql("get_batch_dbt_nocompletion")
    select id,batchno,persist_version 
    from inner_batchpay_baseinfo where service_status = ?
  #end
  
  #sql("get_batch_dbt_count")
    select count(1) as cnt 
    from inner_batchpay_bus_attach_detail where batchno = ? and pay_status in(?,?,?)
  #end
  
  #sql("update_batch_dbt_status")
    update inner_batchpay_baseinfo set service_status = ? where id = ? and persist_version = ?
  #end
  
  #sql("get_batch_zft_nocompletion")
    select id,batchno,persist_version 
    from outer_batchpay_baseinfo where service_status = ?
  #end
  
  #sql("get_batch_zft_count")
    select count(1) as cnt 
    from outer_batchpay_bus_attach_detail where batchno = ? and pay_status in(?,?,?)
  #end
  
  #sql("update_batch_zft_status")
    update outer_batchpay_baseinfo set service_status = ? where id = ? and persist_version = ?
  #end
  
  
  #sql("selOneLaRecvTotal")
    select * from ZDDHPF where [STATUS] in(0,2,3)
  #end
  
  #sql("selOneBatchLaRecvDetail")
    select 
		count(1) size, sum(amount) sum_amount
	from 
		la_origin_recv_data 
	where 
		job_no = ?
  #end
  
  #sql("ddhEncrypt")
    Exec jam ?
  #end
  
  #sql("ddhDecrypt")
    Exec jem ?
  #end
#end
  
