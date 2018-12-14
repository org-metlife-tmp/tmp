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
	feed_back
FROM
	inner_db_payment
WHERE 1=1
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
            payment_amount <= #para(x.value)
          #elseif("max".equals(x.key))
            payment_amount >= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findInnerDbPaymentAllListTotal")
SELECT
	SUM(payment_amount) as total_amount,
	COUNT(0) as total_num
FROM
	inner_db_payment
WHERE 1=1
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
            payment_amount <= #para(x.value)
          #elseif("max".equals(x.key))
            payment_amount >= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findInnerDbPaymentDetailList")
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
	feed_back
FROM
	inner_db_payment
WHERE 1=1
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
            payment_amount <= #para(x.value)
          #elseif("max".equals(x.key))
            payment_amount >= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
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
	feed_back
FROM
	inner_db_payment
WHERE 1=1
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
            payment_amount <= #para(x.value)
          #elseif("max".equals(x.key))
            payment_amount >= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(create_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
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

#sql("chgDbPaymentByIdAndVersion")
update
  inner_db_payment
set
  #for(x : map.set)
    #if(x.value&&x.value!="")
      #if(for.index > 0)
        #(",")
      #end
      #(x.key) = #para(x.value)
    #end
  #end
  where
  #for(y : map.where)
    #if(y.value&&y.value!="")
      #if(for.index > 0)
        and
      #end
      #(y.key) = #para(y.value)
    #end
  #end
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
#end