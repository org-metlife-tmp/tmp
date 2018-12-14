#sql("billList")
SELECT
	cbbad.detail_id  AS id,
	cbbad.pay_account_id ,
	cbbad.pay_account_no,
	cbbad.pay_account_name,
	cbbad.pay_account_cur,
	cbbad.pay_account_bank,
	cbbad.pay_bank_cnaps,
	cbbad.pay_bank_prov,
	cbbad.payment_amount,
	cbbad.pay_status,
	cbbad.feed_back,
	cbbad.persist_version,
	cbb.recv_account_id,
	cbb.recv_account_no,
	cbb.recv_account_name,
	cbb.recv_account_cur,
	cbb.recv_account_bank,
	cbb.recv_bank_cnaps,
	cbb.recv_bank_prov,
	cbb.recv_bank_city,
	cbb.process_bank_type,
	cbb.total_num,
	cbb.pay_mode,
	cbb.create_on
FROM
  collect_batch_baseinfo cbb,
    collect_batch_bus_attach_detail cbbad
where cbb.batchno = cbbad.batchno
and cbb.delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key))
          	cbbad.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_account_no".equals(x.key))
          	cbb.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            cbbad.payment_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            cbbad.payment_amount <= #para(x.value)
          #elseif("is_checked".equals(x.key))
            cbbad.is_checked = #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),cbb.create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),cbb.create_on) <= 0
          #elseif("org_id".equals(x.key))
            cbb.org_id = #para(x.value)
          #elseif("service_status".equals(x.key))
            cbbad.pay_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            cbbad.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by cbbad.detail_id desc
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
	convert(varchar,trans_date)+' '+convert(varchar,trans_time)  AS trans_date 
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
	convert(varchar,trans.trans_date)+' '+convert(varchar,trans.trans_time)  AS trans_date
FROM
	acc_his_transaction trans,
	( 
		SELECT ctc.trans_id 
			FROM collect_batch_trans_checked ctc
		WHERE ctc.delete_flag = 0 
			AND ctc.bill_id = ? ) ctc 
WHERE
	trans.is_checked = 1 
	AND trans.id = ctc.trans_id
#end


#sql("selectInfoByBatchno")
  SELECT
      *
  FROM
      collect_batch_baseinfo
  Where
      batchno = ?
  #end

