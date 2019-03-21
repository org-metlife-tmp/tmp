#sql("findLAPayCounterList")
select 
    tab.* ,
    case isnull(gmf.service_status,'-1')  when '-1' then '0' when '7' then '4'  when '5' then '2' when '3' then '1'
         when '8' then '5'   when '4' then '3'   end service_status ,
    gmf.actual_payment_date
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
	org.name,
	biztype.type_name
FROM
	pay_legal_data AS pay,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	la_biz_type AS biztype 
WHERE
	pay.id = la.legal_id AND
	org.org_id = pay.org_id AND
	biztype.type_code = la.biz_type 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("source_sys".equals(x.key))
        	pay.source_sys = #para(x.value)
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
	tab.recv_acc_name,
	tab.recv_cert_type,
	case  tab.insure_type when  '1'  then  tab.company_customer_no  else  tab.recv_cert_code end  recv_cert_code,
	tab.recv_bank_name,
	tab.recv_acc_no,	
	tab.status ,
	tab.process_msg,
	tab.bank_fb_msg,
	tab.create_time,
	tab.persist_version,
	tab.bank_key,
	tab.id AS la_id,
	tab.legal_id ,
	tab.insure_type ,	
	tab.branch_code ,
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
	tab.biz_code ,
	tab.name,
	tab.channel_code ,
	tab.channel_desc ,
	tab.type_name,
    case isnull(gmf.service_status,'-1')  when '-1' then '0' when '7' then '4'  when '5' then '2' when '3' then '1'
         when '8' then '5'   when '4' then '3'   end service_status ,
    gmf.actual_payment_date  
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
	ebs.bank_key,
	ebs.id AS la_id,
	ebs.legal_id ,
	ebs.insure_type ,	
	ebs.branch_code ,
	ebs.preinsure_bill_no ,
	ebs.insure_bill_no ,
	ebs.biz_type ,
	ebs.pay_mode ,
	ebs.pay_date ,
	ebs.sale_code ,
	ebs.sale_name ,
	ebs.op_code ,
	ebs.op_name ,
	ebs.company_name ,
	ebs.company_customer_no ,
	ebs.biz_code ,
	org.name,
	biztype.type_name
FROM
	pay_legal_data AS pay,
	ebs_pay_legal_data_ext AS  ebs ,
	organization AS org 
WHERE
	pay.id = ebs.legal_id AND
	org.org_id = pay.org_id 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("source_sys".equals(x.key))
        	pay.source_sys = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),ebs.pay_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),ebs.pay_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            ebs.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            ebs.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            ebs.pay_mode = #para(x.value)
        #elseif("recv_cert_code".equals(x.key))
            (case ebs.insure_type  when  '1' then  ebs.company_customer_no  else  pay.recv_cert_code ) = #para(x.value)
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
  )  tab  left join gmf_bill gmf
     on gmf.legal_id = tab.pay_id
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
#end




#sql("findDistinctStatus")
SELECT
  DISTINCT(status) AS status
FROM
  pay_legal_data AS pay
WHERE 
pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.pay_id)
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
SELECT
	gmf.id as ipd_id,
	gmf.org_id,
	gmf.source_sys,
	gmf.pay_account_no,
	gmf.pay_account_name,
	gmf.pay_account_cur,
	gmf.recv_account_no,
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
	gmf_bill gmf,cfm_workflow_run_execute_inst cwrei
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