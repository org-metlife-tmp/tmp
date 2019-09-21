#sql("findBranchItemPayById")
  select * from oa_branch_payment_item where id = ? and delete_flag = 0
#end

#sql("findOaList")
  SELECT
     obpi.pay_account_no  AS pool_account_no ,
     obpi.recv_account_no AS pay_pay_account_no,
     od.bill_no,
     obp.id,
    obp.org_id,
    obp.dept_id,
    obp.recv_account_id,
    obp.recv_account_no,
    obp.recv_account_name,
    obp.recv_account_cur,
    obp.recv_account_bank,
    obp.recv_bank_cnaps,
    obp.recv_bank_prov,
    obp.recv_bank_city,
    obp.payment_amount,
    obp.pay_mode,
    obp.payment_summary,
    obp.service_status,
    obp.service_serial_number,
    obp.create_by,
    obp.update_by,
    obp.delete_flag,
    obp.persist_version,
    obp.attachment_count,
    obp.ref_id,
    obp.feed_back,
     CONVERT(varchar(100), obp.create_on, 23) as create_on,
     CONVERT(varchar(100), obp.update_on, 23) as update_on,
     org.name as org_name,
     obpi.bank_serial_number
  FROM
     oa_branch_payment  obp
     left join oa_branch_payment_item obpi
     on obp.id  =  obpi.base_id
     and obpi.item_type = 1
     and obpi.delete_flag = 0

     #if (map != null && map.org_id != null )
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
        ) = 1 and org.org_id = #(map.org_id)
      ) org
      on
        org.org_id =  obp.org_id
     #end
  JOIN oa_origin_data od on od.id = obp.ref_id
  WHERE   
     obp.delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")

          #if("org_id".equals(x.key))
              #continue
          #end

          AND
          #if("recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
              obp.#(x.key) like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            obp.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
              obp.payment_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            obp.payment_amount <= #para(x.value)
          #elseif("apply_start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),obp.create_on) >= 0
          #elseif("apply_end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obp.create_on) <= 0
          #elseif("send_start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),obp.update_on) >= 0
          #elseif("send_end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obp.update_on) <= 0
          #elseif("org_name".equals(x.key))
            org.name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("bill_no".equals(x.key))
            od.bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            obp.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by obp.id desc
#end



#sql("findPendingList")
SELECT
	obp.id as ipd_id,
	obpi.pay_account_id,
	obpi.pay_account_no,
	obpi.pay_account_name,
	obpi.pay_account_bank,
	obpi.pay_bank_cnaps,
	obpi.pay_bank_prov,
	obpi.pay_bank_city,
	obp.recv_account_id,
	obp.recv_account_no,
	obp.recv_account_name,
	obp.recv_account_bank,
	obp.recv_account_cur ,
	obp.recv_bank_prov ,
	obp.recv_bank_city ,
	obp.recv_bank_cnaps ,
	obp.payment_amount,
	obp.pay_mode,
	obp.payment_summary,
	obp.service_status,
	obp.service_serial_number,
	obp.create_by,
	obp.create_on,
	obp.update_by,
	obp.update_on,
	obp.delete_flag,
	obp.persist_version,
	obp.attachment_count,
	obp.ref_id,
	od.bill_no,
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
	oa_branch_payment obp,cfm_workflow_run_execute_inst cwrei , oa_branch_payment_item obpi,oa_origin_data od,organization org
WHERE obp.id = cwrei.bill_id
  AND obp.id = obpi.base_id
  AND od.id = obp.ref_id
  AND org.org_id = obp.org_id
  AND obpi.item_type = 1
  AND obpi.delete_flag = 0
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

#sql("findBillDetail")
  SELECT
     obp.* ,
     od.bill_no,
     org.name as org_name,
     obpi.id  AS item_id,
     obpi.pay_account_id AS pool_account_id,
     obpi.pay_account_no AS pool_account_no,
     obpi.pay_account_name AS pool_account_name,
     obpi.pay_account_cur AS pool_account_cur,
     obpi.pay_account_bank AS pool_account_bank,
     obpi.pay_bank_cnaps AS pool_bank_cnaps,
     obpi.pay_bank_prov AS  pool_bank_prov,
     obpi.pay_bank_city AS pool_bank_city,
     obpi.recv_account_id AS pay_account_id,
     obpi.recv_account_no AS pay_account_no,
     obpi.recv_account_name AS pay_account_name,
     obpi.recv_account_cur AS pay_account_cur,
     obpi.recv_account_bank AS pay_account_bank,
     obpi.recv_bank_cnaps AS pay_bank_cnaps,
     obpi.recv_bank_prov AS pay_bank_prov,
     obpi.recv_bank_city AS pay_bank_city,
     case obpi.item_type when 1 then '下拨' when 2 then '对外支付'  end  item_type
  FROM
     oa_branch_payment obp
  JOIN
     oa_origin_data od on od.id = obp.ref_id
  JOIN
    organization org on obp.org_id = org.org_id
  left join
     oa_branch_payment_item  obpi 
  on  
     obp.id  =  obpi.base_id  	
  and 
     obpi.delete_flag = 0 
  and 
     obpi.item_type = 1
  WHERE 
     obp.id = ?
  AND
     obp.delete_flag = 0 
