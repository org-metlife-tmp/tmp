#sql("findLAPayCounterList")
select 
    tab.* ,
    case isnull(gmf.service_status,'-1') when '2' then '审批中'  when '-1' then '未给付' when '7' then '给付成功'  when '5' then '审批拒绝' when '3' then '审批中'
         when '8' then '给付失败'   when '4' then '审批通过,给付中' when '6' then '审批通过,给付中'   end service_status ,
    gmf.actual_payment_date,
    gmf.id AS gmf_id,
    gmf.feed_back 
from  
 (
SELECT
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
	pay.consumer_acc_name,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,	
	pay.status ,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	pay.payment_summary,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.biz_type ,
	case la.pay_mode when '0' then '网银' when 'H' then '第三方' when 'Q' then '实时收付' when 'C' then '批量收付' end pay_mode,
	la.pay_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name ,
	org.name,
	biztype.type_name ,
	origin.create_time AS push_date
FROM
	pay_legal_data AS pay,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	la_biz_type AS biztype ,
	la_origin_pay_data AS origin
WHERE
	pay.id = la.legal_id AND
	org.org_id = pay.org_id AND
	biztype.type_code = la.biz_type AND
	origin.id = pay.origin_id AND
	biztype.[type] = 1 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("source_sys".equals(x.key))
        	pay.source_sys = #para(x.value)
        #elseif("type_code".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),origin.create_time) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),origin.create_time) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("org_ids".equals(x.key))
            pay.org_id in(
              #for(z : map.org_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("recv_acc_name".equals(x.key))
           pay.recv_acc_name = #para(x.value)
         #elseif("recv_cert_code".equals(x.key))
           pay.recv_cert_code = #para(x.value)
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  )  tab  left join gmf_bill gmf
     on gmf.legal_id = tab.pay_id
     and gmf.delete_num = 0
     #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        #if("service_status".equals(x.key))
         AND
        	gmf.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
       #end
      #end
    #end
  #end
  where 1=1
  #if(null != map.service_status_origin && 
       0 != map.service_status_origin && !"0".equals(map.service_status_origin))
	   and gmf.service_status is not null 
	#end
#end



#sql("findEBSPayCounterList")
select 
    tab.op_date ,
    tab.op_user_name ,
	tab.pay_id ,
	tab.source_sys,
	tab.origin_id,
	tab.pay_code,
	tab.channel_id,
	tab.org_id,
	tab.org_code,
	tab.amount,
	(case  tab.insure_type when  '1'  then  tab.op_name  else tab.consumer_acc_name end ) consumer_acc_name,
	tab.recv_acc_name,
	tab.recv_cert_type,
	(case  tab.insure_type when  '1'  then  tab.op_code  else  tab.recv_cert_code end ) recv_cert_code,
	tab.recv_bank_name,
	tab.recv_acc_no,	
	tab.status ,
	tab.process_msg,
	tab.bank_fb_msg,
	tab.create_time,
	tab.persist_version,
	tab.bank_key,
	tab.la_id,
	tab.legal_id ,
	tab.insure_type ,	
	tab.preinsure_bill_no ,
	tab.insure_bill_no ,
	CASE tab.biz_type  WHEN 1 THEN '定期结算退费' WHEN 5 THEN '理赔给付' WHEN 10 THEN '保全退费' WHEN 12 THEN
       '基金单满期退费'  WHEN 13 THEN '客户账户退费'  END type_name,
	tab.pay_mode ,
	tab.pay_date ,
	tab.sale_code ,
	tab.sale_name ,
	tab.op_code ,
	tab.op_name ,
	tab.company_name ,
	tab.company_customer_no ,
	tab.payment_summary,
	tab.biz_code ,
	tab.name,
	tab.push_date,
	case isnull(gmf.service_status,'-1') when '2' then '审批中'  when '-1' then '未给付' when '7' then '给付成功'  when '5' then '审批拒绝' when '3' then '审批中'
         when '8' then '给付失败'   when '4' then '审批通过,给付中' when '6' then '审批通过,给付中'   end service_status ,
    gmf.actual_payment_date  ,
    gmf.id AS gmf_id,
    gmf.feed_back 
