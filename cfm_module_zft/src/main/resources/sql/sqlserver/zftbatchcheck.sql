#sql("billList")
SELECT
	di.detail_id AS id,
	bi.pay_account_id,
	bi.pay_account_no,
	bi.pay_account_bank,
	di.recv_account_id,
	di.recv_account_no,
	di.recv_account_name,
	di.persist_version,
	di.payment_amount,
	bi.create_on ,
	bi.apply_on
FROM
  outer_batchpay_baseinfo bi,
	outer_batchpay_bus_attach_detail di
where bi.batchno = di.batchno
and bi.delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key))
          	bi.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_account_no".equals(x.key))
          	di.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            di.payment_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            di.payment_amount <= #para(x.value)
          #elseif("is_checked".equals(x.key))
            di.is_checked = #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bi.apply_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bi.apply_on) <= 0
          #elseif("org_id".equals(x.key))
            bi.org_id = #para(x.value)
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
	convert(varchar,trans_date)+' '+convert(varchar,trans_time)  AS trans_date 
FROM
	acc_his_transaction
where is_checked = 0
	and direction = 1
	#if(1 == map.recv_validate  || "1".equals(map.recv_validate))
	   and opp_acc_no = #para(map.recv_account_no)
	#end
	and acc_no = #para(map.pay_account_no)
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
			FROM outer_batchpay_trans_checked ctc
		WHERE ctc.delete_flag = 0 
			AND ctc.bill_id = ? ) ctc 
WHERE
	trans.is_checked = 1 
	AND trans.id = ctc.trans_id
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


#sql("selectInfoByBatchno")
  SELECT
      *
  FROM
      outer_batchpay_baseinfo
  Where
      batchno = ?
  #end
