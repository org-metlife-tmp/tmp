#sql("recvcheckBatchLAlist")
SELECT
    GETDATE() AS visit_time ,
    recv.op_date ,
    recv.op_user_name ,
	recv.id AS recv_id ,
	case recv.source_sys when '0' then 'LA' when '1' then 'EBS' else '其他' end source_sys,
	recv.origin_id,
	recv.pay_code,
	recv.channel_id,
	recv.org_id,
	recv.org_code,
	recv.amount,
	recv.pay_acc_name,
	recv.pay_cert_type,
	recv.pay_cert_code,
	recv.pay_bank_type,
	recv.pay_bank_name,
	recv.pay_acc_no,	
	case recv.status  when '0' then '未提交' when '1' then '已提交' when '2' then '已拒绝' else '其他' end status,
	recv.process_msg,
	recv.bank_fb_msg,
	recv.create_time,
	recv.persist_version,
	case la.biz_type  when '0' then '首期' when '1' then '续期'  else '其他'  end  biz_type ,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.pay_mode ,
	la.recv_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name ,
	org2.name,
	channel.channel_code ,
	channel.channel_desc
FROM
	recv_legal_data AS recv,
	channel_setting AS channel,
	la_recv_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	recv.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = recv.channel_id AND
	org2.org_id = recv.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.recv_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.recv_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            recv.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("codes".equals(x.key))
            org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #else
           recv.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
  order by recv.pay_code asc
#end


#sql("checkBatchLAAmount")
SELECT
    SUM(recv.amount) as total_amount,
	COUNT(0) as total_num
FROM
	recv_legal_data AS recv,
	channel_setting AS channel,
	la_recv_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	recv.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = recv.channel_id AND
	org2.org_id = recv.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.recv_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.recv_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            recv.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("amountstatus".equals(x.key))
            recv.status in(
              #for(a : map.amountstatus)
                #if(for.index > 0)
                  #(",")
                #end
                #(a)
              #end
            )
        #elseif("codes".equals(x.key))
            org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #else
           recv.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end



#sql("checkBatchLADetail")
   select
    recv.op_date ,
    recv.op_user_name ,
    recv.id AS recv_id ,
	recv.source_sys,
	recv.origin_id,
	recv.pay_code,
	recv.channel_id,
	recv.org_id,
	recv.org_code,
	recv.amount,
	recv.pay_acc_name,
	recv.pay_cert_type,
	recv.pay_cert_code,
	recv.pay_bank_name,
	recv.pay_acc_no,
	recv.pay_bank_type,
	recv.status,
	recv.process_msg,
	recv.bank_fb_msg,
	recv.create_time,
	recv.persist_version,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.biz_type ,
	la.pay_mode ,
	la.recv_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name 
   from 
     recv_legal_data AS recv ,
     la_recv_legal_data_ext AS la
   where 
     recv.id = la.legal_id
     AND
     recv.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.recv_id)
     #end
     )
#end



#sql("checkBatchSumAmount")
   select 
	SUM(recv.amount)  AS  sumAmount
   from 
     recv_legal_data AS recv 
   where 
     recv.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.recv_id)
     #end
        )
#end



#sql("updateLegalByGroup")
   update
     recv_legal_data 
   set
     op_date = #para(map.op_date) ,
     op_user_name = #para(map.op_user_name),
     status =  1 ,
     persist_version = persist_version + 1
   where 
      id  in (
      #for(y : map.ids)
          #if(for.index > 0)
            #(",")
          #end
          #(y.recv_id)
     #end
        )
#end




#sql("selectChannelCodeList")
   SELECT 
     channel.* ,
     channel.id AS channel_id
   from 
     channel_setting AS channel ,
     channel_online AS online
   where 
     channel.is_checkout = 1
     and
     online.channel_code = channel.channel_code
     order by channel.id asc
#end 




#sql("selectSonByMasterno")
   SELECT 
      pbtm.id AS pay_master_id ,
      case pbtm.source_sys  when '0' then 'LA' when '1' then 'EBS' else '其他' end source_sys,
      pbtm.master_batchno ,
      pbt.id AS pay_id ,
      pbt.child_batchno ,
      pbtm.create_on,
      pbt.total_num,
      pbt.total_amount,
      pbtm.create_by ,
      userinfo.name ,
      channel.channel_code ,
	  channel.channel_desc ,
	  case channel.interactive_mode when '0' then '直连' when '1' then '报盘' else '其他' end interactive_mode
   from 
     pay_batch_total AS pbt,
     pay_batch_total_master  AS pbtm ,
     channel_setting AS channel ,
     user_info AS userinfo
   where 
     pbt.master_batchno = pbtm.master_batchno
     AND
     channel.id = pbtm.channel_id
     AND
     userinfo.usr_id = pbtm.create_by
     AND
     pbtm.master_batchno = ? 
#end 



#sql("checkBatchLAlist_confirm")
SELECT
	recv.id AS recv_id 
FROM
	recv_legal_data AS recv,
	channel_setting AS channel,
	la_recv_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	recv.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = recv.channel_id AND
	org2.org_id = recv.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.recv_date) >= 0
        #elseif("visit_time".equals(x.key))
              DATEDIFF(second,#para(x.value),recv.create_time) <= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.recv_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            recv.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("codes".equals(x.key))
            org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("remove_ids".equals(x.key))
            recv.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           recv.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end


#sql("checkBatchLAlistAmount_confirm")
SELECT
	SUM(recv.amount) AS total_amount_master 
FROM
	recv_legal_data AS recv,
	channel_setting AS channel,
	la_recv_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	recv.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = recv.channel_id AND
	org2.org_id = recv.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.recv_date) >= 0
        #elseif("visit_time".equals(x.key))
              DATEDIFF(day,#para(x.value),recv.create_time) <= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.recv_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            recv.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("codes".equals(x.key))
            org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("remove_ids".equals(x.key))
            recv.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           recv.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end





