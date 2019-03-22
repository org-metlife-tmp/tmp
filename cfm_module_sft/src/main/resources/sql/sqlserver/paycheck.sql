#sql("paylist")
SELECT
	pay.*,
	master.is_inner,
	master.net_mode,
	channel.channel_code,
	channel.channel_desc
FROM
	pay_batch_total pay,
	pay_batch_total_master master,
	channel_setting channel
WHERE master.channel_id = channel.id
	and pay.master_batchno = master.master_batchno
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("service_status".equals(x.key))
          pay.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("is_checked".equals(x.key))
          pay.is_checked in(
            #for(y : map.is_checked)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("channel_id_one".equals(x.key))
          master.channel_id = #para(x.value)
        #elseif("channel_id_two".equals(x.key))
          master.channel_id = #para(x.value)
        #elseif("source_sys".equals(x.key))
          master.source_sys = #para(x.value)
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,pay.send_on,110)) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,pay.send_on,110)) <= 0
        #else
          pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by pay.id desc
#end

#sql("tradingList")
SELECT
  his.id,
  his.acc_id,
  his.acc_no,
  his.acc_name,
  case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
  his.opp_acc_no,
  his.opp_acc_name,
  his.amount,
  his.summary,
  convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
  his.is_checked,
  his.business_check,
	his.check_service_number,
	his.check_user_name,
	his.check_date,
	his.opp_acc_bank,
	bank.name bank_name,
	acc.bankcode
FROM
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	his.acc_id = acc.acc_id
	AND acc.bank_cnaps_code = bank.cnaps_code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("summary".equals(x.key))
          his.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("bankcode".equals(x.key))
          acc.#(x.key) = #para(x.value)
        #elseif("is_checked".equals(x.key))
          his.is_checked in(
            #for(y : map.is_checked)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("business_check".equals(x.key))
          his.business_check in(
            #for(y : map.business_check)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("min".equals(x.key))
          his.amount >= #para(x.value)
        #elseif("max".equals(x.key))
          his.amount <= #para(x.value)
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,his.trans_date,110)) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,his.trans_date,110)) <= 0
        #else
          his.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("getladetailbybatchno")
SELECT
	master.source_sys,
	org.name org_name,
	detail.recv_bank_name,
	detail.recv_cert_code,
	detail.recv_acc_name,
	detail.recv_acc_no,
	detail.amount,
	ext.pay_date,
	case detail.status when 0 then '未处理' when 1 then '成功' when 2 then '失败' when '3' then '处理中' end status
FROM
	pay_batch_detail detail,
	pay_batch_total total,
	pay_batch_total_master master,
	organization org,
	pay_legal_data leg,
	la_pay_legal_data_ext ext
WHERE detail.child_batchno = total.child_batchno
and total.master_batchno = master.master_batchno
and detail.delete_num = 0
and detail.org_code = org.code
and detail.legal_id = leg.id
and leg.origin_id = ext.origin_id
and detail.base_id = ?
  order by detail.id desc
#end

#sql("getebsdetailbybatchno")
SELECT
	master.source_sys,
	org.name org_name,
	detail.recv_bank_name,
	detail.recv_cert_code,
	detail.recv_acc_name,
	detail.recv_acc_no,
	detail.amount,
	ext.pay_date,
	case detail.status when 0 then '未处理' when 1 then '成功' when 2 then '失败' when '3' then '处理中' end status
FROM
	pay_batch_detail detail,
	pay_batch_total total,
	pay_batch_total_master master,
	organization org,
	pay_legal_data leg,
	ebs_pay_legal_data_ext ext
WHERE detail.child_batchno = total.child_batchno
and total.master_batchno = master.master_batchno
and detail.org_code = org.code
and detail.delete_num = 0
and detail.legal_id = leg.id
and leg.origin_id = ext.origin_id
and detail.base_id = ?
  order by detail.id desc
#end

#sql("findBatchList")
SELECT
	pay.*,
	master.is_inner,
	master.channel_id,
	channel.net_mode,
	channel.channel_code,
	channel.channel_desc
FROM
	pay_batch_total pay,
	pay_batch_total_master master,
	channel_setting channel
WHERE master.channel_id = channel.id
	and pay.master_batchno = master.master_batchno
	and pay.id in (
    #for(x : batchNo)
      #(for.index == 0 ? "" : ",") #para(x)
    #end
	)
#end

#sql("findMaster")
SELECT
	master.*
FROM
	pay_batch_total pay,
	pay_batch_total_master master
WHERE pay.master_batchno = master.master_batchno
	and pay.id = ?
#end

#sql("findTradList")
select * from acc_his_transaction where id in(
  #for(x : tradingNo)
    #(for.index == 0 ? "" : ",") #para(x)
  #end
)
#end

#sql("findTradListBusiness")
select * from acc_his_transaction his
where 1 = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("tradingNo".equals(x.key))
          his.id in(
            #for(y : map.tradingNo)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #else
          his.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("gettradbybatchno")
SELECT
	his.id,
	his.acc_id,
	his.acc_no,
	his.acc_name,
	case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
	his.opp_acc_no,
	his.opp_acc_name,
	his.amount,
	his.summary,
	convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
	bank.name bank_name
FROM
	pay_batch_checked pay,
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	pay.trans_id = his.id
	and his.acc_id = acc.acc_id
	and acc.bank_cnaps_code = bank.cnaps_code
	and pay.batch_id = ?
#end


#sql("findbatchdetail")
SELECT
	detail.*,
	master.channel_id,
	master.net_mode
FROM
	pay_batch_detail detail,
	pay_batch_total child,
	pay_batch_total_master master
WHERE detail.child_batchno = child.child_batchno
and child.master_batchno = master.master_batchno
and detail.delete_num = 0
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("batchid".equals(x.key))
        	  detail.base_id in(
              #for(y : map.batchid)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #else
          detail.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findbatch")
SELECT
  *
FROM
  pay_batch_total
WHERE 1 = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("batchid".equals(x.key))
            id in(
              #for(y : map.batchid)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #else
          pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findaccount")
SELECT
  *
FROM
  account
WHERE bankcode = ?
#end



