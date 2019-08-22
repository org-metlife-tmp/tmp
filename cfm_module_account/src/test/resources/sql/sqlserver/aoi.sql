#sql("findOpenIntentionToPage")
select
	id,
	memo,
	detail,
	org_id,
	dept_id,
	CONVERT(DATE,apply_on,110) as apply_on,
	CONVERT(DATE,create_on,110) as create_on,
	create_by,
	CONVERT(DATE,update_on,110) as update_on,
	update_by,
	service_serial_number,
	service_status,
	delete_flag,
	persist_version,
	attachment_count,
	finally_memo,
	feedback,
	(select [name] from user_info where usr_id = create_by ) as user_name,
	(select [name] from organization o where o.org_id = aoi.org_id) as org_name,
	(select [name] from department d where d.dept_id = aoi.dept_id) as dept_name,
	(SELECT GROUP_CONCAT(usr.name) AS [name] FROM acc_open_intention_apply_issue issue
	JOIN user_info usr ON usr.usr_id = issue.user_id AND usr.is_admin = 0 AND usr.status = 1 WHERE issue.bill_id = aoi.id) as issue
from
	acc_open_intention_apply aoi
where 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            memo like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
            DATEDIFF(day,#para(x.value),apply_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),apply_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("getOpenIntentionById")
  select
    id,
    memo,
    detail,
    org_id,
    dept_id,
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
    finally_memo,
    feedback
  from
    acc_open_intention_apply
  where id = ?
  and delete_flag = ?
#end

#sql("chgOpenIntentionByIdAndVersion")
  update
    acc_open_intention_apply
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

#sql("findIssueList")
select
	bill_id,
	user_id,
	counts
from
	acc_open_intention_apply_issue
where bill_id = ?
#end

#sql("findIssueName")
SELECT
	STUFF(
		(
			SELECT
				',' + CONVERT (VARCHAR, usr.name)
			FROM
				acc_open_intention_apply_issue issue
			JOIN user_info usr ON usr.usr_id = issue.user_id
			AND usr.is_admin = 0
			AND usr.status = 1
			WHERE
				issue.bill_id = ? FOR xml path ('')
		),
		1,
		1,
		''
	) AS name
#end

#sql("findOpenIntentionPendingList")
SELECT
	aoi.id as aoi_id,
	aoi.memo,
	aoi.detail,
	aoi.org_id,
	aoi.dept_id,
	aoi.apply_on,
	aoi.create_on,
	aoi.create_by,
	aoi.update_on,
	aoi.update_by,
	aoi.service_serial_number,
	aoi.service_status,
	aoi.delete_flag,
	aoi.persist_version,
	aoi.attachment_count,
	aoi.finally_memo,
	aoi.feedback,
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
	acc_open_intention_apply aoi,cfm_workflow_run_execute_inst cwrei
WHERE aoi.id = cwrei.bill_id
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