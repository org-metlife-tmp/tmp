#sql("paylist")
SELECT
	recv.*,
	channel.channel_code,
	channel.channel_desc
FROM
	recv_batch_total recv,
	recv_batch_total_master master,
	channel_setting channel
WHERE master.channel_id = channel.id
	and recv.master_batchno = master.master_batchno
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("service_status".equals(x.key))
          recv.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("is_checked".equals(x.key))
          recv.is_checked in(
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
          DATEDIFF(day,#para(x.value),CONVERT(DATE,recv.send_on,110)) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,recv.send_on,110)) <= 0
        #else
          recv.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by recv.id desc
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
	his.acc_no = acc.acc_no
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

#sql("findBatchList")
SELECT
	recv.*,
	master.channel_id,
	channel.net_mode,
	channel.channel_code,
	channel.channel_desc
FROM
	recv_batch_total recv,
	recv_batch_total_master master,
	channel_setting channel
WHERE master.channel_id = channel.id
	and recv.master_batchno = master.master_batchno
	and recv.id in (
    #for(x : batchNo)
      #(for.index == 0 ? "" : ",") #para(x)
    #end
	)
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

#sql("getladetailbybatchno")
SELECT
	master.source_sys,
	org.name org_name,
	detail.pay_bank_name,
	detail.pay_cert_code,
	detail.pay_acc_name,
	detail.pay_acc_no,
	detail.amount,
	ext.recv_date,
	case detail.status when 0 then '未处理' when 1 then '成功' when 2 then '失败' when '3' then '处理中' end status
FROM
	recv_batch_detail detail,
	recv_batch_total total,
	recv_batch_total_master master,
	organization org,
	recv_legal_data leg,
	la_recv_legal_data_ext ext
WHERE detail.child_batchno = total.child_batchno
and total.master_batchno = master.master_batchno
and detail.org_code = org.code
and detail.legal_id = leg.id
and leg.origin_id = ext.origin_id
and detail.base_id = ?
  order by detail.id desc
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
	recv_batch_checked recv,
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	recv.trans_id = his.id
	and his.acc_id = acc.acc_id
	and acc.bank_cnaps_code = bank.cnaps_code
	and recv.batch_id = ?
#end

#sql("findbatchdetail")
SELECT
	detail.*,
	master.channel_id
FROM
	recv_batch_detail detail,
	recv_batch_total child,
	recv_batch_total_master master
WHERE detail.child_batchno = child.child_batchno
and child.master_batchno = master.master_batchno
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

