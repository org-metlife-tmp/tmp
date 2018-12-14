#sql("billList")
SELECT
	id,
	pay_account_no,
	pay_account_bank,
	recv_account_no,
	recv_account_name,
	persist_version,
	allocation_amount payment_amount,
	create_on
FROM
	allocation_execute_instruction
where is_checked = #para(map.is_checked)
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_query_key".equals(x.key))
          	pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_query_key".equals(x.key))
          	recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            allocation_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            allocation_amount <= #para(x.value) 
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),create_on) <= 0             
          #elseif("service_status".equals(x.key))
            allocation_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by id desc
#end

#sql("tradingList")
SELECT
	his.*,
	bank.name bank_name 
FROM
	(
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
			convert(varchar,trans_date)+' '+convert(varchar,trans_time) trans_date
		FROM
			acc_his_transaction
		where is_checked = 0
			and direction = 1
			and acc_no = #para(map.pay_account_no)
			and opp_acc_no = #para(map.recv_account_no)
			and amount = #para(map.payment_amount)
			and convert(varchar,trans_date)+' '+convert(varchar,trans_time) >= #para(map.create_on)
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
			convert(varchar,trans_date)+' '+convert(varchar,trans_time) trans_date
		FROM
			acc_his_transaction
		where is_checked = 0
			and direction = 2
			and acc_no = #para(map.recv_account_no)
			and opp_acc_no = #para(map.pay_account_no)
			and amount = #para(map.payment_amount)
			and convert(varchar,trans_date)+' '+convert(varchar,trans_time) >= #para(map.create_on)
	) his,
	account acc,
	all_bank_info bank 
WHERE
	his.acc_id = acc.acc_id 
	AND acc.bank_cnaps_code = bank.cnaps_code			
#end

#sql("confirmbillList")
SELECT
	pay.id,
	pay.pay_account_no,
	pay.pay_account_bank,
	pay.recv_account_no,
	pay.recv_account_name,
	pay.allocation_amount payment_amount,
	pay.create_on,
	COUNT ( 1 )  total_num
FROM
	allocation_execute_instruction pay,
	allocation_trans_checked che 
WHERE che.delete_flag = 0	
	AND pay.is_checked = #para(map.is_checked)
	and pay.id = che.bill_id
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
		      AND
		          #if("pay_query_key".equals(x.key))
		          	pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
		          #elseif("recv_query_key".equals(x.key))
		          	recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
		          #elseif("min".equals(x.key))
		            allocation_amount >= #para(x.value)
		          #elseif("max".equals(x.key))
		            allocation_amount <= #para(x.value)
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
GROUP BY
	pay.id,
	pay.pay_account_no,
	pay.pay_account_bank,
	pay.recv_account_no,
	pay.recv_account_name,
	pay.allocation_amount,
	pay.create_on
order by pay.id desc
#end

#sql("confirmTradingList")
SELECT
	his.*,
	bank.name bank_name 
FROM
	(
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
			convert(varchar,trans.trans_date)+' '+convert(varchar,trans.trans_time) trans_date
		FROM
			acc_his_transaction trans,
			( 
				SELECT che.trans_id 
					FROM allocation_trans_checked che
				WHERE che.delete_flag = 0 
					AND che.bill_id = ? ) che 
		WHERE
			trans.is_checked = 1 
			AND trans.id = che.trans_id
	)his,
	account acc,
	all_bank_info bank 
WHERE
	his.acc_id = acc.acc_id 
	AND acc.bank_cnaps_code = bank.cnaps_code				
#end