#end




#sql("findDetail")
  SELECT
     id ,
     base_id ,
     org_id ,
     dept_id ,
     pay_account_id ,
     pay_account_no ,
     pay_account_name ,
     pay_account_cur ,
     pay_account_bank ,
     pay_bank_cnaps ,
     pay_bank_prov ,
     pay_bank_city ,
     recv_account_id ,
     recv_account_no ,
     recv_account_name ,
     recv_account_cur ,
     recv_account_bank ,
     recv_bank_cnaps ,
     recv_bank_prov ,
     recv_bank_city ,
     payment_amount ,
     pay_mode ,
     payment_summary ,
     service_status ,
     service_serial_number ,
     bank_serial_number ,
     repeat_count ,
     create_by ,
     create_on ,
     update_by ,
     update_on ,
     delete_flag ,
     process_bank_type ,
     case item_type when 1 then '下拨' when 2 then '对外支付'  end  item_type,
     attachment_count ,
     feed_back ,
     is_checked ,
     instruct_code ,
     statement_code 
  FROM
     oa_branch_payment_item  obpi 
  WHERE 
  obpi.delete_flag = 0 
  AND
  obpi.base_id = ?
#end

#sql("findDetailByItem")
  SELECT
     id ,
     base_id ,
     org_id ,
     dept_id ,
     pay_account_id ,
     pay_account_no ,
     pay_account_name ,
     pay_account_cur ,
     pay_account_bank ,
     pay_bank_cnaps ,
     pay_bank_prov ,
     pay_bank_city ,
     recv_account_id ,
     recv_account_no ,
     recv_account_name ,
     recv_account_cur ,
     recv_account_bank ,
     recv_bank_cnaps ,
     recv_bank_prov ,
     recv_bank_city ,
     payment_amount ,
     pay_mode ,
     payment_summary ,
     service_status ,
     service_serial_number ,
     bank_serial_number ,
     repeat_count ,
     create_by ,
     create_on ,
     update_by ,
     update_on ,
     delete_flag ,
     process_bank_type ,
     item_type,
     attachment_count ,
     feed_back ,
     is_checked ,
     instruct_code ,
     statement_code,
     instruct_code 
  FROM
     oa_branch_payment_item  obpi 
  WHERE 
  obpi.delete_flag = 0 and item_type = ?
  AND
  obpi.base_id = ?
#end


#sql("updBillById")
   update oa_branch_payment_item set bank_serial_number = ?,repeat_count = ?,service_status = ?,instruct_code = ?
   where id = ? and repeat_count = ? and service_status = ?
#end

#sql("findOaBranchItemToList")
  SELECT
	obp.ref_id,
	item.id,
	item.base_id,
	item.org_id,
	item.dept_id,
	item.pay_account_id,
	item.pay_account_no,
	item.pay_account_name,
	item.pay_account_cur,
	item.pay_account_bank,
	item.pay_bank_cnaps,
	item.pay_bank_prov,
	item.pay_bank_city,
	item.recv_account_id,
	item.recv_account_no,
	item.recv_account_name,
	item.recv_account_cur,
	item.recv_account_bank,
	item.recv_bank_cnaps,
	item.recv_bank_prov,
	item.recv_bank_city,
	item.payment_amount,
	item.pay_mode,
	item.payment_summary,
	item.service_status,
	item.service_serial_number,
	item.bank_serial_number,
	item.repeat_count,
	item.create_by,
	item.create_on,
	item.update_by,
	item.update_on,
	item.delete_flag,
	item.process_bank_type,
	item.item_type,
	item.attachment_count,
	item.persist_version,
	item.feed_back,
	item.is_checked,
	item.instruct_code,
	item.statement_code,
	item.refund_flag
FROM
	oa_branch_payment obp,oa_branch_payment_item item
WHERE obp.id = item.base_id
AND item.id = ?
#end