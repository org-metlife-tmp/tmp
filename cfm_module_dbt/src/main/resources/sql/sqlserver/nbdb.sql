#sql("findInnerDbPaymentMoreList")
SELECT
	id,
	pay_account_id,
	pay_account_no,
	pay_account_name,
	pay_account_bank,
	recv_account_id,
	recv_account_no,
	recv_account_name,
	recv_account_bank,
	payment_amount,
	pay_mode,
	payment_type,
	payment_summary,
	service_status,
	service_serial_number,
	bank_serial_number,
	create_by,
	create_on,
	update_by,
	update_on,
	apply_on,
	delete_flag,
	org_id,
	dept_id,
	pay_account_cur,
	recv_account_cur,
	pay_bank_cnaps,
	recv_bank_cnaps,
	pay_bank_prov,
	recv_bank_prov,
	pay_bank_city,
	recv_bank_city,
	process_bank_type,
	persist_version,
	attachment_count,
	feed_back,
	biz_id,
	biz_name
FROM
	inner_db_payment
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by service_status,pay_mode asc , id desc
#end

#sql("findInnerDbPaymentAllListTotal")
SELECT
	SUM(dbt.payment_amount) as total_amount,
	COUNT(0) as total_num
FROM
	inner_db_payment dbt,organization org
WHERE dbt.org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) <= 0
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #elseif("org_id".equals(x.key))
            org.org_id >= #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findInnerDbPaymentDetailList")
SELECT
	dbt.id,
	dbt.pay_account_id,
	dbt.pay_account_no,
	dbt.pay_account_name,
	dbt.pay_account_bank,
	dbt.recv_account_id,
	dbt.recv_account_no,
	dbt.recv_account_name,
	dbt.recv_account_bank,
	dbt.payment_amount,
	dbt.pay_mode,
	dbt.payment_type,
	dbt.payment_summary,
	dbt.service_status,
	dbt.service_serial_number,
	dbt.bank_serial_number,
	dbt.create_by,
	dbt.create_on,
	dbt.apply_on,
	dbt.update_by,
	dbt.update_on,
	dbt.delete_flag,
	dbt.org_id,
	dbt.dept_id,
	dbt.pay_account_cur,
	dbt.recv_account_cur,
	dbt.pay_bank_cnaps,
	dbt.recv_bank_cnaps,
	dbt.pay_bank_prov,
	dbt.recv_bank_prov,
	dbt.pay_bank_city,
	dbt.recv_bank_city,
	dbt.process_bank_type,
	dbt.persist_version,
	dbt.attachment_count,
	dbt.feed_back,
	dbt.biz_id,
	dbt.biz_name
FROM
	inner_db_payment dbt,organization org
WHERE dbt.org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) <= 0
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #elseif("org_id".equals(x.key))
            org.org_id >= #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by service_status,pay_mode asc, id desc
#end

#sql("findInnerDbPaymentPayList")
SELECT
	id,
	pay_account_id,
	pay_account_no,
	pay_account_name,
	pay_account_bank,
	recv_account_id,
	recv_account_no,
	recv_account_name,
	recv_account_bank,
	payment_amount,
	pay_mode,
	payment_type,
	payment_summary,
	service_status,
	service_serial_number,
	bank_serial_number,
	create_by,
	create_on,
	apply_on,
	update_by,
	update_on,
	delete_flag,
	org_id,
	dept_id,
	pay_account_cur,
	recv_account_cur,
	pay_bank_cnaps,
	recv_bank_cnaps,
	pay_bank_prov,
	recv_bank_prov,
	pay_bank_city,
	recv_bank_city,
	process_bank_type,
	persist_version,
	attachment_count,
	feed_back,
	biz_id,
	biz_name
FROM
	inner_db_payment
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) <= 0
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by service_status,pay_mode asc, id desc
#end

#sql("findAccountByAccId")
SELECT
	a.acc_id,
	a.acc_no,
	a.acc_name,
	a.acc_attr,
	a.open_date,
	a.cancel_date,
	a.is_activity,
	a.status,
	a.curr_id,
	a.bank_cnaps_code,
	b.name as bank_name,
	b.bank_type as bank_type,
	b.cnaps_code as cnaps_code,
	b.province as province,
	b.city as city,
	c.name as curr_name,
	c.iso_code as curr_code,
	a.org_id,
	o.name as org_name,
	o.level_num as level_num,
	o.level_code as level_code,
	a.interactive_mode,
	a.is_virtual,
	a.lawfull_man
FROM
	account a,
	all_bank_info b,
	organization o,
	currency c
WHERE
	a.org_id = o.org_id
AND a.bank_cnaps_code = b.cnaps_code
AND a.curr_id = c.id
AND a.acc_id = ?
#end

#sql("findInnerPayMentPendingList")
SELECT
	idp.id as ipd_id,
	idp.pay_account_id,
	idp.pay_account_no,
	idp.pay_account_name,
	idp.pay_account_bank,
	idp.recv_account_id,
	idp.recv_account_no,
	idp.recv_account_name,
	idp.recv_account_bank,
	idp.payment_amount,
	idp.pay_mode,
	idp.payment_type,
	idp.payment_summary,
	idp.service_status,
	idp.service_serial_number,
	idp.bank_serial_number,
	idp.create_by,
	idp.create_on,
	idp.update_by,
	idp.update_on,
	idp.delete_flag,
	idp.org_id,
	idp.dept_id,
	idp.pay_account_cur,
	idp.recv_account_cur,
	idp.pay_bank_cnaps,
	idp.recv_bank_cnaps,
	idp.pay_bank_prov,
	idp.recv_bank_prov,
	idp.pay_bank_city,
	idp.recv_bank_city,
	idp.process_bank_type,
	idp.persist_version,
	idp.attachment_count,
	idp.feed_back,
	idp.biz_id,
	idp.biz_name,
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
	inner_db_payment idp,cfm_workflow_run_execute_inst cwrei
WHERE idp.id = cwrei.bill_id
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

#sql("getBillById")
  SELECT
	idp.id,
	idp.pay_account_id,
	idp.pay_account_no,
	idp.pay_account_name,
	idp.pay_account_bank,
	idp.recv_account_id,
	idp.recv_account_no,
	idp.recv_account_name,
	idp.recv_account_bank,
	idp.payment_amount,
	idp.pay_mode,
	idp.payment_type,
	idp.payment_summary,
	idp.service_status,
	idp.service_serial_number,
	idp.bank_serial_number,
	idp.create_by,
	idp.create_on,
	idp.update_by,
	idp.update_on,
	idp.delete_flag,
	idp.org_id,
	idp.dept_id,
	idp.pay_account_cur,
	idp.recv_account_cur,
	idp.pay_bank_cnaps,
	idp.recv_bank_cnaps,
	idp.pay_bank_prov,
	idp.recv_bank_prov,
	idp.pay_bank_city,
	idp.recv_bank_city,
	idp.process_bank_type,
	idp.persist_version,
	idp.attachment_count,
	idp.feed_back,
	idp.biz_id,
	idp.biz_name,
	idp.repeat_count,
	idp.instruct_code 
FROM
	inner_db_payment idp 
where idp.id = ?
#end

#sql("updBillById")
   update inner_db_payment set bank_serial_number = ?,repeat_count = ?,service_status = ?,instruct_code = ?
   where id = ? and repeat_count = ? and service_status = ?
#end
