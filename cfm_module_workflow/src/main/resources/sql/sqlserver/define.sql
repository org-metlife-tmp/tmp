###流程定义
#sql("getPage")
SELECT
  base.id,
  base.workflow_name,
  base.workflow_type,
  base.laster_version,
  base.is_activity,
  CONVERT(VARCHAR(10), base.create_on, 120) as create_on,
  base.create_by,
  CONVERT(VARCHAR(10), base.update_on, 120) as update_on,
  base.update_by,
  base.persist_version,
  base.delete_flag,
  usr.name as user_name
FROM
  cfm_workflow_base_info base
JOIN user_info usr
  ON usr.usr_id = base.create_by
WHERE delete_flag = 0
  AND workflow_type = 2
  #if(map != null)
    #for(x : map)
      #if(x.value && x.value != "")
        AND
        #if("query_key".equals(x.key))
          workflow_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("nodes")
SELECT
  id,
  def_id,
  node_name as item_id,
  lane_code,
  node_exp as data,
  axis_x,
  axis_y,
  n_row,
  n_column
FROM
  cfm_workflow_def_node
WHERE def_id = ?
#end

#sql("lines")
select
	line.id ,
	line.def_id,
	convert(varchar,case
		when line.start_node_id < 0 then line.start_node_id
		else start_node.node_name
	end) as d_source_id,
	convert(varchar,case
		when line.end_node_id < 0 then line.end_node_id
		else end_node.node_name
	end) as d_target_id,
	drive_condition as [rule]
from
	cfm_workflow_def_line line
left join cfm_workflow_def_node start_node on
	line.start_node_id = start_node.id
left join cfm_workflow_def_node end_node on
	line.end_node_id = end_node.id
where
	line.def_id = ?
#end

#sql("users")
  SELECT
  usr_id AS id,
  name
FROM
  user_info
WHERE 1 = 1 AND STATUS = 1
  #if(list != null)
    and usr_id in(
      #for(y : list)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
    )
  #end
#end

#sql("poss")
  SELECT
  pos_id AS id,
  name
FROM
  position_info
WHERE 1 = 1 AND STATUS != 3
  #if(list != null)
    and pos_id in(
      #for(y : list)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
    )
  #end
#end

#sql("checkWorkFlowNameisExist")
  select *
  from cfm_workflow_base_info
  where workflow_name = ?
  and workflow_name NOT IN(?);
#end

#sql("findNewDefineVersion")
SELECT
	id,
	base_id,
	def_version,
	reject_strategy,
	lanes,
	create_on,
	create_by
FROM
	cfm_workflow_define
WHERE base_id = ?
ORDER BY def_version DESC
#end