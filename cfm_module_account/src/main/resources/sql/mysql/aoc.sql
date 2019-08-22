#sql("findOpenCompleteToPage")
select
	apply.id,
	apply.relation_id,
	apply.memo as up_memo,
	apply.finally_memo,
	apply.detail,
	apply.org_id,
	apply.org_name,
	DATE_FORMAT(apply.apply_on,'%Y-%m-%d') as apply_on,
	apply.bank_type,
  apply.area_code,
  apply.bank_cnaps_code,
  apply.lawfull_man,
  apply.acc_attr,
  apply.interactive_mode,
  apply.curr_id,
  apply.deposits_mode,
	apply.create_on,
	apply.create_by,
	apply.update_on,
	apply.update_by,
	apply.service_status,
	apply.attachment_count,
	apply.up_attachment_count,
	apply.persist_version,
	apply.acc_purpose,
	(select [value] from category_value where cat_code = 'acc_purpose' and [key] = apply.acc_purpose) as acc_purpose_name,
	apply.acc_attr,
	(select [value] from category_value where cat_code = 'acc_attr' and [key] = apply.acc_attr) as acc_attr_name,
	bnk.name  as bank_name,
	bnk.province,
	bnk.city,
	(select name from currency where id = apply.curr_id) as curr_name,
	apply.reserved_seal
from
	(
		select
			oca.id as id,
			oia.id as relation_id,
			oia.memo as memo,
			oia.org_id,
			org.name as org_name,
			oia.apply_on,
			oia.bank_type,
			oia.area_code,
			oia.bank_cnaps_code,
			oia.lawfull_man,
			oia.acc_attr,
			oia.interactive_mode,
			oia.curr_id,
			oia.deposits_mode,
			oia.create_on,
			oia.create_by,
			oia.update_on,
			oia.update_by,
			oca.service_status,
			oia.attachment_count as up_attachment_count,
			oca.attachment_count,
			oia.delete_flag,
			oia.detail as detail,
			oia.finally_memo,
			oca.persist_version as persist_version,
			oia.acc_purpose,
			oca.reserved_seal
		from
			acc_open_complete_apply oca,
			acc_open_intention_apply oia,
			organization org
		where
			oca.relation_id = oia.id
			and oca.service_status in(1,5,12)
			and oia.create_by = #para(map.user_id)
			and oca.delete_flag = 0
			and org.org_id = oia.org_id
	union all select
			null as id,
			oia.id as relation_id,
			oia.memo,
			oia.org_id,
			org.name  as org_name,
			oia.apply_on,
			oia.bank_type,
			oia.area_code,
			oia.bank_cnaps_code,
			oia.lawfull_man,
			oia.acc_attr,
			oia.interactive_mode,
			oia.curr_id,
			oia.deposits_mode,
			oia.create_on,
			oia.create_by,
			oia.update_on,
			oia.update_by,
			12 as service_status,
			oia.attachment_count as up_attachment_count,
			0 as attachment_count,
			oia.delete_flag,
			oia.detail,
			oia.finally_memo,
			null as persist_version,
			oia.acc_purpose,
			null as reserved_seal
		from
			acc_open_intention_apply oia,
			acc_open_intention_apply_issue oiai,
			organization org
		where
			oia.service_status = 4
			and oia.id = oiai.bill_id
			and oiai.user_id = #para(map.user_id)
			and org.org_id = oia.org_id
	) apply, all_bank_info bnk
