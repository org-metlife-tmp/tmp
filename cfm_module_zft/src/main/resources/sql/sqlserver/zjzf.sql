#sql("findMoreInfo")
SELECT
	zft.*,
	bank.bank_type as pay_bank_type
FROM
	outer_zf_payment zft,all_bank_info bank,organization org
WHERE 1=1 and zft.pay_bank_cnaps = bank.cnaps_code and org.org_id = zft.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
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
             DATEDIFF(day,#para(x.value),apply_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),apply_on) <= 0
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #elseif("org_id".equals(x.key))
            zft.org_id = #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by zft.service_status asc,zft.id desc
#end


#sql("findAllAmount")
SELECT
	SUM(payment_amount) as total_amount,
	COUNT(0) as total_num
FROM
	outer_zf_payment zft,organization org
WHERE 1=1 and zft.org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
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
             DATEDIFF(day,#para(x.value),apply_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),apply_on) <= 0
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #elseif("org_id".equals(x.key))
            zft.org_id = #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findPendingList")
SELECT
	ozp.id as ipd_id,
	ozp.pay_account_id,
	ozp.pay_account_no,
	ozp.pay_account_name,
	ozp.pay_account_bank,
	ozp.recv_account_id,
	ozp.recv_account_no,
	ozp.recv_account_name,
	ozp.recv_account_bank,
	ozp.payment_amount,
	ozp.payment_summary,
	ozp.service_status,
	ozp.service_serial_number,
	ozp.bank_serial_number,
	ozp.create_by,
	ozp.create_on,
	ozp.apply_on,
	ozp.update_by,
	ozp.update_on,
	ozp.delete_flag,
	ozp.org_id,
	ozp.dept_id,
	ozp.pay_account_cur,
	ozp.recv_account_cur,
	ozp.pay_bank_cnaps,
	ozp.recv_bank_cnaps,
	ozp.pay_bank_prov,
	ozp.recv_bank_prov,
	ozp.pay_bank_city,
	ozp.recv_bank_city,
	ozp.process_bank_type,
	ozp.persist_version,
	ozp.attachment_count,
	ozp.feed_back,
	ozp.biz_id,
	ozp.biz_name,
	cwrei.id as inst_id,
	cwrei.base_id,
	cwrei.workflow_name,
	cwrei.define_id,
	cwrei.workflow_type,
	cwrei.reject_strategy,
	cwrei.def_version,
	cwrei.workflow_node_id,
	cwrei.step_number,
	cwrei.shadow_execute,
	cwrei.shadow_user_id,
	cwrei.shadow_user_name,
	cwrei.biz_type,
	cwrei.bill_id,
	cwrei.bill_code,
	cwrei.submitter,
	cwrei.submitter_name,
	cwrei.submitter_pos_id,
	cwrei.submitter_pos_name,
	cwrei.init_user_id,
	cwrei.init_user_name,
	cwrei.init_org_id,
	cwrei.init_org_name,
	cwrei.init_dept_id,
	cwrei.init_dept_name,
	cwrei.start_time
FROM
	outer_zf_payment ozp,cfm_workflow_run_execute_inst cwrei
WHERE ozp.id = cwrei.bill_id
  #for(x : map)
    #if("in".equals(x.key))
      #if(map.in != null)
        AND cwrei.id IN (
          #for(y : map.in)
            #for(z : y.instIds)
              #if(for.index > 0)
                #(",")
              #end
              #(z)
            #end
          #end
        )
      #end
    #elseif("notin".equals(x.key))
      #if(map.notin != null)
        AND cwrei.id NOT IN (
          #for(y : map.notin)
            #for(z : y.excludeInstIds)
              #if(for.index > 0)
                #(",")
              #end
              #(z)
            #end
          #end
        )
      #end
    #elseif("biz_type".equals(x.key))
     AND  #(x.key) = #(x.value)
    #end
  #end
  order by ipd_id
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
			FROM outer_pay_trans_checked che 
		WHERE che.delete_flag = 0 
			AND che.bill_id = ? ) che 
WHERE
	trans.is_checked = 1 
	AND trans.id = che.trans_id
#end

#sql("getBillById")
  SELECT
	ozp.id,
	ozp.pay_account_id,
	ozp.pay_account_no,
	ozp.pay_account_name,
	ozp.pay_account_bank,
	ozp.recv_account_id,
	ozp.recv_account_no,
	ozp.recv_account_name,
	ozp.recv_account_bank,
	ozp.payment_amount,
	ozp.payment_summary,
	ozp.service_status,
	ozp.service_serial_number,
	ozp.bank_serial_number,
	ozp.create_by,
	ozp.create_on,
	ozp.apply_on,
	ozp.update_by,
	ozp.update_on,
	ozp.delete_flag,
	ozp.org_id,
	ozp.dept_id,
	ozp.pay_account_cur,
	ozp.recv_account_cur,
	ozp.pay_bank_cnaps,
	ozp.recv_bank_cnaps,
	ozp.pay_bank_prov,
	ozp.recv_bank_prov,
	ozp.pay_bank_city,
	ozp.recv_bank_city,
	ozp.process_bank_type,
	ozp.persist_version,
	ozp.attachment_count,
	ozp.feed_back,
	ozp.biz_id,
	ozp.biz_name,
	ozp.repeat_count,
	ozp.instruct_code 
FROM
	outer_zf_payment ozp
where ozp.id = ?
#end

#sql("updBillById")
   update outer_zf_payment set bank_serial_number = ?,repeat_count = ?,service_status = ? ,instruct_code = ?
   where id =  ? and service_status = ? and repeat_count = ?
#end

#sql("tradingList")
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
	and amount = #para(map.payment_amount)
	and convert(varchar,trans_date)+' '+convert(varchar,trans_time) >= #para(map.apply_on)
#end


#sql("findBillById")
   SELECT 
     * 
   FROM
 outer_zf_payment
   WHERE 
   id = ?
   order by id desc
#end


#sql("findBillByTrade")
SELECT
	zft.*
FROM
	outer_zf_payment zft
WHERE 1=1 
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key))
            pay_account_no in(
              #for(y : map.pay_account_no)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
           #elseif("payment_amount".equals(x.key))
            payment_amount in(
              #for(y : map.payment_amount)
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
  order by zft.id desc
#end



 #sql("updateTradeByBill")
    update
      #(map.table_name)
    set
      #for(x : map.set)
        #if(for.index > 0)
          #(",")
        #end
        [#(x.key)] = #para(x.value)
      #end
      where 
      #for(y : map.where)
        #if(for.index > 0)
          and
        #end
        #if("trade_id".equals(y.key))
            id in(
              #for(z : y.value)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
      #end
  #end
#end

#sql("findAttachDetailToPage")
SELECT
	detail_id,
	batchno,
	info_id,
	recv_account_id,
	recv_account_no,
	recv_account_name,
	recv_account_cur,
	recv_account_bank,
	recv_bank_cnaps,
	recv_bank_prov,
	recv_bank_city,
	payment_amount,
	pay_status,
	memo,
	update_on,
	update_by,
	feed_back
FROM
	outer_batchpay_bus_attach_detail
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_status".equals(x.key))
              pay_status in(
                #for(y : map.pay_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
          #else
            #(x.key) = '#(x.value)'
          #end
        #end
    #end
  #end
  order by detail_id desc
#end

#sql("findAttachInfoByIDBatchno")
SELECT
	[number] as total_num,
	amount as total_amount
FROM
	outer_batchpay_bus_attach_info d
WHERE 1=1
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          and
          #(x.key) = '#(x.value)'
        #end
    #end
  #end
#end