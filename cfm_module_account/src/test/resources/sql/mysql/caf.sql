###销户事项补录
#sql("getTodoPage")
  SELECT
    apply.id,
    apply.relation_id,
    apply.aci_memo,
    apply.memo,
    DATE_FORMAT(apply.apply_on,'%Y-%m-%d') as apply_on,
    DATE_FORMAT(apply.create_on,'%Y-%m-%d') as create_on,
    DATE_FORMAT(apply.update_on,'%Y-%m-%d') as update_on,
    apply.create_by,
    apply.update_by,
    apply.service_serial_number,
    apply.service_status,
    apply.delete_flag,
    apply.persist_version,
    apply.attachment_count,
    apply.feedback,
    apply.close_date,
    apply.acc_id
  FROM
  (SELECT
    NULL AS id,
    acia.id AS relation_id,
    acia.memo as aci_memo,
    acia.memo,
    acia.apply_on,
    acia.create_on,
    acia.create_by,
    acia.update_on,
    acia.update_by,
    acia.service_serial_number,
    12 AS service_status,
    acia.delete_flag,
    acia.persist_version,
    acia.attachment_count,
    acia.feedback,
    null as close_date,
    null as acc_id
  FROM
    `acc_close_intertion_apply` acia
  WHERE acia.service_status = 4
    AND acia.delete_flag = 0
    AND (
      (acia.create_by = #para(map.create_by) and acia.org_id = #para(map.org_id))
      OR EXISTS
      (SELECT
        1
      FROM
        `acc_close_intention_apply_issue` aciai
      WHERE aciai.`user_id` = #para(map.user_id)
        AND aciai.bill_id = acia.id)
    )
    UNION
    ALL
    SELECT
      acca.id,
      acca.relation_id,
      acia.memo as aci_memo,
      acca.memo,
      acca.apply_on,
      acca.create_on,
      acca.create_by,
      acca.update_on,
      acca.update_by,
      acca.service_serial_number,
      acca.service_status,
      acca.delete_flag,
      acca.persist_version,
      acca.attachment_count,
      acca.feedback,
      acca.close_date,
      acca.acc_id
    FROM
      `acc_close_complete_apply` acca
      left join `acc_close_intertion_apply` acia
      ON acia.id = acca.relation_id
    WHERE acca.`delete_flag` = 0
      AND acca.`service_status` IN (1, 5)
      AND acca.`create_by` = #para(map.create_by)
      ) apply
    WHERE 1=1
    #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("create_by".equals(x.key) || "org_id".equals(x.key) || "user_id".equals(x.key))
              #continue
            #end
            AND
            #if("query_key".equals(x.key))
              apply.memo like concat('%', #para(x.value), '%')
            #elseif("service_status".equals(x.key))
              apply.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
    #end
#end

#sql("getDonePage")
  SELECT
    acca.id,
    acca.relation_id,
    acia.memo as aci_memo,
    acca.memo,
    DATE_FORMAT(acca.apply_on,'%Y-%m-%d') as apply_on,
    DATE_FORMAT(acca.create_on,'%Y-%m-%d') as create_on,
    DATE_FORMAT(acca.update_on,'%Y-%m-%d') as update_on,
    acca.create_by,
    acca.update_by,
    acca.service_serial_number,
    acca.service_status,
    acca.delete_flag,
    acca.persist_version,
    acca.attachment_count,
    acca.feedback,
    acca.close_date,
    acca.acc_id
  FROM acc_close_complete_apply acca
  left join acc_close_intertion_apply acia
  ON acia.id = acca.relation_id
  WHERE acca.delete_flag = 0
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
          #if("query_key".equals(x.key))
            acca.memo like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            acca.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("start_date".equals(x.key))
            DATE_FORMAT(acca.apply_on,'%Y-%m-%d') >= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(acca.apply_on,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            acca.#(x.key) = #para(x.value)
          #end
      #end
    #end
  #end
#end

#sql("fingCloseCompleteById")
  select
	id,
	relation_id,
	acc_id,
	close_date,
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
    acc_close_complete_apply
  where 1=1
    #for(x:cond)
      #if(x.value&&x.value!="")
        and
          #(x.key) = '#(x.value)'
      #end
    #end
#end

#sql("getAdditional")
SELECT
  apply_id,
  comments,
  amount
FROM
  acc_close_complete_apply_additional
WHERE apply_id = ?
#end

#sql("findCloseCompletePendingList")
SELECT
	chg.id as aca_id,
	chg.acc_id,
	chg.memo,
	chg.apply_on,
	chg.create_on,
	chg.create_by,
	chg.update_on,
	chg.update_by,
	chg.service_serial_number,
	chg.service_status,
	chg.delete_flag,
	chg.persist_version,
	chg.attachment_count,
	chg.feedback,
	acc.acc_no,
	acc.acc_name,
	curr.name as curr_name,
	acc.interactive_mode,
	(select `value` from category_value where cat_code = 'acc_attr' and `key` = acc.acc_attr) as acc_attr,
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
	acc_change_apply chg,cfm_workflow_run_execute_inst cwrei,account acc,currency curr
WHERE chg.id = cwrei.bill_id and acc.acc_id = chg.acc_id and curr.id = acc.curr_id
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