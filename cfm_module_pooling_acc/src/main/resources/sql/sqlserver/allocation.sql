#sql("findMoreList")
SELECT
	pt.id,
	pt.topic,
	pt.allocation_type,
	pt.allocation_amount,
	pt.allocation_frequency,
	pt.allocation_child_account_count,
	pt.service_status,
	pt.is_activity,
	pt.persist_version as persist_version,
	pe.allocation_id,
	pe.execute_time,
	pe.allocation_amount as execute_amount,
	pe.success_count,
	pe.fail_count,
	pe.allocation_status as execute_status
FROM
	allocation_topic pt
LEFT JOIN allocation_execute pe ON
	( pt.execute_id = pe.id )
LEFT JOIN allocation_main_account main ON
  (pt.id = main.allocation_id)
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            pt.topic like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("service_status".equals(x.key))
            pt.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("is_activity".equals(x.key))
            pt.is_activity in(
              #for(y : map.is_activity)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("allocationids".equals(x.key))
            main.main_acc_id in(
              #for(y : map.allocationids)
                #if(for.index > 0)
                  #(",")
                #end
                #(y.main_acc_id)
              #end
            )
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by pt.service_status asc,pt.id desc
#end

#sql("findViewList")
select
	a.id,
	a.topic,
	a.allocation_type,
	a.allocation_amount,
	a.allocation_frequency,
	a.allocation_child_account_count,
	a.service_status,
	a.is_activity,
	a.persist_version,
	a.execute_id,
	b.allocation_id,
	b.execute_time,
	b.allocation_amount as execute_amount,
	b.success_count,
	b.fail_count,
	b.allocation_status as execute_status
from
	allocation_topic a
left join allocation_execute b on
	a.execute_id = b.id
	LEFT JOIN allocation_main_account main ON
  (a.id = main.allocation_id)
LEFT JOIN organization o ON
	 (a.create_org_id = o.org_id)
where 1=1
	#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            a.topic like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("service_status".equals(x.key))
            a.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("is_activity".equals(x.key))
            a.is_activity in(
              #for(y : map.is_activity)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("allocationids".equals(x.key))
            main.main_acc_id in(
              #for(y : map.allocationids)
                #if(for.index > 0)
                  #(",")
                #end
                #(y.main_acc_id)
              #end
            )
          #elseif("level_code".equals(x.key))
            o.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            o.level_num >= #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by a.service_status asc, a.id desc
#end

#sql("findMainAccount")
SELECT
	id,
	tab,
	allocation_id,
	main_acc_org_id,
	main_acc_org_name,
	main_acc_id,
	main_acc_no,
	main_acc_name,
	main_acc_bank_name,
	main_acc_bank_cnaps_code,
	child_account_count
FROM
	allocation_main_account
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #end
    #end
  #end
#end

#sql("findAccount")
SELECT
	acc.acc_id,
	acc.acc_no,
	acc.acc_name,
	acc.acc_attr,
	acc.acc_purpose,
	acc.open_date,
	acc.lawfull_man,
	acc.cancel_date,
	acc.curr_id,
	acc.bank_cnaps_code,
	acc.org_id,
	acc.interactive_mode,
	acc.memo,
	acc.is_activity,
	acc.is_virtual,
	acc.status,
	acc.is_close_confirm,
	acc.deposits_mode,
	acc.subject_code,
	org.org_id,
	org.name as org_name,
	bank.name as bank_name,
	bank.province as province,
	bank.city as city,
	curr.name as curr_name,
	curr.id as curr_id,
	curr.iso_code as iso_code
FROM
	account acc,organization org,all_bank_info bank,currency curr
WHERE 1=1 AND acc.org_id = org.org_id AND acc.bank_cnaps_code = bank.cnaps_code AND acc.curr_id = curr.id and acc.status =1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("notin".equals(x.key))
              #if(map.notin != null)
                acc.acc_id NOT IN (
                  #for(y : map.notin)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(y)
                  #end
                )
              #end
            #elseif("org_id".equals(x.key))
              acc.org_id =  #para(x.value)
            #elseif("acc_id".equals(x.key))
              acc.acc_id =  #para(x.value)
            #elseif("query_key".equals(x.key))
              acc.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #else
              #(x.key) = #para(x.value)
            #end
        #end
    #end
  #end
#end

#sql("findTimesettingByAlloId")
SELECT
	id,
	allocation_id,
	allocation_frequency,
	allocation_frequency_detail,
	allocation_time,
	allocation_time_cron
FROM
	allocation_timesetting
WHERE allocation_id = ?
#end

#sql("findMainAccByAlloId")
SELECT
	id,
	tab,
	allocation_id,
	main_acc_org_id,
	main_acc_org_name,
	main_acc_id,
	main_acc_no,
	main_acc_name,
	main_acc_bank_name,
	main_acc_bank_cnaps_code,
	child_account_count
FROM
	allocation_main_account
WHERE allocation_id = ?
#end

#sql("findChildAccByAlloId")
SELECT
	id,
	allocation_id,
	allocation_main_account_id,
	child_acc_org_id,
	child_acc_org_name,
	child_acc_id,
	child_acc_no,
	child_acc_name,
	child_acc_bank_name,
	child_acc_bank_cnaps_code
FROM
	allocation_child_account
WHERE allocation_id = ?
AND allocation_main_account_id = ?
ORDER BY allocation_id
#end

#sql("findChildAccInfo")
SELECT
	acc.acc_id as child_acc_id,
	acc.acc_no as child_acc_no,
	acc.acc_name as child_acc_name,
	acc.acc_attr,
	acc.open_date,
	acc.cancel_date,
	acc.is_activity,
	acc.status,
	acc.curr_id,
	acc.org_id,
	acc.interactive_mode,
	acc.is_virtual,
	b.name AS child_acc_bank_name,
	b.cnaps_code,
	b.province,
	b.city,
	o.name AS org_name,
	o.level_code
FROM
	account acc,
	all_bank_info b,
	organization o
WHERE
	acc.bank_cnaps_code = b.cnaps_code
	AND acc.org_id = o.org_id
	and acc.status =1
	#if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
          #if("notin".equals(x.key))
            #if(map.notin != null)
              acc.acc_id NOT IN (
                #for(y : map.notin)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(y)
                #end
              )
            #end
          #elseif("level_code".equals(x.key))
            o.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("acc_name".equals(x.key))
            acc.acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("acc_attr".equals(x.key))
            acc.#(x.key) = #para(x.value)
          #elseif("bank_type".equals(x.key))
            b.bank_type = #para(x.value)
          #elseif("not_org_num".equals(x.key))
            o.level_num > #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
      #end
    #end
  #end
	ORDER BY o.org_id desc
#end

#sql("delTimesettingByAllocationId")
DELETE FROM allocation_timesetting WHERE allocation_id = ?
#end

#sql("delMainAccByAllocationId")
DELETE FROM allocation_main_account WHERE allocation_id = ?
#end

#sql("delChildAccByAllocationId")
DELETE FROM allocation_child_account WHERE allocation_id = ?
#end

#sql("findAllocationTopicByIDVersion")
SELECT
	id,
	service_serial_number,
	topic,
	allocation_type,
	allocation_amount,
	allocation_frequency,
	allocation_main_account_count,
	allocation_child_account_count,
	service_status,
	summary,
	is_activity,
	execute_id,
	create_by,
	create_on,
	update_by,
	update_on,
	create_org_id,
	delete_flag,
	persist_version,
	attachment_count
FROM
	allocation_topic
WHERE id = ?
AND persist_version = ?
#end

#sql("findAllocationPendingList")
SELECT
	aot.id,
	aot.service_serial_number,
	aot.topic,
	aot.allocation_type,
	aot.allocation_amount,
	aot.allocation_frequency,
	aot.allocation_main_account_count,
	aot.allocation_child_account_count,
	aot.service_status,
	aot.summary,
	aot.is_activity,
	aot.execute_id,
	aot.create_by,
	CONVERT(DATE,aot.create_on,110) as create_on,
	aot.update_by,
	aot.update_on,
	aot.create_org_id,
	aot.delete_flag,
	aot.persist_version,
	aot.attachment_count,
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
	allocation_topic aot,cfm_workflow_run_execute_inst cwrei
WHERE aot.id = cwrei.bill_id
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
  order by aot.id
#end