###销户事项
#sql("getPage")
SELECT
  apply.id,
  apply.memo,
  apply.detail,
  apply.org_id,
  apply.dept_id,
  DATE_FORMAT(
    apply.apply_on,
    '%Y-%m-%d'
    ###"%Y-%m-%d %H:%i:%s"
  ) AS apply_on,
  DATE_FORMAT(apply.create_on,'%Y-%m-%d') as create_on,
  DATE_FORMAT(apply.update_on,'%Y-%m-%d') as update_on,
  apply.create_by,
  apply.update_by,
  apply.service_serial_number,
  apply.service_status,
  apply.delete_flag,
  apply.persist_version,
  apply.attachment_count,
  apply.finally_memo,
  apply.feedback,
  usr.`name` AS user_name,
  org.name AS org_name,
  dept.`name` AS dept_name,
    (SELECT
    GROUP_CONCAT(usr.name) AS `name`
  FROM
    `acc_close_intention_apply_issue` issue
    JOIN user_info usr
      ON usr.usr_id = issue.`user_id`
      AND usr.`is_admin` = 0
      AND usr.`status` = 1
  WHERE issue.bill_id = apply.id) as issues
FROM
  acc_close_intertion_apply apply
  JOIN user_info usr
    ON usr.usr_id = apply.create_by
  JOIN user_org_dept_pos uodp
    ON uodp.usr_id = usr.usr_id
    AND uodp.`is_default` = 1
  JOIN organization org
    ON org.`org_id` = uodp.org_id
  JOIN department dept
    ON dept.`dept_id` = uodp.`dept_id`
WHERE 1 = 1
  AND delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            apply.`memo` like concat('%', #para(x.value), '%')
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
            DATE_FORMAT(apply.apply_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(apply.apply_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            apply.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("update")
  update acc_close_intertion_apply
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
    #for(x : map.where)
      #if(x.value&&x.value!="")
        #if(for.index > 0)
          and
        #end
        #(x.key) = #para(x.value)
      #end
    #end
#end

#sql("usernames")
SELECT
  GROUP_CONCAT(usr.name) AS `name`
FROM
  `acc_close_intention_apply_issue` issue
  JOIN user_info usr
    ON usr.usr_id = issue.`user_id`
    AND usr.`is_admin` = 0
    AND usr.`status` = 1
WHERE bill_id = ?
#end

#sql("findCloseIntentionPendingList")
SELECT
	cai.id as cai_id,
	cai.memo,
	cai.detail,
	cai.org_id,
	cai.dept_id,
	cai.apply_on,
	cai.create_on,
	cai.create_by,
	cai.update_on,
	cai.update_by,
	cai.service_serial_number,
	cai.service_status,
	cai.delete_flag,
	cai.persist_version,
	cai.attachment_count,
	cai.finally_memo,
	cai.feedback,
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
	acc_close_intertion_apply cai,cfm_workflow_run_execute_inst cwrei
WHERE cai.id = cwrei.bill_id
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
