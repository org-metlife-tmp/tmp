#sql("billList")
SELECT
	ohp.id ,
	ohp.org_id ,
	ohp.dept_id ,
	ohp.pay_account_id ,
	ohp.pay_account_no ,
	ohp.pay_account_name ,
	ohp.pay_account_cur ,
	ohp.pay_account_bank ,
	ohp.pay_bank_cnaps ,
	ohp.pay_bank_prov ,
	ohp.pay_bank_city ,
	ohp.recv_account_id ,
	ohp.recv_account_no ,
	ohp.recv_account_name ,
	ohp.recv_account_cur ,
	ohp.recv_account_bank ,
	ohp.recv_bank_cnaps  ,
	ohp.recv_bank_prov ,
	ohp.recv_bank_city ,
	ohp.payment_amount ,
	ohp.pay_mode ,
	ohp.payment_summary ,
	ohp.service_status ,
	ohp.service_serial_number ,
	ohp.bank_serial_number ,
	ohp.repeat_count ,
	ohp.create_by ,
	ohp.create_on ,
	ohp.update_by ,
	ohp.update_on ,
	ohp.delete_flag ,
	ohp.process_bank_type ,
	ohp.persist_version ,
	ohp.attachment_count ,
	ohp.feed_back ,
	ohp.is_checked ,
	ohp.instruct_code ,
	ohp.statement_code 
FROM
	nc_head_payment  ohp
WHERE 	 1=1
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
		      AND
		         #if("pay_account_no".equals(x.key) || "recv_account_no".equals(x.key))
                     #(x.key) like concat('%', #para(x.value), '%')
                 #elseif("service_status".equals(x.key))
                    service_status in(
                     #for(y : map.service_status)
                     #if(for.index > 0)
                     #(",")
                      #end
                      #(y)
                      #end
                     )
		          #elseif("min".equals(x.key))
		            payment_amount >= #para(x.value)
		          #elseif("max".equals(x.key))
		            payment_amount <= #para(x.value) 
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
  	order by ohp.id desc
#end


#sql("alreaadyTradingList")
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
		SELECT che.trans_id 
			FROM nc_head_payment_checked che
		WHERE che.delete_flag = 0 
			AND che.bill_id = ? ) che 
WHERE
	trans.is_checked = 1 
	AND trans.id = che.trans_id
#end


