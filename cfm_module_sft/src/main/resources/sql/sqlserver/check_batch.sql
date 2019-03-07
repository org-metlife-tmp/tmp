#sql("checkBatchLAlist")
SELECT
    GETDATE() AS visit_time ,
    pay.op_date ,
    pay.op_user_name ,
	pay.id AS pay_id ,
	pay.source_sys,
	pay.origin_id,
	pay.pay_code,
	pay.channel_id,
	pay.org_id,
	pay.org_code,
	pay.amount,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,	
	case pay.status  when '0' then '未提交' when '1' then '已提交' when '2' then '已拒绝' else '其他' end status,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.biz_type ,
	la.pay_mode ,
	la.pay_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name ,
	org2.name,
	channel.channel_code ,
	channel.channel_desc
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = pay.channel_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
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
            pay.status in(
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
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
         #else
           pay.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
  order by pay.pay_code asc
#end


#sql("checkBatchLAAmount")
SELECT
    SUM(pay.amount) as total_amount,
	COUNT(0) as total_num
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = pay.channel_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
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
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("amountstatus".equals(x.key))
            pay.status in(
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
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
         #else
           pay.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end




#sql("checkBatchLAFindAll")
SELECT
    SUM(pay.amount) as total_amount,
	COUNT(0) as total_num
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org 
WHERE
	pay.id = la.legal_id AND
	pay.org_id = org.org_id AND
	channel.id = pay.channel_id 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("recv_acc_no".equals(x.key))
        	pay.recv_acc_no = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
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
        #elseif("channel_id".equals(x.key))
            pay.channel_id = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("source_sys".equals(x.key))
           pay.source_sys = #para(x.value)
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
        #end
      #end
    #end
  #end
#end


#sql("checkBatchLADetail")
   select
    pay.op_date ,
    pay.op_user_name ,
    pay.id AS pay_id ,
	pay.source_sys,
	pay.origin_id,
	pay.pay_code,
	pay.channel_id,
	pay.org_id,
	pay.org_code,
	pay.amount,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,
	pay.recv_bank_type,
	pay.status,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.biz_type ,
	la.pay_mode ,
	la.pay_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name 
   from 
     pay_legal_data AS pay ,
     la_pay_legal_data_ext AS la
   where 
     pay.id = la.legal_id
     AND
     pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.pay_id)
     #end
     )
#end



#sql("checkBatchSumAmount")
   select 
	SUM(pay.amount)  AS  sumAmount
   from 
     pay_legal_data AS pay 
   where 
     pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.pay_id)
     #end
        )
#end



#sql("checkBatchEBSDetail")
   select 
    pay.op_date ,
    pay.op_user_name ,
    pay.id AS pay_id ,
	pay.source_sys,
	pay.origin_id,
	pay.pay_code,
	pay.channel_id,
	pay.org_id,
	pay.org_code,
	pay.amount,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_type,
	pay.recv_bank_name,
	pay.recv_acc_no,
	pay.recv_bank_type,
	pay.status ,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	ebs.id AS ebs_id,
	ebs.legal_id ,
	ebs.insure_type ,
	ebs.preinsure_bill_no ,
	ebs.insure_bill_no ,
	ebs.branch_bill_no ,
	ebs.biz_type ,
	ebs.pay_mode ,
	ebs.pay_date ,
	ebs.company_name ,
	ebs.company_customer_no ,
	ebs.biz_code ,
	ebs.sale_code ,
	ebs.sale_name ,
	ebs.op_code ,
	ebs.op_name 
   from 
     pay_legal_data AS pay ,
     ebs_pay_legal_data_ext AS ebs
   where 
     pay.id = ebs.legal_id
     AND
     pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.pay_id)
     #end
        )
#end


#sql("checkBatchEBSlist")
SELECT
    GETDATE() AS visit_time ,
    pay.op_date ,
    pay.op_user_name ,
	pay.id AS pay_id ,
	pay.source_sys,
	pay.origin_id,
	pay.pay_code,
	pay.channel_id,
	pay.org_id,
	pay.org_code,
	pay.amount,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,
	case pay.status  when '0' then '未提交' when '1' then '已提交' when '2' then '已拒绝' else '其他' end status,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	ebs.bank_key , 
	ebs.id AS ebs_id, 
	ebs.legal_id ,
	case ebs.insure_type when '1' then '团险' when '2' then '极短险' when '3' then '赠险' else '其他' end insure_type ,
	ebs.preinsure_bill_no ,
	ebs.insure_bill_no ,
	ebs.branch_bill_no ,
	ebs.biz_type ,
	ebs.pay_mode ,
	ebs.pay_date ,
	ebs.company_name ,
	ebs.company_customer_no ,
	ebs.biz_code ,
	ebs.sale_code ,
	ebs.sale_name ,
	ebs.op_code ,
	ebs.op_name ,
	channel.channel_code ,
	channel.channel_desc,
	org2.name
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = ebs.legal_id AND
	channel.id = pay.channel_id AND
	org.org_id = channel.org_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
       #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            ebs.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
        #elseif("bank_key".equals(x.key))
           ebs.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0
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
           pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by pay.pay_code  asc
