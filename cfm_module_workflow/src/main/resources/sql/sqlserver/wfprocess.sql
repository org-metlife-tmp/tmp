#sql("findAuthorizedRelation")
SELECT
	authorize_usr_id,
	authorize_usr_name,
	be_authorize_usr_id,
	be_authorize_usr_name,
	start_date,
	end_date,
	import_time
FROM
	cfm_workflow_authorize_relation;
#end


#sql("findAllWfRunInst")
select
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
from
	cfm_workflow_run_execute_inst
#end


#sql("findWfSpecifiedUser")
SELECT
  usr.usr_id,
  usr.email,
  usr.login_name,
  usr.name,
  usr.phone,
  usr.status,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name
FROM user_info usr
  JOIN user_org_dept_pos uodp
    ON usr.usr_id = uodp.usr_id
       AND uodp.is_default = 1
  JOIN organization org
    ON org.org_id = uodp.org_id
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
where usr.usr_id = ?
#end


#sql("findUsersInOrg")
SELECT
  usr.usr_id,
  usr.email,
  usr.login_name,
  usr.name,
  usr.phone,
  usr.status,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name
FROM user_info usr
  JOIN user_org_dept_pos uodp
    ON usr.usr_id = uodp.usr_id
  JOIN organization org
    ON org.org_id = uodp.org_id
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
where uodp.org_id = ?
#end

#sql("findUsersWithOrgPos")
SELECT
  usr.usr_id,
  usr.email,
  usr.login_name,
  usr.name,
  usr.phone,
  usr.status,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name
FROM user_info usr
  JOIN user_org_dept_pos uodp
    ON usr.usr_id = uodp.usr_id
  JOIN organization org
    ON org.org_id = uodp.org_id
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
where uodp.org_id = ?
and uodp.pos_id = ?
#end





#sql("findWFDefineByDefineId")
select
	define.base_id ,
	base.workflow_name ,
	base.workflow_type ,
	define.id as define_id ,
	define.reject_strategy ,
	define.def_version ,
	base.persist_version
from
	cfm_workflow_base_info base ,
	cfm_workflow_define define
where
	base.id = define.base_id
	and base.laster_version = define.def_version
	and base.delete_flag = 0
	and is_activity = 1
	and define.id = ?
#end


#sql("findFirstNodes")
select
	node.id ,
	node.def_id ,
	node.node_name ,
	node.lane_code ,
	node.node_exp ,
	node.axis_x ,
	node.axis_y ,
	node.n_row ,
	node.n_column,
	line.drive_condition
from
	cfm_workflow_def_node node ,
	cfm_workflow_def_line line
where
	node.id = line.end_node_id
	and line.start_node_id = -1
	and node.def_id  = ?
	order by drive_condition desc
#end


#sql("findCustomerFirstNodes")
select
	id,
	def_id,
	user_id,
	user_name,
	step_number
from
	cfm_workflow_def_custom_node
where
	step_number = 1
	and def_id =?
#end


#sql("findwfnodeById")
select
 id as workflow_node_id,
 def_id as define_id,
 node_name,
 lane_code,
 node_exp,
 axis_x,
 axis_y,
 n_row,
 n_column
from
 cfm_workflow_def_node
where id = ?
#end


#sql("findSubOrg")
select
	org_id
from
	organization
where
	level_code like ?
	and level_num = ?
#end


#sql("findPossibleNextNodes")
select
	node.id ,
	node.def_id ,
	node.node_name ,
	node.lane_code ,
	node.node_exp ,
	node.axis_x ,
	node.axis_y ,
	node.n_row ,
	node.n_column,
	line.drive_condition
from
	cfm_workflow_def_node node ,
	cfm_workflow_def_line line
where
	node.id = line.end_node_id
	and line.start_node_id = ?
	and node.def_id  = ?
#end

#sql("findPossibleLinesWithStart")
select
	id,
	def_id,
	start_node_id,
	end_node_id,
	drive_condition
from
	cfm_workflow_def_line
where
	def_id = ?
	and start_node_id = ?
#end

#sql("findPossibleLinesWithEnd")
select
	id,
	def_id,
	start_node_id,
	end_node_id,
	drive_condition
from
	cfm_workflow_def_line
where
	def_id = ?
	and end_node_id = ?
#end


#sql("findPreHisInstNode")
select
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
	end_time
from
	cfm_workflow_his_execute_inst
where
	shadow_execute = ?
	and assignee_type = ?
	and workflow_node_id != ?
	and base_id = ?
	and define_id = ?
	and biz_type = ?
	and bill_id = ?
	and step_number = ?
	order by id desc
#end

#sql("findwfrunexecuteinstlist")
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
#for(x : map)
    #if("in".equals(x.key))
      #if(map.in != null)
        AND id IN (
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
        AND id NOT IN (
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
ORDER BY biz_type ASC, id desc
#end

#sql("findwfhisexecuteinstlist")
SELECT
	inst.*
FROM
	cfm_workflow_his_execute_inst inst
  JOIN (
    SELECT
      biz_type,
      bill_id,
      bill_code,
      MAX (ru_execute_inst_id) AS ru_execute_inst_id
    FROM
      cfm_workflow_his_execute_inst
    WHERE
      1 = 1
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
    GROUP BY
      biz_type,
      bill_id,
      bill_code
  ) da ON da.ru_execute_inst_id = inst.ru_execute_inst_id
  AND da.bill_code = inst.bill_code
  AND da.bill_id = inst.bill_id
  AND da.biz_type = inst.biz_type
#end