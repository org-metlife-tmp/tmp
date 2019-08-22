#sql("findChangeToList")
select
	aca.id,
	aca.acc_id,
	aca.memo,
	DATE_FORMAT(aca.apply_on,'%Y-%m-%d') as apply_on,
	aca.create_on,
	aca.create_by,
	aca.update_on,
	aca.update_by,
	aca.service_serial_number,
	aca.service_status,
	aca.delete_flag,
	aca.persist_version,
	aca.attachment_count,
	aca.feedback,
	acc.acc_no,
	acc.acc_name
from
	acc_change_apply aca,
	account acc
where aca.acc_id = acc.acc_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            aca.memo like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            service_status in(
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

#sql("fingchgById")
select
	id,
	acc_id,
	memo,
	DATE_FORMAT(apply_on,'%Y-%m-%d') as apply_on,
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
	acc_change_apply
where service_status = 4
  #for(x:cond)
    #if(x.value&&x.value!="")
      and
        #(x.key) = '#(x.value)'
    #end
  #end
#end

#sql("chgChangeByIdAndVersion")
  update
    acc_change_apply
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

#sql("delChangeDetailByApplyId")
  DELETE
  FROM acc_change_apply_detail
  WHERE apply_id = ?
#end

#sql("findChangeDetailByApplyId")
select
	id,
	apply_id,
	`type`,
	old_id,
	old_value,
	new_id,
	new_value
from
	acc_change_apply_detail
WHERE apply_id = ?
#end

#sql("findAccChangePendingList")
SELECT
	aca.id as aca_id,
	aca.acc_id,
	aca.memo,
	acc.interactive_mode,
	acc.acc_name,
	acc.acc_no,
	curr.name as curr_name,
	(select value from category_value where cat_code = 'acc_attr' and `key` = acc.acc_attr) as acc_attr,
	aca.apply_on,
	aca.create_on,
	aca.create_by,
	aca.update_on,
	aca.update_by,
	aca.service_serial_number,
	aca.service_status,
	aca.delete_flag,
	aca.persist_version,
	aca.attachment_count,
	aca.feedback,
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
	acc_change_apply aca,cfm_workflow_run_execute_inst cwrei,account acc,currency curr
WHERE aca.id = cwrei.bill_id and acc.acc_id = aca.acc_id and acc.curr_id = curr.id
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