from  
 (
SELECT
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
	pay.consumer_acc_name,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,	
	pay.status ,
	pay.process_msg,
	pay.payment_summary,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	ebs.bank_key,
	ebs.id AS la_id,
	ebs.legal_id ,
	ebs.insure_type ,	
	ebs.preinsure_bill_no ,
	ebs.insure_bill_no ,
	ebs.biz_type ,
	case ebs.pay_mode when '0' then '网银' when 'H' then '第三方' when 'Q' then '实时收付' when 'C' then '批量收付' end pay_mode,
	ebs.pay_date ,
	ebs.sale_code ,
	ebs.sale_name ,
	ebs.op_code ,
	ebs.op_name ,
	ebs.company_name ,
	ebs.company_customer_no ,
	ebs.biz_code ,
	org.name ,
	origin.create_time AS push_date
	FROM
	pay_legal_data AS pay,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org ,
	ebs_origin_pay_data AS origin 
WHERE
	pay.id = ebs.legal_id AND
	org.org_id = pay.org_id AND
	origin.id = pay.origin_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("source_sys".equals(x.key))
        	pay.source_sys = #para(x.value)
        #elseif("type_code".equals(x.key))
            ebs.biz_type = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),origin.create_time) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),origin.create_time) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            ebs.pay_mode = #para(x.value)
        #elseif("recv_cert_code".equals(x.key))
            (case ebs.insure_type  when  '1' then  ebs.company_customer_no  else  pay.recv_cert_code end) = #para(x.value)
        #elseif("biz_code".equals(x.key))
            ebs.biz_code  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("recv_acc_name".equals(x.key))
           pay.recv_acc_name = #para(x.value)
        #elseif("org_ids".equals(x.key))
            pay.org_id in(
              #for(z : map.org_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
         #else
           1 =1 
        #end
      #end
    #end
  #end
  )  tab  left join gmf_bill gmf
     on gmf.legal_id = tab.pay_id
     and gmf.delete_num = 0
     #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        #if("service_status".equals(x.key))
         AND
        	gmf.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
       #end
      #end
    #end
  #end
   where 1=1
  #if(null != map.service_status_origin && 
       0 != map.service_status_origin && !"0".equals(map.service_status_origin))
	   and gmf.service_status is not null 
	#end
#end




#sql("findDistinctStatus")
SELECT
  DISTINCT(status) AS status ,
  supply_status AS supply_status 
FROM
  pay_legal_data AS pay
WHERE 
pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y)
     #end
     )
#end


#sql("updateFileRef")
    update
    common_attachment_info_ref
    set 
    bill_id = ?
    where 
    biz_type = ?
    and 
    bill_id = ?
#end


#sql("findPendingList")
select
    tab.*
	from 
