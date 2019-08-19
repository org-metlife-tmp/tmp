#sql("billList")
SELECT
	obpi.id ,
	obpi.base_id ,
	obpi.org_id ,
	obpi.dept_id ,
	obpi.pay_account_id ,
	obpi.pay_account_no ,
	obpi.pay_account_name ,
	obpi.pay_account_cur ,
	obpi.pay_account_bank ,
	obpi.pay_bank_cnaps ,
	obpi.pay_bank_prov ,
	obpi.pay_bank_city ,
	obpi.recv_account_id ,
	obpi.recv_account_no ,
	obpi.recv_account_name ,
	obpi.recv_account_cur ,
	obpi.recv_account_bank ,
	obpi.recv_bank_cnaps  ,
	obpi.recv_bank_prov ,
	obpi.recv_bank_city ,
	obpi.payment_amount ,
	obpi.pay_mode ,
	obpi.payment_summary ,
	obpi.service_status ,
	obpi.service_serial_number ,
	obpi.bank_serial_number ,
	obpi.repeat_count ,
	obpi.create_by ,
	obpi.create_on ,
	obpi.update_by ,
	obpi.update_on ,
	obpi.delete_flag ,
	obpi.process_bank_type ,
	case obpi.item_type  when '1' then '资金下拨' when '2' then '对外支付' else '其他' end item_type,
	obpi.attachment_count ,
	obpi.feed_back ,
	obpi.is_checked ,
	obpi.instruct_code ,
	obpi.statement_code,
	obpi.persist_version AS persist_version
FROM
	oa_branch_payment_item  obpi
	left join 
	oa_branch_payment obp
	on 
	obpi.base_id = obp.id
	and 
	obp.delete_flag = 0
WHERE 	 1=1
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
		      AND
		         #if("pay_account_no".equals(x.key) || "recv_account_no".equals(x.key))
                     obpi.#(x.key) like concat('%', #para(x.value), '%')
                 #elseif("service_status".equals(x.key))
                    obpi.service_status in(
                     #for(y : map.service_status)
                     #if(for.index > 0)
                     #(",")
                      #end
                      #(y)
                      #end
                     )
		          #elseif("min".equals(x.key))
		            obpi.payment_amount >= #para(x.value)
		          #elseif("max".equals(x.key))
		            obpi.payment_amount <= #para(x.value) 
		          #elseif("start_date".equals(x.key))
                    DATEDIFF(day,#para(x.value),obpi.create_on) >= 0
                  #elseif("end_date".equals(x.key))
                    DATEDIFF(day,#para(x.value),obpi.create_on) <= 0
			      #else
			        obpi.#(x.key) = #para(x.value)
			      #end
		     #end
	    #end
  	#end
  	order by obpi.id desc
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
			FROM oa_branch_payment_checked che 
		WHERE che.delete_flag = 0 
			AND che.bill_id = ? ) che 
WHERE
	trans.is_checked = 1 
	AND trans.id = che.trans_id
#end

