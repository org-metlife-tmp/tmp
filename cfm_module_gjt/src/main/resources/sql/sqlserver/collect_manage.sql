#sql("list")
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
    t.delete_flag = 0
  AND t.service_status = 4
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
            #elseif("is_activity".equals(x.key))
              t.is_activity = #para(x.value)
            #else
              t.#(x.key) = #para(x.value)
            #end
        #end
    #end
  #end
#end

#sql("findQuartz")
  select * from cfm_quartz where groups = ?
#end

#sql("updInstrById")
  update collect_execute_instruction set bank_serial_number = ? , collect_status = ? ,repeat_count = ?
  where id = ? and  repeat_count = ? and collect_status = ?
#end

#sql("findCollectExecuteByCollectId")
SELECT
TOP 3
	id,
	collect_id,
	execute_time,
	collect_amount,
	success_count,
	fail_count,
	collect_status,
	child_account_count,
	main_account_count
FROM
	collect_execute
WHERE
	collect_id = ?
ORDER BY
	execute_time desc
#end

#sql("findExecuteInstructionListByExecuteId")
SELECT
	id,
	collect_id,
	collect_execute_id,
	bank_serial_number,
	repeat_count,
	pay_account_id,
	pay_account_org_id,
	pay_account_org_name,
	pay_account_no,
	pay_account_name,
	pay_account_cur,
	pay_account_bank,
	pay_bank_cnaps,
	pay_bank_prov,
	pay_bank_city,
	recv_account_id,
	recv_account_org_id,
	recv_account_org_name,
	recv_account_no,
	recv_account_name,
	recv_account_cur,
	recv_account_bank,
	recv_bank_cnaps,
	recv_bank_prov,
	recv_bank_city,
	collect_amount,
	collect_status,
	create_on,
	update_on,
	percentage,
	persist_version,
	is_checked,
	instruct_code,
	statement_code,
	feed_back
FROM
	collect_execute_instruction ins
WHERE
  collect_execute_id = ?
AND create_on = ?
ORDER BY create_on DESC
#end