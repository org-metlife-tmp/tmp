#sql("billList")
SELECT
	cei.id,
	cei.collect_id,
	cei.collect_execute_id,
	cei.bank_serial_number,
	cei.repeat_count,
	cei.pay_account_id,
	cei.pay_account_org_id,
	cei.pay_account_org_name,
	cei.pay_account_no,
	cei.pay_account_name,
	cei.pay_account_cur,
	cei.pay_account_bank,
	cei.pay_bank_cnaps,
	cei.pay_bank_prov,
	cei.pay_bank_city,
	cei.recv_account_id,
	cei.recv_account_org_id,
	cei.recv_account_org_name,
	cei.recv_account_no,
	cei.recv_account_name,
	cei.recv_account_cur,
	cei.recv_account_bank,
	cei.recv_bank_cnaps,
	cei.recv_bank_prov,
	cei.recv_bank_city,
	cei.collect_amount  AS payment_amount,
	cei.collect_status,
	cei.create_on,
	cei.create_on AS apply_on,
	cei.update_on,
	cei.percentage,
	cei.persist_version,
	cei.is_checked,
	cei.instruct_code,
	cei.statement_code
FROM
	collect_execute_instruction cei
WHERE 	 1=1
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
		      AND
		         #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
                     "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
                     #(x.key) like concat('%', #para(x.value), '%')
                 #elseif("collect_status".equals(x.key))
                    collect_status in(
                     #for(y : map.collect_status)
                     #if(for.index > 0)
                     #(",")
                      #end
                      #(y)
                      #end
                     )
		          #elseif("min".equals(x.key))
		            collect_amount >= #para(x.value)
		          #elseif("max".equals(x.key))
		            collect_amount <= #para(x.value) 
		          #elseif("start_date".equals(x.key))
                    DATEDIFF(day,#para(x.value),create_on) >= 0
                  #elseif("end_date".equals(x.key))
                    DATEDIFF(day,#para(x.value),create_on) <= 0
			      #else
			        #(x.key) = #para(x.value)
			      #end
		     #end
	    #end
  	#end
  	order by cei.id desc
#end



#sql("nochecktradingList")
SELECT
	id,
	acc_id,
	acc_no,
	acc_name,
	case direction when '1' then '付' when '2' then '收' else '其他' end direction,
	opp_acc_no,
	opp_acc_name,
	amount,
	summary,
	convert(varchar,trans_date)+' '+convert(varchar,trans_time) AS trans_date
FROM
	acc_his_transaction
where is_checked = 0
	and direction = 1
	and acc_no = #para(map.pay_account_no)
	#if(1 == map.recv_validate  || "1".equals(map.recv_validate))
		and opp_acc_no = #para(map.recv_account_no)
    #end
	and amount = #para(map.collect_amount)
	#if(1 == map.date_validate  || "1".equals(map.date_validate))
	   and DATEDIFF(day,#para(map.create_on),trans_date) >= 0
    #end
UNION ALL  
SELECT
	id,
	acc_id,
	acc_no,
	acc_name,
	case direction when '1' then '付' when '2' then '收' else '其他' end direction,
	opp_acc_no,
	opp_acc_name,
	amount,
	summary,
	convert(varchar,trans_date)+' '+convert(varchar,trans_time) AS trans_date
FROM
	acc_his_transaction
where is_checked = 0
	and direction = 2
	and acc_no = #para(map.recv_account_no)
	#if(1 == map.recv_validate  || "1".equals(map.recv_validate))
	   and opp_acc_no = #para(map.pay_account_no)
    #end	
	and amount = #para(map.collect_amount)
	#if(1 == map.date_validate  || "1".equals(map.date_validate))
	   and DATEDIFF(day,#para(map.create_on),trans_date) >= 0
    #end
#end




#sql("confirmTradingList")
SELECT
	trans.id,
	trans.acc_id,
	trans.acc_no,
	trans.acc_name,
	case trans.direction when '1' then '付' when '2' then '收' else '其他' end direction,
	trans.opp_acc_no,
	trans.opp_acc_name,
	trans.amount,
	trans.summary,
	convert(varchar,trans.trans_date)+' '+convert(varchar,trans.trans_time)  AS trans_date
FROM
	acc_his_transaction trans,
	( 
		SELECT ctc.trans_id 
			FROM collect_trans_checked ctc
		WHERE ctc.delete_flag = 0 
			AND ctc.bill_id = ? ) ctc 
WHERE
	trans.is_checked = 1 
	AND trans.id = ctc.trans_id
#end