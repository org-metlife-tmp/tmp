#sql("findHeadPayById")
  select * from nc_head_payment where id = ? and delete_flag = 0 and persist_version=?
#end

#sql("getTodoPage")
  SELECT
    head.id,
    head.org_id,
    head.dept_id,
    head.pay_account_id,
    head.pay_account_no,
    head.pay_account_name,
    head.pay_account_cur,
    head.pay_account_bank,
    head.pay_bank_cnaps,
    head.pay_bank_prov,
    head.pay_bank_city,
    head.recv_account_id,
    head.recv_account_no,
    head.recv_account_name,
    head.recv_account_cur,
    head.recv_account_bank,
    head.recv_bank_cnaps,
    head.recv_bank_prov,
    head.recv_bank_city,
    head.payment_amount,
    head.pay_mode,
    head.payment_summary,
    head.service_status,
    head.service_serial_number,
    head.bank_serial_number,
    head.repeat_count,
    head.delete_flag,
    head.process_bank_type,
    head.persist_version,
    head.attachment_count,
    head.feed_back,
    head.is_checked,
    head.instruct_code,
    head.statement_code,
    head.ref_id,
    head.create_by,
    head.update_by,
    CONVERT(varchar(100), head.create_on, 23) as create_on,
    CONVERT(varchar(100), head.update_on, 23) as update_on,
    od.flow_id,
    od.apply_user,
    org.name as org_name,
    left(head.recv_bank_cnaps,3) as recv_bank_type
  FROM
    nc_head_payment head
  JOIN (
    SELECT
      org2.org_id,
      org2.code,
      org2.name,
      org2.address
    FROM
      organization org
    JOIN organization org2 ON charindex(
      org.level_code,
      org2.level_code
    ) = 1
    WHERE
      org.org_id = #(map.org_id)
  ) org ON org.org_id = head.org_id
  JOIN nc_origin_data od on od.id = head.ref_id
  WHERE
    head.delete_flag = 0
    #if(map != null)
      #for(x : map)

          #if("org_id".equals(x.key))
            #continue
          #end

          #if(x.value&&x.value!="")
            AND
            #if("recv_account_query_key".equals(x.key))
              (
              head.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              or
              head.recv_account_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("service_status".equals(x.key))
              head.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("min".equals(x.key))
              payment_amount >= #para(x.value)
            #elseif("max".equals(x.key))
              payment_amount <= #para(x.value)
            #elseif("apply_start_date".equals(x.key))
               DATEDIFF(day,#para(x.value),apply_date) >= 0
            #elseif("apply_end_date".equals(x.key))
                DATEDIFF(day,#para(x.value),apply_date) <= 0
            #elseif("send_start_date".equals(x.key))
               DATEDIFF(day,#para(x.value),update_on) >= 0
            #elseif("send_end_date".equals(x.key))
                DATEDIFF(day,#para(x.value),update_on) <= 0
            #elseif("org_name".equals(x.key))
                org.name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
    #end
    order by head.id desc
#end

#sql("findHeadOrgNCPendingList")
  SELECT
	ohp.id as ohp_id,
	ohp.pay_account_id,
	ohp.pay_account_no,
	ohp.pay_account_name,
	ohp.pay_account_cur,
	ohp.pay_account_bank,
	ohp.pay_bank_cnaps,
	ohp.pay_bank_prov,
	ohp.pay_bank_city,
	ohp.recv_account_id,
	ohp.recv_account_no,
	ohp.recv_account_name,
	ohp.recv_account_cur,
	ohp.recv_account_bank,
	ohp.recv_bank_cnaps,
	ohp.recv_bank_prov,
	ohp.recv_bank_city,
	ohp.payment_amount,
	ohp.pay_mode,
	ohp.payment_summary,
	ohp.service_status,
	ohp.service_serial_number,
	ohp.bank_serial_number,
	ohp.repeat_count,
  ohp.persist_version,
	ohp.ref_id,
  org.name as org_name,
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
	nc_head_payment ohp,cfm_workflow_run_execute_inst cwrei,nc_origin_data od,organization org
WHERE ohp.id = cwrei.bill_id
AND od.id = ohp.ref_id
  AND org.org_id = ohp.org_id
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
  order by ohp_id
#end

#sql("updBillById")
   update nc_head_payment set bank_serial_number = ?,repeat_count = ?,service_status = ?,instruct_code = ?,update_on = ?, persist_version=?
   where id = ? and repeat_count = ? and persist_version=?
#end