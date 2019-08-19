#sql("findMoreInfo")
SELECT
	skt.*,
  bank.bank_type,
  bank.province,
  bank.city
FROM
  outer_sk_receipts skt,all_bank_info bank
WHERE 1=1 and skt.pay_bank_cnaps = bank.cnaps_code
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
            receipts_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            receipts_amount <= #para(x.value)
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
order by service_status asc , skt.id desc
#end


#sql("findAllAmount")
SELECT
	SUM(receipts_amount) as total_amount,
	COUNT(0) as total_num
FROM
	outer_sk_receipts
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
            receipts_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            receipts_amount <= #para(x.value)
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

#sql("findPendingList")
SELECT
	ozp.id as ipd_id,
	ozp.pay_account_id,
	ozp.pay_account_no,
	ozp.pay_account_name,
	ozp.pay_account_bank,
	ozp.recv_account_id,
	ozp.recv_account_no,
	ozp.recv_account_name,
	ozp.recv_account_bank,
	ozp.receipts_amount,
	ozp.receipts_mode,
	ozp.receipts_summary,
	ozp.service_status,
	ozp.service_serial_number,
	ozp.bank_serial_number,
	ozp.create_by,
	ozp.create_on,
	ozp.apply_on,
	ozp.update_by,
	ozp.update_on,
	ozp.delete_flag,
	ozp.org_id,
	ozp.dept_id,
	ozp.pay_account_cur,
	ozp.recv_account_cur,
	ozp.pay_bank_cnaps,
	ozp.recv_bank_cnaps,
	ozp.pay_bank_prov,
	ozp.recv_bank_prov,
	ozp.pay_bank_city,
	ozp.recv_bank_city,
	ozp.process_bank_type,
	ozp.persist_version,
	ozp.attachment_count,
	ozp.feed_back,
	ozp.biz_id,
	ozp.biz_name,
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
	outer_sk_receipts ozp,cfm_workflow_run_execute_inst cwrei
WHERE ozp.id = cwrei.bill_id
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
  order by ipd_id
#end