#end


#sql("checkBatchEBSAmount")
SELECT
    SUM(pay.amount) as total_amount,
	COUNT(0) as total_num
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = ebs.legal_id AND
	channel.id = pay.channel_id AND
	org.org_id = channel.org_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
       #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            ebs.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("amountstatus".equals(x.key))
            pay.status in(
              #for(a : map.amountstatus)
                #if(for.index > 0)
                  #(",")
                #end
                #(a)
              #end
            )
        #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
        #elseif("bank_key".equals(x.key))
           ebs.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0
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
           pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end


#sql("checkBatchEBSFindAll")
SELECT
    SUM(pay.amount) as total_amount,
	COUNT(0) as total_num
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	ebs_pay_legal_data_ext AS  ebs 
WHERE
	pay.id = ebs.legal_id AND
	channel.id = pay.channel_id 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("recv_acc_no".equals(x.key))
        	pay.recv_acc_no =  #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            ebs.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("channel_id".equals(x.key))
            pay.channel_id = #para(x.value)
        #elseif("status".equals(x.key))
           pay.status = #para(x.value)
        #elseif("source_sys".equals(x.key))
           pay.source_sys = #para(x.value)
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0            
        #end
      #end
    #end
  #end
#end


#sql("findPendingList")
SELECT
    pbtm.id AS ipd_id,
	pbtm.source_sys ,
	pbtm.master_batchno,
	case channel.interactive_mode when '0' then '直连' when '1' then '报盘' else '其他' end inter_mode ,
	channel.channel_code ,
    channel.channel_desc ,
    pbtm.total_num ,
    pbtm.total_amount,
    pbtm.create_on ,
    pbtm.create_by ,
    pbtm.persist_version,
    pbtm.service_status,
    userinfo.name ,
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
	pay_batch_total_master pbtm,
	cfm_workflow_run_execute_inst cwrei ,
	channel_setting channel ,
	user_info  userinfo
WHERE
	pbtm.id = cwrei.bill_id
	and
	channel.id = pbtm.channel_id
	and 
	userinfo.usr_id = pbtm.create_by	
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
      AND #(x.key) = #(x.value)
    #end
  #end
  order by pbtm.id
#end


#sql("updateLegalByGroup")
   update
     pay_legal_data 
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
          #(y.pay_id)
     #end
        )
#end



#sql("updateDetailByTotal")
   update
     pay_batch_detail 
   set 
     delete_num = id
   where 
     master_batchno = ?
   and
     delete_num = ?   
#end


#sql("updateLegalDataByTotal")
   update
     pay_legal_data 
   set 
     status = 0 ,
     persist_version = persist_version + 1
   where 
     id 
     in
     (select legal_id from pay_batch_detail where master_batchno = ?
       and delete_num = ?) 
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


#sql("selectTotalByMaster")
   SELECT 
      pbt.id
   from 
     pay_batch_total AS pbt,
     pay_batch_total_master  AS pbtm
   where 
     pbt.master_batchno = pbtm.master_batchno
     AND
     pbtm.id = ?
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
	pay.id AS pay_id 
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = pay.channel_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("visit_time".equals(x.key))
              DATEDIFF(second,#para(x.value),pay.create_time) <= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            pay.status in(
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
            pay.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           pay.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end


#sql("checkBatchLAlistAmount_confirm")
SELECT
	SUM(pay.amount) AS total_amount_master 
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = pay.channel_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("visit_time".equals(x.key))
              DATEDIFF(second,#para(x.value),pay.create_time) <= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            pay.status in(
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
            pay.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           pay.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
#end



#sql("checkBatchEBSlist_confirm")
SELECT
	pay.id AS pay_id 
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = ebs.legal_id AND
	channel.id = pay.channel_id AND
	org.org_id = channel.org_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
       #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("visit_time".equals(x.key))
            DATEDIFF(second,#para(x.value),pay.create_time) <= 0
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("bank_key".equals(x.key))
           ebs.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0
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
            pay.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end


#sql("checkBatchEBSlistAmount_confirm")
SELECT
    SUM(pay.amount) AS total_amount_master
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org ,
	organization AS org2 
WHERE
	pay.id = ebs.legal_id AND
	channel.id = pay.channel_id AND
	org.org_id = channel.org_id AND
	org2.org_id = pay.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
       #if("channel_desc".equals(x.key))
        	channel.id = #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("visit_time".equals(x.key))
            DATEDIFF(second,#para(x.value),pay.create_time) <= 0
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("biz_type".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("bank_key".equals(x.key))
           ebs.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
           DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0
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
            pay.id not in(
              #for(n : map.remove_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(n)
              #end
            )
         #else
           pay.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end