(SELECT
	gmf.id as ipd_id,
	gmf.org_id,
	gmf.source_sys,
	gmf.pay_account_no,
	gmf.pay_account_name,
	gmf.pay_account_cur,
	gmf.recv_account_no,
	gmf.recv_account_no AS recv_acc_no,
	gmf.recv_account_name,
	gmf.recv_bank_name,
	gmf.recv_bank_cnaps,
	gmf.persist_version,
	gmf.amount,
	gmf.service_status,
	gmf.create_on,
	gmf.create_by,
	gmf.bank_serial_number,
	gmf.service_serial_number,
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
	cwrei.submitter_name AS op_user_name,
	cwrei.submitter_pos_id,
	cwrei.submitter_pos_name,
	cwrei.init_user_id,
	cwrei.init_user_name,
	cwrei.init_org_id,
	cwrei.init_org_name,
	cwrei.init_dept_id,
	cwrei.init_dept_name,
	cwrei.start_time ,
	la.pay_date  AS pay_date,
	pay.pay_code AS pay_code,
	la.preinsure_bill_no AS preinsure_bill_no,
	la.insure_bill_no AS insure_bill_no,
	null AS biz_code ,
	biztype.type_name AS type_name,
	origin.create_time AS pay_date
FROM
	gmf_bill AS gmf,
	cfm_workflow_run_execute_inst AS cwrei ,
	la_pay_legal_data_ext AS  la ,
	la_biz_type AS biztype ,
	pay_legal_data AS pay,
	la_origin_pay_data AS origin
WHERE gmf.id = cwrei.bill_id  AND
      pay.id = la.legal_id AND 
	  biztype.type_code = la.biz_type AND
	  gmf.legal_id = pay.id AND
	  gmf.delete_num = 0	AND
	  biztype.[type] =1 AND
      origin.id = pay.origin_id	  
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
     AND  cwrei.#(x.key) = #(x.value)
    #end
  #end 
UNION ALL    
  SELECT
	gmf.id as ipd_id,
	gmf.org_id,
	gmf.source_sys,
	gmf.pay_account_no,
	gmf.pay_account_name,
	gmf.pay_account_cur,
	gmf.recv_account_no,
	gmf.recv_account_no AS recv_acc_no,
    case isnull(ebs.company_name,'') when '' then pay.recv_acc_name else ebs.company_name end  recv_account_name,
	gmf.recv_bank_name,
	gmf.recv_bank_cnaps,
	gmf.persist_version,
	gmf.amount,
	gmf.service_status,
	gmf.create_on,
	gmf.create_by,
	gmf.bank_serial_number,
	gmf.service_serial_number,
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
	cwrei.submitter_name AS op_user_name,
	cwrei.submitter_name,
	cwrei.submitter_pos_id,
	cwrei.submitter_pos_name,
	cwrei.init_user_id,
	cwrei.init_user_name,
	cwrei.init_org_id,
	cwrei.init_org_name,
	cwrei.init_dept_id,
	cwrei.init_dept_name,
	cwrei.start_time ,
	ebs.pay_date AS pay_date,
	pay.pay_code AS pay_code,
	ebs.preinsure_bill_no AS preinsure_bill_no,
	ebs.insure_bill_no AS insure_bill_no,
	ebs.biz_code AS biz_code,
	CASE ebs.biz_type  WHEN 1 THEN '定期结算退费' WHEN 5 THEN '理赔给付' WHEN 10 THEN '保全退费' WHEN 12 THEN
       '基金单满期退费'  WHEN 13 THEN '客户账户退费'  END type_name	,
    origin.create_time AS pay_date
FROM
	gmf_bill AS gmf,
	cfm_workflow_run_execute_inst AS cwrei ,
	ebs_pay_legal_data_ext AS  ebs ,
	pay_legal_data AS pay	,
	ebs_origin_pay_data AS origin
WHERE gmf.id = cwrei.bill_id  AND
      pay.id = ebs.legal_id AND 
	  gmf.legal_id = pay.id AND
	  gmf.delete_num = 0	AND
	  origin.id = pay.origin_id
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
     AND  cwrei.#(x.key) = #(x.value)
    #end
  #end
  UNION ALL    
  SELECT
	gmf.id as ipd_id,
	gmf.org_id,
	gmf.source_sys,
	gmf.pay_account_no,
	gmf.pay_account_name,
	gmf.pay_account_cur,
	gmf.recv_account_no,
	gmf.recv_account_no AS recv_acc_no,
    gmf.recv_account_name,
	gmf.recv_bank_name,
	gmf.recv_bank_cnaps,
	gmf.persist_version,
	gmf.amount,
	gmf.service_status,
	gmf.create_on,
	gmf.create_by,
	gmf.bank_serial_number,
	gmf.service_serial_number,
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
	cwrei.submitter_name AS op_user_name,
	cwrei.submitter_name,
	cwrei.submitter_pos_id,
	cwrei.submitter_pos_name,
	cwrei.init_user_id,
	cwrei.init_user_name,
	cwrei.init_org_id,
	cwrei.init_org_name,
	cwrei.init_dept_id,
	cwrei.init_dept_name,
	cwrei.start_time ,
	mat.refund_on AS pay_date,
	'' AS pay_code,
	'' AS preinsure_bill_no,
	'' AS insure_bill_no,
	'' AS biz_code,
	'待匹配收款退费' AS type_name	
FROM
	gmf_bill AS gmf,
	cfm_workflow_run_execute_inst AS cwrei ,
	recv_counter_match AS mat
WHERE gmf.id = cwrei.bill_id  AND
      mat.id = gmf.legal_id AND
	  gmf.delete_num = 0	  
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
     AND  cwrei.#(x.key) = #(x.value)
    #end
  #end
  ) tab  order by ipd_id 
#end



#sql("updateLaOriginData")
   update
   la_origin_pay_data  
   set 
   tmp_status = ? , 
   tmp_err_message  = ?  
   where
   id  = ? 
#end

#sql("updateEbsOriginData")
   update
   ebs_origin_pay_data  
   set 
   tmp_status = ? , 
   tmp_err_message  = ?,
   paybankaccno = ?,
   paybankcode = ? ,
   paydate = ? ,
   paytime = ? 
   where
     id  = ? 
#end

#sql("updBillById")
   update gmf_bill set bank_serial_number = ?,repeat_count = ?,service_status = ? ,instruct_code = ?,actual_payment_date = ?,send_on = ?
   where id =  ? and service_status = ? and repeat_count = ?
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
	pay.recv_bank_cnaps,
	pay.status,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	pay.payment_summary,
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
          #(y)
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
	pay.recv_bank_cnaps,
	pay.payment_summary,
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
          #(y)
     #end
        )
#end


#sql("findLaDetailById")
    select 
      gmf.* ,
      gmf.recv_account_no AS recv_acc_no ,
      biztype.type_name
    from
    gmf_bill AS gmf,
    pay_legal_data AS pay,
    la_pay_legal_data_ext AS la,
    la_biz_type AS biztype
    where 
     gmf.legal_id = pay.id AND
     pay.id = la.legal_id AND
     biztype.type_code = la.biz_type AND
     biztype.[type] = 1 AND
     gmf.id = ?
