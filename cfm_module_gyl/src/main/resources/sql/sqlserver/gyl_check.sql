#sql("billList")
SELECT
	gaei.id ,
	gaei.gyl_allocation_id ,
	gaei.gyl_allocation_execute_id ,
	gaei.bank_serial_number ,
	gaei.repeat_count ,
	gaei.pay_account_id ,
	gaei.pay_account_org_id ,
	gaei.pay_account_org_name ,
	gaei.pay_account_no ,
	gaei.pay_account_name ,
	gaei.pay_account_cur ,
	gaei.pay_account_bank ,
	gaei.pay_bank_cnaps ,
	gaei.pay_bank_prov ,
	gaei.pay_bank_city ,
	gaei.recv_account_no ,
	gaei.recv_account_name ,
	gaei.gyl_allocation_amount  AS payment_amount ,
	gaei.gyl_allocation_status ,
	gaei.create_on ,
	gaei.update_on ,
	gaei.percentage ,
	gaei.persist_version ,
	gaei.is_checked ,
	gaei.instruct_code ,
	gaei.statement_code 
FROM
	gyl_allocation_execute_instruction  gaei
WHERE 	 1=1
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
		      AND
		         #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
                     "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
                     #(x.key) like concat('%', #para(x.value), '%')
                 #elseif("gyl_allocation_status".equals(x.key))
                    gyl_allocation_status in(
                     #for(y : map.gyl_allocation_status)
                     #if(for.index > 0)
                     #(",")
                      #end
                      #(y)
                      #end
                     )
		          #elseif("min".equals(x.key))
		            gyl_allocation_amount >= #para(x.value)
		          #elseif("max".equals(x.key))
		            gyl_allocation_amount <= #para(x.value) 
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
  	order by gaei.id desc
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
	and opp_acc_no = #para(map.recv_account_no)
	and amount = #para(map.collect_amount)
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
	convert(varchar,trans_date)+' '+convert(varchar,trans_time) AS trans_date
FROM
	acc_his_transaction
where is_checked = 0
	and direction = 2
	and acc_no = #para(map.recv_account_no)
	and opp_acc_no = #para(map.pay_account_no)
	and amount = #para(map.collect_amount)
	and convert(varchar,trans_date)+' '+convert(varchar,trans_time) >= #para(map.create_on)
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
	trans.trans_date 
FROM
	acc_his_transaction trans,
	( 
		SELECT gac.trans_id 
			FROM gyl_allocation_checked gac
		WHERE gac.delete_flag = 0 
			AND gac.bill_id = ? ) gac 
WHERE
	trans.is_checked = 1 
	AND trans.id = gac.trans_id
#end
