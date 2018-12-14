#sql("billList")
SELECT
	di.detail_id id,
	bi.pay_account_no,
	bi.pay_account_bank,
	di.recv_account_no,
	di.recv_account_name,
	di.persist_version,
	di.payment_amount,
	bi.create_on
FROM
  inner_batchpay_baseinfo bi,
	inner_batchpay_bus_attach_detail di
where bi.batchno = di.batchno
and bi.delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_query_key".equals(x.key))
          	bi.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_query_key".equals(x.key))
          	di.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            di.payment_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            di.payment_amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bi.create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bi.create_on) <= 0
          #elseif("org_id".equals(x.key))
            bi.org_id = #para(x.value)
          #elseif("is_checked".equals(x.key))
            di.is_checked = #para(x.value)
          #elseif("service_status".equals(x.key))
            di.pay_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            di.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by di.detail_id desc
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
	di.detail_id id,
	bi.pay_account_no,
	bi.pay_account_bank,
	di.recv_account_no,
	di.recv_account_name,
	di.payment_amount,
	bi.create_on,
  COUNT ( 1 )  total_num
FROM
	inner_batchpay_baseinfo bi,
	inner_batchpay_bus_attach_detail di,
	inner_batchpay_trans_checked che
WHERE bi.batchno = di.batchno
	and bi.delete_flag = 0
	AND che.delete_flag = 0	
	and di.detail_id = che.bill_id
  	#if(map != null)
	    #for(x : map)
	        #if(x.value&&x.value!="")
            AND
            #if("pay_query_key".equals(x.key))
              bi.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("recv_query_key".equals(x.key))
              di.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("min".equals(x.key))
              di.payment_amount >= #para(x.value)
            #elseif("max".equals(x.key))
              di.payment_amount <= #para(x.value)
            #elseif("org_id".equals(x.key))
              bi.org_id = #para(x.value)
            #elseif("is_checked".equals(x.key))
              di.is_checked = #para(x.value)
            #elseif("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),bi.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),bi.create_on) <= 0
            #elseif("service_status".equals(x.key))
              di.pay_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #else
              di.#(x.key) = #para(x.value)
            #end
		      #end
	    #end
  	#end	
GROUP BY
	di.detail_id,
	bi.pay_account_no,
	bi.pay_account_bank,
	di.recv_account_no,
	di.recv_account_name,
	di.payment_amount,
	bi.create_on
order by di.detail_id
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
					FROM inner_batchpay_trans_checked che
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

#sql("findNumByTradingNo")	
SELECT
	direction 
FROM
	acc_his_transaction 
WHERE
	direction IN ( 1, 2 ) 
	AND id IN (
    #for(x : tradingNo)
		#(for.index == 0 ? "" : ",") #para(x)
    #end
) 
GROUP BY
	direction
#end

#sql("findInnerBatchpayBaseinfoByBathno")
  SELECT * FROM inner_batchpay_baseinfo WHERE batchno = ?
#end