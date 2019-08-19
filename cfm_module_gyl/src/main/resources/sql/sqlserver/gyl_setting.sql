#sql("findGYLPendingList")
  SELECT
	gat.id as gat_id,
	gat.service_serial_number,
	gat.service_status,
  gat.create_on,
  gat.topic,
  gat.gyl_allocation_amount,
  gat.gyl_allocation_type,
  gat.gyl_allocation_frequency,
  gat.persist_version,
  ti.gyl_allocation_time,
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
	gyl_allocation_topic gat
	JOIN (
    SELECT
      MAX (id) AS m_id,
      gyl_allocation_id
    FROM
      gyl_allocation_timesetting ti
    GROUP BY
      ti.gyl_allocation_id
  ) tid ON tid.gyl_allocation_id = gat.id
  JOIN gyl_allocation_timesetting ti ON ti.id = tid.m_id
  join cfm_workflow_run_execute_inst cwrei on gat.id = cwrei.bill_id
WHERE gat.delete_flag = 0
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
  order by gat_id
#end

#sql("morebill")
  SELECT
    t.*, ti.gyl_allocation_time
  FROM
    gyl_allocation_topic t
  JOIN (
    SELECT
      MAX (id) AS m_id,
      gyl_allocation_id
    FROM
      gyl_allocation_timesetting ti
    GROUP BY
      ti.gyl_allocation_id
  ) tid ON tid.gyl_allocation_id = t.id
  JOIN gyl_allocation_timesetting ti ON ti.id = tid.m_id
  where t.delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
            #if("service_status".equals(x.key) && map.service_status.size()==0)
              #continue
            #end

            #if("is_activity".equals(x.key) && map.is_activity.size()==0)
              #continue
            #end
          AND

            #if("topic".equals(x.key))
              t.topic like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("service_status".equals(x.key))
              t.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("is_activity".equals(x.key))
              t.is_activity in(
                #for(y : map.is_activity)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("pay_acc_query_key".equals(x.key))
              (
                t.pay_acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
                t.pay_acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #else
              t.#(x.key) = #para(x.value)
            #end
        #end
    #end
  #end
  order by t.id desc
#end

#sql("findTimeSettingList")
  select * from gyl_allocation_timesetting where gyl_allocation_id = ?
#end

#sql("timeSettings")
  SELECT
        STUFF(
          (
            SELECT
              ',' + CONVERT (VARCHAR, ti.gyl_allocation_time)
            FROM
              gyl_allocation_timesetting ti
            WHERE
              ti.gyl_allocation_id = ? FOR xml path ('')
          ),
          1,
          1,
          ''
        ) as time_settings
#end