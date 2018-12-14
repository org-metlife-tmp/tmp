#sql("findOpenCompleteToPage")
select
	apply.id,
	apply.relation_id,
	apply.memo as up_memo,
	apply.finally_memo,
	apply.detail,
	apply.org_id,
	apply.org_name,
	CONVERT(DATE,apply.apply_on,110) as apply_on,
	apply.create_on,
	apply.create_by,
	apply.update_on,
	apply.update_by,
	apply.service_status,
	apply.attachment_count,
	apply.up_attachment_count,
	apply.persist_version
from
	(
		select
			oca.id as id,
			oia.id as relation_id,
			oia.memo as memo,
			oia.org_id,
			org.name as org_name,
			oia.apply_on,
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
			oca.persist_version as persist_version
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
			null as persist_version
		from
			acc_open_intention_apply oia,
			acc_open_intention_apply_issue oiai,
			organization org
		where
			oia.service_status = 4
			and oia.id = oiai.bill_id
			and oiai.user_id = #para(map.user_id)
			and org.org_id = oia.org_id
	) apply
where 1=1
    #if(map != null)
        #for(x : map)
            #if(x.value&&x.value!="")
              #if("user_id".equals(x.key))
                #continue
              #end
              AND
              #if("query_key".equals(x.key))
                apply.memo like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
                DATEDIFF(day,#para(x.value),apply.apply_on) >= 0
              #elseif("end_date".equals(x.key))
                DATEDIFF(day,#para(x.value),apply.apply_on) <= 0
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
    aoc.acc_purpose,
    aoc.memo,
    CONVERT(DATE,aoc.apply_on,110) as apply_on,
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
          aoc.memo like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
          DATEDIFF(day,#para(x.value),aoc.apply_on) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),aoc.apply_on) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("chgOpenCompleteByIdAndVersion")
  update
    acc_open_complete_apply
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
	(select [value] from category_value where cat_code = 'acc_attr' and [key] = aoc.acc_attr) as acc_attr,
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

