###账户冻结/解冻通用分页查询sql
#sql("getPage")
SELECT
  afd.id as id,
  afd.acc_id,
  afd.memo,
  afd.type,
  afd.create_by,
  afd.update_on,
  afd.update_by,
  afd.service_serial_number,
  afd.service_status,
  afd.delete_flag,
  afd.persist_version,
  afd.attachment_count,
  afd.feedback,
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
  acc.is_activity,
  acc.is_virtual,
  acc.status,
  DATE_FORMAT(afd.`create_on`, '%Y-%m-%d') AS create_on,
  DATE_FORMAT(afd.`apply_on`, '%Y-%m-%d') AS apply_on,
  org.name AS org_name,
  acc.lawfull_man,
  curr.name AS curr_name,
  curr.iso_code,
  bank.`name` AS bank_name,
  bank.province,
  bank.city,
  (select `value` from category_value where cat_code = 'acc_purpose' and `key` = acc.acc_purpose) as acc_purpose_name,
  (select `value` from category_value where cat_code = 'acc_attr' and `key` = acc.acc_attr) as acc_attr_name,
  acc.deposits_mode,
  acc.subject_code
FROM
  acc_freeze_and_defreeze_apply afd
  JOIN account acc
    ON acc.`acc_id` = afd.`acc_id`
  JOIN organization org
    ON org.org_id = acc.org_id
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  JOIN all_bank_info bank
    ON bank.cnaps_code = acc.`bank_cnaps_code`
WHERE 1=1 and afd.delete_flag = 0
  #if(map != null)
    #for(x : map)
      #if(x.value && x.value != "")
        AND
        #if("query_key".equals(x.key))
          afd.`memo` like concat('%', #para(x.value), '%')
        #elseif("service_status".equals(x.key))
          afd.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("start_date".equals(x.key))
          DATE_FORMAT(afd.create_on,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #elseif("end_date".equals(x.key))
          DATE_FORMAT(afd.create_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #elseif("status".equals(x.key))
          acc.status in(
            #for(y : map.status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("create_by".equals(x.key))
          afd.`create_by` = #para(x.value)
        #elseif("id".equals(x.key) || "type".equals(x.key))
          afd.#(x.key) = #para(x.value)
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findFreezeAndDeFreezePendingList")
SELECT
	afd.id as afd_id,
	afd.acc_id,
	afd.memo,
	afd.`type`,
	afd.apply_on,
	afd.create_on,
	afd.create_by,
	afd.update_on,
	afd.update_by,
	afd.service_serial_number,
	afd.service_status,
	afd.delete_flag,
	afd.persist_version,
	afd.attachment_count,
	afd.feedback,
	acc.acc_no,
	acc.acc_name,
	curr.name as curr_name,
	curr.iso_code,
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
	acc_freeze_and_defreeze_apply afd,cfm_workflow_run_execute_inst cwrei,account acc,currency curr
WHERE afd.id = cwrei.bill_id and acc.acc_id = afd.acc_id and curr.id = acc.curr_id
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
    #elseif("type".equals(x.key))
      AND #(x.key) = #(x.value)
    #elseif("biz_type".equals(x.key))
      AND #(x.key) = #(x.value)
    #end
  #end
#end