#end


#sql("findEBSDetailById")
    select 
      gmf.* ,
      gmf.recv_account_no AS recv_acc_no ,
      CASE ebs.biz_type  WHEN 1 THEN '定期结算退费' WHEN 5 THEN '理赔给付' WHEN 10 THEN '保全退费' WHEN 12 THEN
      '基金单满期退费'  WHEN 13 THEN '客户账户退费'  END type_name
    from
    gmf_bill AS gmf,
    pay_legal_data AS pay,
    ebs_pay_legal_data_ext AS ebs
    where 
     gmf.legal_id = pay.id AND
     pay.id = ebs.legal_id AND
     gmf.id = ?
#end

#sql("updateDeleteByLegal")
    update
    gmf_bill 
    set 
    gmf_bill.delete_num = gmf_bill.id
    where
    gmf_bill.legal_id = ?
    and
    gmf_bill.delete_num = ?
#end




#sql("findTMPPayCounterList")
select 
    tab.* ,
    case isnull(gmf.service_status,'-1') when '2' then '审批中'  when '-1' then '未给付' when '7' then '给付成功'  when '5' then '审批拒绝' when '3' then '审批中'
         when '8' then '给付失败'   when '4' then '审批通过,给付中' when '6' then '审批通过,给付中'   end service_status ,
    gmf.actual_payment_date,
    gmf.id AS gmf_id ,
    '待匹配收款退费' AS type_name ,
    '网银' AS pay_mode
from  
 (
SELECT
	org.name,
	mat.refund_on AS push_date,
	mat.id ,
	mat.amount ,
	mat.payer AS consumer_acc_name ,
	mat.payer_cer_no AS recv_cert_code,
	mat.match_recv_acc_name AS recv_acc_name ,
	mat.match_recv_acc_no AS recv_acc_no ,
	mat.match_recv_bank_name AS recv_bank_name ,
	mat.id AS pay_id ,
	mat.recv_bank_cnaps,
	mat.refund_acc_no,
	mat.payment_summary,
	mat.fefund_bank_name,
	mat.refund_on,
	mat.refund_user_name,
	mat.match_status,
	mat.persist_version,
	mat.delete_flag,
	mat.update_by,
	mat.update_on,
	mat.create_on,
	mat.create_by,
	mat.supply_status,
	mat.op_date,
	mat.op_user_name,
	mat.status,
	mat.recv_date,
	mat.recv_org_id
FROM
    recv_counter_match AS mat ,
	organization AS org 
WHERE
	org.org_id = mat.recv_org_id 
	and 
	mat.refund_on is not null
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),mat.refund_on) >= 0
        #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),mat.refund_on) <= 0
        #elseif("status".equals(x.key))
            mat.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
         #elseif("match_status".equals(x.key))
            mat.match_status in(
              #for(y : map.match_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
         #elseif("recv_acc_name".equals(x.key))
           mat.payer = #para(x.value)
         #elseif("recv_cert_code".equals(x.key))
           mat.payer_cer_no = #para(x.value)
         #elseif("org_id".equals(x.key))
           mat.recv_org_id = #para(x.value)
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  )  tab  left join gmf_bill gmf
     on gmf.legal_id = tab.pay_id
     and gmf.source_sys = 3 
     and gmf.delete_num = 0
     #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        #if("service_status".equals(x.key))
         AND
        	gmf.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
       #end
      #end
    #end
  #end
  where 1=1
  #if(null != map.service_status_origin && 
       0 != map.service_status_origin && !"0".equals(map.service_status_origin))
	   and gmf.service_status is not null 
	#end
#end


#sql("findTMPDetailById")
    select 
      gmf.* ,
      gmf.recv_account_no AS recv_acc_no ,
      '待匹配收款退费' AS type_name
    from
    gmf_bill AS gmf,
    recv_counter_match AS mat 
    where 
     gmf.legal_id = mat.id  and
     gmf.id = ?
#end

#sql("findDistinctTMPStatus")
SELECT
  DISTINCT(status) AS status ,
  supply_status AS supply_status 
FROM
  recv_counter_match AS mat
WHERE 
mat.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y)
     #end
     )
#end


#sql("checkBatchTMPDetail")
   select
     mat.*
   from 
     recv_counter_match AS mat
   where 
     mat.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y)
     #end
     )
#end

#sql("getSonOrg")
     SELECT 
      org2.org_id,
      org2.name as org_name
    FROM
      organization org
    JOIN organization org2 ON charindex(
      org.level_code,
      org2.level_code
    ) = 1
    WHERE
      org.org_id = ? 
#end

#sql("gettypecode")
     SELECT 
      *
    FROM
      la_biz_type
    WHERE
      [type] = ? 

#end