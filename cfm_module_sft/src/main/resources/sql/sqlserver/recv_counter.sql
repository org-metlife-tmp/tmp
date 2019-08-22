#sql("personalList")
SELECT
    org1.name  AS bill_org_name ,
    org2.name AS recv_org_name ,
    recv.id ,
    recv.bill_org_id,
    case recv.source_sys when '0' then 'LA'  when '1' then 'EBS'  when '2' then 'NB'  end  source_sys,
    recv.recv_date ,
    recv.batch_process_no ,
    recv.insure_bill_no ,
    case recv.recv_mode when '0' then 'POS机'  when '1' then '现金解款单'  when '2' then '支票'  when '3' then '网银/汇款'  else '其他' end  recv_mode ,
    recv.recv_acc_no ,
    recv.recv_bank_name ,
    recv.currency ,
    case recv.use_funds when '6' then '保单暂记'  when '7' then '追加投资悬账'  end use_funds,
    case recv.bill_status when '0' then '已到账'  when '1' then '已退票'  end bill_status  ,
    recv.bill_number ,
    recv.bill_date ,
    recv.terminal_no ,
    recv.amount ,
    bank.name AS consumer_bank_name ,
    recv.consumer_acc_no ,
    recv.consumer_no ,
    recv.batch_no ,
    recv.insure_name ,
    recv.insure_cer_no ,
    case recv.isnot_electric_pay  when '0' then '否'  when '1' then '是'  end isnot_electric_pay ,
    case recv.isnot_bank_transfer_premium   when '0' then '否'  when '1' then '是' end isnot_bank_transfer_premium ,
    case recv.third_payment  when '0' then '否'  when '1' then '是' end third_payment,
    recv.check_service_number ,
    recv.payer ,
    recv.payer_cer_no ,
    recv.business_acc ,
    recv.business_acc_no ,
    recv.payer_relation_insured ,
    recv.pay_reason ,
    recv.pay_code ,
    recv.payment_summary ,
    recv.attachment_count ,
    recv.pay_status,
    recv.create_on ,
    recv.create_by ,
    recv.update_on ,
    recv.update_by ,
    recv.delete_flag ,
    recv.persist_version ,
    recv.create_user_name ,
    case recv.is_checked  when '0' then '未核对'  when '1' then '已核对'  end is_checked,
    recv.check_user_name,
    recv.check_date
FROM
	organization AS org1 ,
	organization AS org2 ,
	recv_counter_bill AS recv,
	const_bank_type AS bank
WHERE
	org1.org_id = recv.bill_org_id AND
	org2.org_id = recv.recv_org_id AND
	recv.delete_flag = 0 AND
	bank.code = recv.consumer_bank_name AND
	recv.wait_match_flag = 0
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&!"[]".equals(x.value.toString()))
         AND
        #if("source_sys".equals(x.key))
        	recv.source_sys = #para(x.value)
        #elseif("recv_org_id".equals(x.key))
            recv.recv_org_id = #para(x.value)
        #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.recv_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.recv_date) <= 0
        #elseif("batch_process_no".equals(x.key))
            recv.batch_process_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            recv.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("terminal_no".equals(x.key))
            recv.terminal_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_mode".equals(x.key))
            recv.recv_mode = #para(x.value)
        #elseif("third_payment".equals(x.key))
            recv.third_payment = #para(x.value)
        #elseif("recv_bank_name".equals(x.key))
            recv.recv_bank_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("wait_match_flag".equals(x.key))
            recv.wait_match_flag = #para(x.value)
        #elseif("use_funds".equals(x.key))
            recv.use_funds = #para(x.value)
        #elseif("bill_type".equals(x.key))
            recv.bill_type = #para(x.value)
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
         #elseif("min".equals(x.key))
              recv.amount >= #para(x.value)
         #elseif("max".equals(x.key))
              recv.amount <= #para(x.value)
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  order by recv.id asc
#end


#sql("detailList")
      SELECT
        recv.* ,
        org.name AS bill_org_name
      from 
      recv_counter_bill AS recv ,
      organization AS org
      where
      recv.bill_org_id = org.org_id 
      and 
      recv.batch_process_no = ?
      and 
      recv.delete_flag = 0
#end

#sql("findPendingList")
SELECT
	counter.bill_type ,

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

#sql("findbankcode")
SELECT
	lord.*
FROM
	la_origin_recv_data lord
WHERE
	lord.insure_bill_no = ?
#end