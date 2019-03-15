#sql("getChildAccList")
  SELECT
    acc.org_id as child_acc_org_id,
    org.org_name as child_acc_org_name,
    acc.acc_id as child_acc_id,
    acc.acc_no as child_acc_no,
    acc.acc_name as child_acc_name,
    bank.name as child_acc_bank_name,
    bank.cnaps_code as child_acc_bank_cnaps_code,
    curr.iso_code AS child_acc_cur,
	  bank.province AS child_acc_bank_prov,
	  bank.city AS child_acc_bank_city
  FROM
    account acc
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  LEFT JOIN category_value pur
  	ON acc.acc_purpose = pur.[key]
  JOIN (
    SELECT DISTINCT
      org2.org_id,
      org2.name as org_name
    FROM
      organization org
    JOIN organization org2 ON charindex(
      org.level_code,
      org2.level_code
    ) = 1
    WHERE
      org.org_id = #para(map.org_id)
  ) org ON acc.org_id = org.org_id
  JOIN all_bank_info bank ON bank.cnaps_code = acc.bank_cnaps_code
  WHERE
    acc.org_id != #para(map.org_id)
    and acc.is_activity = 1
    and acc.status = 1
    and acc.interactive_mode = 1
    #if(map!=null)
      #for(x : map)
        #if(x.value&&x.value!="")

          #if("org_id".equals(x.key))
            #continue
          #end

          #if("exclude_ids".equals(x.key) && map.exclude_ids.size()==0)
            #continue
          #end

          AND
          #if("query_key".equals(x.key))
            (acc.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              or
            acc.acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            )
          #elseif("bank_type".equals(x.key))
            bank.bank_type = #para(x.value)
          #elseif("acc_type".equals(x.key))
            pur.[key] = #para(x.value)
          #elseif("exclude_ids".equals(x.key))
            acc.acc_id not in(
              #for(y : map.exclude_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            acc.#(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
#end

#sql("findGJTPendingList")
  select * from (
  SELECT
    DISTINCT
    t.id as t_id,
    t.topic,
    t.attachment_count,
    t.service_serial_number,
    t.collect_type,
    t.collect_amount,
    t.collect_frequency,
    t.collect_main_account_count,
    t.collect_child_account_count,
    t.service_status,
    t.is_activity,
    t.summary,
    t.persist_version,
    ct2.collect_time,
    cwrei.id AS inst_id,
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
    collect_topic t
  JOIN collect_main_account ma ON t.id = ma.collect_id
  JOIN (
    SELECT
      MAX (ct.id) AS ct_id,
      ct.collect_id
    FROM
      collect_timesetting ct
    GROUP BY
      ct.collect_id
  ) ct ON ct.collect_id = t.id
  JOIN collect_timesetting ct2 ON ct2.id = ct.ct_id
  JOIN cfm_workflow_run_execute_inst cwrei ON t.id = cwrei.bill_id
  WHERE
    t.delete_flag = 0
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
  ) res
  order by t_id
#end

#sql("morebill")
  select * from (
  SELECT
    distinct
    t.id,
    t.topic,
    t.attachment_count,
    t.service_serial_number,
    t.collect_type,
    t.collect_amount,
    t.collect_frequency,
    t.collect_main_account_count,
    t.collect_child_account_count,
    t.service_status,
    t.is_activity,
    t.summary,
    t.persist_version,
    ct2.collect_time
  FROM
    collect_topic t
  JOIN collect_main_account ma ON t.id = ma.collect_id
  JOIN (
    SELECT
      MAX (ct.id) AS ct_id,
      ct.collect_id
    FROM
      collect_timesetting ct
    GROUP BY
      ct.collect_id
  ) ct ON ct.collect_id = t.id
  JOIN collect_timesetting ct2 ON ct2.id = ct.ct_id
  WHERE
    1 = 1
  AND delete_flag = 0
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
            #elseif("main_acc_query_key".equals(x.key))
              (
                ma.main_acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
                ma.main_acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #else
              t.#(x.key) = #para(x.value)
            #end
        #end
    #end
  #end
  ) res
  order by service_status asc,id desc
#end

#sql("findTimeSettingList")
  select * from collect_timesetting where collect_id = ?
#end

#sql("findMainAccList")
  select * from collect_main_account where collect_id = ?
#end

#sql("findChildAccList")
  select * from collect_child_account where collect_id = ?
#end

#sql("listByST")
SELECT
  acc_id as main_acc_id,
  acc_no as main_acc_no,
  acc_name as main_acc_name,
  org.name AS main_acc_org_name,
  acc.bank_cnaps_code as main_acc_bank_cnaps_code,
  acc.org_id as main_acc_org_id,
  bank.name AS main_acc_bank_name,
  curr.iso_code AS main_acc_cur,
	bank.province AS main_acc_bank_prov,
	bank.city AS main_acc_bank_city
FROM
  account acc
  JOIN organization org
    ON org.org_id = acc.org_id
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  JOIN all_bank_info bank
    ON bank.cnaps_code = acc.bank_cnaps_code
    left join category_value cv
  	ON acc.acc_attr = cv.[key]
WHERE cv.cat_code='acc_attr'
  AND NOT EXISTS
  (SELECT
    1
  FROM
    acc_process_lock l
  WHERE l.acc_id = acc.acc_id
  #if(acc_id != null && !"".equals(acc_id))
    AND l.acc_id <> #para(acc_id)
  #end
  )
  AND acc.status = #para(status)
  AND acc.org_id = #para(org_id)
  AND acc.is_activity = 1
  #if(exclude_ids != null && exclude_ids.size() > 0)
    and acc.acc_id not in(
    #for(y : exclude_ids)
      #if(for.index > 0)
        #(",")
      #end
      #(y)
    #end
    )
  #end
#end

#sql("timeSettings")
  SELECT
        STUFF(
          (
            SELECT
              ',' + CONVERT (VARCHAR, ti.collect_time)
            FROM
              collect_timesetting ti
            WHERE
              ti.collect_id = ? FOR xml path ('')
          ),
          1,
          1,
          ''
        ) as time_settings
#end