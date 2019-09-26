#sql("grouplist")
SELECT
	org1.name AS bill_org_name,
	tab.*
FROM
	(
    SELECT
      org2.name AS recv_org_name,
      recv.*
    FROM
      organization AS org2,
      recv_counter_bill AS recv
    WHERE
      org2.org_id = recv.recv_org_id
      AND recv.delete_flag = 0
      AND recv.bill_type = 1
      AND recv.wait_match_flag = 0
      #if(map != null)
        #for(x : map)
          #if(x.value&&x.value!=""&&!"[]".equals(x.value.toString()))
             AND
            #if("recv_org_id".equals(x.key))
                recv.recv_org_id = #para(x.value)
            #elseif("start_date".equals(x.key))
                DATEDIFF(day,#para(x.value),recv.recv_date) >= 0
            #elseif("end_date".equals(x.key))
                DATEDIFF(day,#para(x.value),recv.recv_date) <= 0
            #elseif("recv_org_id".equals(x.key))
                org2.org_id  = #para(x.value)
            #elseif("batch_no".equals(x.key))
                recv.batch_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("preinsure_bill_no".equals(x.key))
                recv.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("insure_bill_no".equals(x.key))
                recv.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("consumer_no".equals(x.key))
                recv.consumer_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("recv_bank_name".equals(x.key))
                recv.recv_bank_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("bill_status".equals(x.key))
                recv.bill_status in(
                  #for(y : map.bill_status)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(y)
                  #end
                )
            #elseif("pay_status".equals(x.key))
                recv.pay_status in(
                  #for(z : map.pay_status)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(z)
                  #end
                )
            #elseif("org_ids".equals(x.key))
            recv.recv_org_id in(
              #for(z : map.org_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
             #elseif("min".equals(x.key))
                recv.amount >= #para(x.value)
             #elseif("max".equals(x.key))
                recv.amount <= #para(x.value)
             #else
                recv.#(x.key) = #para(x.value)
            #end
          #end
        #end
      #end) tab
	LEFT JOIN organization org1 ON org1.org_id = tab.bill_org_id
  order by tab.id asc
#end


#sql("getBankcode")
SELECT
	acc.bankcode,
	acc.acc_no
FROM
	account acc,
	organization org
WHERE acc.bankcode IS NOT NULL
AND acc.org_id = org.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("level_code".equals(x.key))
        	charindex(#para(x.value), org.level_code ) = 1
        #elseif("bankcode".equals(x.key))
          acc.bankcode  = #para(x.value)
        #else
          org.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findPendingList")
SELECT
	counter.*,
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
	recv_counter_bill counter,
	cfm_workflow_run_execute_inst cwrei
WHERE counter.id = cwrei.bill_id
and counter.delete_flag = 0
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
  order by counter.id
#end
