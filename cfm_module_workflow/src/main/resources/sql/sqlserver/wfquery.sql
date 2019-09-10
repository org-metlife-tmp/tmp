#sql("findrunexecuteinstList")
SELECT
	id,
	base_id,
	workflow_name,
	define_id,
	workflow_type,
	reject_strategy,
	def_version,
	workflow_node_id,
	step_number,
	shadow_execute,
	shadow_user_id,
	shadow_user_name,
	biz_type,
	bill_id,
	bill_code,
	submitter,
	submitter_name,
	submitter_pos_id,
	submitter_pos_name,
	init_user_id,
	init_user_name,
	init_org_id,
	init_org_name,
	init_dept_id,
	init_dept_name,
	start_time
FROM
	cfm_workflow_run_execute_inst
WHERE 1=1
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("bill_code".equals(x.key))
             #(x.key) LIKE #para(x.value)
          #elseif("start_time".equals(x.key))
             DATEDIFF(day,#para(x.value),CONVERT(DATE,start_time,110)) >= 0
          #elseif("end_time".equals(x.key))
             DATEDIFF(day,#para(x.value),CONVERT(DATE,end_time,110)) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
ORDER BY id asc
#end

#sql("findhisexecuteinstList")
SELECT
	id,
	ru_execute_inst_id,
	base_id,
	workflow_name,
	define_id,
	workflow_type,
	reject_strategy,
	def_version,
	workflow_node_id,
	step_number,
	shadow_execute,
	shadow_user_id,
	shadow_user_name,
	shadow_step_number,
	assignee_id,
	assignee,
	assignee_type,
	assignee_memo,
	biz_type,
	bill_id,
	bill_code,
	submitter,
	submitter_name,
	submitter_pos_id,
	submitter_pos_name,
	init_user_id,
	init_user_name,
	init_org_id,
	init_org_name,
	init_dept_id,
	init_dept_name,
	start_time,
	end_time,
	assignee_id as usr_id
FROM
	cfm_workflow_his_execute_inst
WHERE 1=1
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
            #(x.key) = #para(x.value)
        #end
    #end
  #end
ORDER BY id asc
#end

#sql("findPendingTaskAllNum")
select
	biz_type,
	count( 1 ) as num
FROM
	cfm_workflow_run_execute_inst rust
where 1=1
	#for(x : map)
    #if("in".equals(x.key))
      #if(map.in != null)
        AND rust.id IN (
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
        AND rust.id NOT IN (
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
    #end
  #end
group by
	rust.biz_type
order by
	rust.biz_type
#end


#sql("findProcessTaskAllNum")
select
  biz_type,
  count(1) as num
FROM
  (
  select
	  biz_type,
	  bill_id
  FROM
	  cfm_workflow_his_execute_inst rust
  where 1=1
	  #for(x : map)
      #if("usr_id".equals(x.key))
       and rust.assignee_id = #para(x.value)
      #end
    #end
  group by
	  rust.biz_type,rust.bill_id
  ) inner_query
group by
	inner_query.biz_type
order by
  inner_query.biz_type
#end


#sql("findProcessedWfInst")
SELECT
	inst.id,
	inst.ru_execute_inst_id,
	inst.base_id,
	inst.workflow_name,
	inst.define_id,
	inst.workflow_type,
	inst.reject_strategy,
	inst.def_version,
	inst.workflow_node_id,
	inst.step_number,
	inst.shadow_execute,
	inst.shadow_user_id,
	inst.shadow_user_name,
	inst.shadow_step_number,
	inst.assignee_id,
	inst.assignee,
	inst.assignee_type,
	inst.assignee_memo,
	inst.biz_type,
	inst.bill_id,
	inst.bill_code,
	inst.submitter,
	inst.submitter_name,
	inst.submitter_pos_id,
	inst.submitter_pos_name,
	inst.init_user_id,
	inst.init_user_name,
	inst.init_org_id,
	inst.init_org_name,
	inst.init_dept_id,
	inst.init_dept_name,
	inst.start_time,
	inst.end_time
	#if(extendS)
	  ,#(extendS)
	#end
FROM
	cfm_workflow_his_execute_inst inst
  JOIN (
    SELECT
      MAX(ru_execute_inst_id) AS ru_execute_inst_id
    FROM
      cfm_workflow_his_execute_inst
    WHERE
      assignee_id = #(assignee_id)
      and
      biz_type = #(biz_type)
    GROUP BY
      bill_id,
      bill_code
  ) da ON da.ru_execute_inst_id = inst.ru_execute_inst_id
  #if(extendJ)
    #(extendJ)
  #end
WHERE
  1=1
  #if(map != null)
    #for(x : map)
      #if(x.value && x.value != "")
        AND
        #if("init_user_name".equals(x.key) || "init_org_name".equals(x.key) || "init_dept_name".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_time".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,start_time,110)) >= 0
        #elseif("end_time".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,end_time,110)) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
order by
  inst.id
#end