where apply.bank_cnaps_code = bnk.cnaps_code
    #if(map != null)
        #for(x : map)
            #if(x.value&&x.value!="")
              #if("user_id".equals(x.key))
                #continue
              #end
              AND
              #if("query_key".equals(x.key))
                apply.memo like concat('%', #para(x.value), '%')
              #elseif("service_status".equals(x.key))
                apply.service_status in(
                  #for(y : map.service_status)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(y)
                  #end
                )
              #elseif("start_date".equals(x.key))
                DATE_FORMAT(apply_on,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
              #elseif("end_date".equals(x.key))
                DATE_FORMAT(apply_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
              #else
                #(x.key) = #para(x.value)
              #end
            #end
        #end
    #end
#end

#sql("findOpenCompleteDonePage")
  select
    aoc.id,
    aoc.relation_id,
    aoc.acc_id,
    aoc.acc_no,
    aoc.acc_name,
    aoc.org_id,
    org.name as org_name,
    aoc.curr_id,
    aoc.lawfull_man,
    aoc.bank_cnaps_code,
    aoc.bank_address,
    aoc.bank_contact,
    aoc.bank_contact_phone,
    aoc.open_date,
    aoc.interactive_mode,
    aoc.acc_attr,
    (select `value` from category_value where cat_code = 'acc_attr' and `key` = aoc.acc_attr) as acc_attr_name,
    aoc.bank_type,
    aoc.acc_purpose,
    (select `value` from category_value where cat_code = 'acc_purpose' and `key` = aoc.acc_purpose) as acc_purpose_name,
    aoc.area_code,
    aoc.deposits_mode,
    (select name from all_bank_info where cnaps_code = aoc.bank_cnaps_code) as bank_name,
	  (select name from currency where id = aoc.curr_id) as curr_name,
    aoc.reserved_seal,
    aoc.memo,
    DATE_FORMAT(aoc.apply_on,'%Y-%m-%d') as apply_on,
    aoc.create_on,
    aoc.create_by,
    aoc.update_on,
    aoc.update_by,
    aoc.service_serial_number,
    aoc.service_status,
    aoc.delete_flag,
    aoc.persist_version,
    aoc.attachment_count,
    aoc.feedback,
    aoi.memo as up_memo,
    aoi.detail,
    aoi.attachment_count as up_attachment_count
  from
    acc_open_complete_apply aoc,
    acc_open_intention_apply aoi,
    organization org
  where aoc.relation_id = aoi.id and org.org_id = aoc.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("query_key".equals(x.key))
          aoc.`memo` like concat('%', #para(x.value), '%')
        #elseif("service_status".equals(x.key))
          aoc.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("start_date".equals(x.key))
          DATE_FORMAT(aoc.apply_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #elseif("end_date".equals(x.key))
          DATE_FORMAT(aoc.apply_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("fingOpenCompleteById")
  select
    id,
    relation_id,
    acc_id,
    acc_no,
    acc_name,
    org_id,
    curr_id,
    lawfull_man,
    bank_cnaps_code,
    bank_address,
    bank_contact,
    bank_contact_phone,
    open_date,
    interactive_mode,
    acc_attr,
    acc_purpose,
    bank_type,
    acc_purpose,
    area_code,
    deposits_mode,
    reserved_seal,
    memo,
    apply_on,
    create_on,
    create_by,
    update_on,
    update_by,
    service_serial_number,
    service_status,
    delete_flag,
    persist_version,
    attachment_count,
    feedback
  from
    acc_open_complete_apply
  where 1=1
    #for(x:cond)
      #if(x.value&&x.value!="")
        and
          #(x.key) = '#(x.value)'
      #end
    #end
#end

#sql("findOpenCompletePendingList")
SELECT
	aoc.id as aoc_id,
	aoc.relation_id,
	aoc.acc_id,
	aoc.acc_no,
	aoc.acc_name,
	aoc.org_id,
	aoc.curr_id,
	aoc.lawfull_man,
	aoc.bank_cnaps_code,
	aoc.bank_address,
	aoc.bank_contact,
	aoc.bank_contact_phone,
	aoc.open_date,
	aoc.interactive_mode,
	aoc.acc_attr,
	aoc.acc_purpose,
	aoc.memo,
	aoc.apply_on,
	aoc.create_on,
	aoc.create_by,
	aoc.update_on,
	aoc.update_by,
	aoc.service_serial_number,
	aoc.service_status,
	aoc.delete_flag,
	aoc.persist_version,
	aoc.attachment_count,
	aoc.feedback,
	curr.name as curr_name,
	curr.iso_code,
	(select `value` from category_value where cat_code = 'acc_attr' and `key` = aoc.acc_attr) as acc_attr,
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
	acc_open_complete_apply aoc,cfm_workflow_run_execute_inst cwrei,currency curr
WHERE aoc.id = cwrei.bill_id and curr.id = aoc.curr_id
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
#end

