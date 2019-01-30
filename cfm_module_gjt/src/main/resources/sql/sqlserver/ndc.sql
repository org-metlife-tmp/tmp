#sql("findAccInfoByAccno")
  SELECT
    TOP 1
    acc.acc_id,
    acc.acc_no,
    acc.acc_name,
    bank.name as bank_name,
    curr.iso_code as curr_name,
    bank.province as bank_prov,
    bank.city as bank_city,
    bank.bank_type
  FROM
    account acc
  LEFT JOIN currency curr ON acc.curr_id = curr.id
  LEFT JOIN category_value pur ON acc.acc_purpose = pur.[key]
  JOIN (
    SELECT DISTINCT
      org2.org_id,
      org2.name AS org_name
    FROM
      organization org
    JOIN organization org2 ON charindex(
      org.level_code,
      org2.level_code
    ) = 1
    WHERE
      org.org_id = ?
  ) org ON acc.org_id = org.org_id
  JOIN all_bank_info bank ON bank.cnaps_code = acc.bank_cnaps_code
  WHERE
    acc.org_id != ?
  AND acc.is_activity = ?
  AND acc.status = ?
  AND acc.interactive_mode != ?
  AND acc.acc_no = ?
#end

#sql("getCollectRecordTemp")
  SELECT
		count(1) as total_num,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (payment_amount)
      ),
      0.00
    ) as total_amount
  FROM
    collect_batch_bus_attach_detail_temp
  WHERE
    info_id = ?
    and batchno = ?
    and uuid = ?
#end

#sql("getAllCollectRecordTemp")
  SELECT
    COUNT (dt.detail_id) AS total_num,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (dt.payment_amount)
      ),
      0.00
    ) AS total_amount
  FROM
    collect_batch_bus_attach_detail_temp dt
  WHERE
    dt.uuid = ?
  AND dt.batchno = ?
#end

#sql("deleteCBBADTempByInfoId")
  DELETE
  FROM
    collect_batch_bus_attach_detail_temp
  WHERE
    info_id = ?
#end

#sql("deleteCBBAITempByInfoId")
  DELETE
  FROM
    collect_batch_bus_attach_info_temp
  WHERE
    id = ?
#end

#sql("insertCBBAI")
  INSERT INTO collect_batch_bus_attach_info (
    id,
    batchno,
    origin_name,
    file_extension_suffix,
    [number],
    amount
  ) SELECT
    id,
    batchno,
    origin_name,
    file_extension_suffix,
    [number],
    amount
  FROM
    collect_batch_bus_attach_info_temp
  WHERE
    uuid = ?
    and batchno = ?
#end

#sql("insertCBBAD")
  INSERT INTO collect_batch_bus_attach_detail (
	batchno,
	info_id,
	pay_account_id,
	pay_account_no,
	pay_account_name,
	pay_account_bank,
	pay_account_cur,
	pay_bank_cnaps,
	pay_bank_prov,
	pay_bank_city,
	payment_amount,
	pay_status,
	memo,
	update_on,
	update_by,
	feed_back
) SELECT
	dt.batchno,
	dt.info_id,
	dt.pay_account_id,
	dt.pay_account_no,
	dt.pay_account_name,
	dt.pay_account_bank,
	dt.pay_account_cur,
	dt.pay_bank_cnaps,
	dt.pay_bank_prov,
	dt.pay_bank_city,
	dt.payment_amount,
	dt.pay_status,
	dt.memo,
	dt.update_on,
	dt.update_by,
	dt.feed_back
FROM
	collect_batch_bus_attach_detail_temp dt
WHERE
	dt.uuid = ?
AND dt.batchno = ?
#end

#sql("deleteCBBADTemp")
  DELETE
  FROM
    collect_batch_bus_attach_detail_temp
  WHERE
    info_id IN (
      SELECT
        it.id
      FROM
        collect_batch_bus_attach_info_temp it
      WHERE
        it.uuid = ?
      AND it.batchno = ?
    )
#end

#sql("deleteCBBAITemp")
  DELETE
  FROM
    collect_batch_bus_attach_info_temp
  WHERE
    uuid = ?
  AND batchno = ?
#end

#sql("deleteCBBTemp")
  delete from collect_batch_baseinfo_temp where uuid = ? and batchno = ?
#end

#sql("insertCBBAITemp")
  INSERT INTO collect_batch_bus_attach_info_temp (
    id,
    batchno,
    origin_name,
    file_extension_suffix,
    number,
    amount,
    uuid
  ) SELECT
    id,
    batchno,
    origin_name,
    file_extension_suffix,
    number,
    amount,
    ? AS uuid
  FROM
    collect_batch_bus_attach_info
  WHERE
    batchno = ?
#end

#sql("insertCBBADTemp")
  INSERT INTO collect_batch_bus_attach_detail_temp (
    batchno,
    info_id,
    pay_account_id,
    pay_account_no,
    pay_account_name,
    pay_account_cur,
    pay_account_bank,
    pay_bank_cnaps,
    pay_bank_prov,
    pay_bank_city,
    payment_amount,
    pay_status,
    memo,
    update_on,
    update_by,
    feed_back,
    uuid
  ) SELECT
    batchno,
    info_id,
    pay_account_id,
    pay_account_no,
    pay_account_name,
    pay_account_cur,
    pay_account_bank,
    pay_bank_cnaps,
    pay_bank_prov,
    pay_bank_city,
    payment_amount,
    pay_status,
    memo,
    update_on,
    update_by,
    feed_back,
    ? AS uuid
  FROM
    collect_batch_bus_attach_detail
  WHERE
    batchno = ?
#end

#sql("getCurrentCBBAITemp")
  SELECT
    *
  FROM
    collect_batch_bus_attach_info_temp
  WHERE
    uuid = ?
  AND batchno = ?
#end

#sql("deleteCBBAD")
  DELETE
  FROM
    collect_batch_bus_attach_detail
  WHERE
    batchno = ?
#end

#sql("deleteCBBAI")
  DELETE
  FROM
    collect_batch_bus_attach_info
  WHERE
    batchno = ?
#end

#sql("accs")
  SELECT
    acc.acc_id as recv_account_id,
    acc.acc_no as recv_account_no,
    acc.acc_name as recv_account_name,
    bank.name as recv_account_bank,
    bank.cnaps_code as recv_bank_cnaps,
    curr.iso_code AS recv_account_cur,
	  bank.province AS recv_bank_prov,
	  bank.city AS recv_bank_city,
	  bank.bank_type as process_bank_type
  FROM
    account acc
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  LEFT JOIN category_value pur
  	ON acc.acc_purpose = pur.[key]
  LEFT JOIN organization org
    ON acc.org_id = org.org_id
  LEFT JOIN all_bank_info bank ON bank.cnaps_code = acc.bank_cnaps_code
  WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
      AND
        #if("level_code".equals(x.key))
            org.level_code in(
              #for(y : map.level_code)
                #if(for.index > 0)
                  #(",")
                #end
                '#(y)'
              #end
            )
        #elseif("is_activity".equals(x.key))
          acc.is_activity = #para(x.value)
        #elseif("status".equals(x.key))
          acc.status = #para(x.value)
        #else
            #(x.key) = #para(x.value)
          #end
      #end
    #end
  #end
#end

#sql("findCBBPendingList")
   SELECT
	obb.id as obb_id,
	obb.recv_account_id,
  obb.recv_account_no,
  obb.recv_account_name,
  obb.recv_account_cur,
  obb.recv_account_bank,
  obb.recv_bank_cnaps,
  obb.recv_bank_prov,
  obb.recv_bank_city,
  obb.create_on,
  obb.apply_on,
  obb.total_num,
  obb.batchno,
  obb.total_amount,
  obb.payment_summary as memo,
  obb.persist_version,
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
	cwrei.start_time,
	obb.service_status
FROM
	collect_batch_baseinfo obb,cfm_workflow_run_execute_inst cwrei
WHERE obb.id = cwrei.bill_id
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
  order by obb.id
#end


#sql("billdetail")
  SELECT
    obb.id,
    obb.total_num,
    obb.total_amount,
    obb.recv_account_no,
    obb.recv_account_bank,
    obb.payment_summary,
    obb.attachment_count,
    obb.batchno,
    obb.service_status,
    obb.persist_version,
    obb.org_id,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        obbad.payment_amount
      ELSE
        0.00
      END
    ) saved_amount,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        1
      ELSE
        0
      END
    ) saved_num,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        obbad.payment_amount
      ELSE
        0.00
      END
    ) failed_amount,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        1
      ELSE
        0
      END
    ) failed_num,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        obbad.payment_amount
      ELSE
        0.00
      END
    ) success_amount,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        1
      ELSE
        0
      END
    ) success_num,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        obbad.payment_amount
      ELSE
        0.00
      END
    ) cancel_amount,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        1
      ELSE
        0
      END
    ) cancel_num,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        obbad.payment_amount
      ELSE
        0.00
      END
    ) process_amount,
    SUM (
      CASE
      WHEN obbad.pay_status = ? THEN
        1
      ELSE
        0
      END
    ) process_num
  FROM
    collect_batch_baseinfo obb
  JOIN collect_batch_bus_attach_detail obbad ON obbad.batchno = obb.batchno
  WHERE
    obb.id = ?
  GROUP BY
    obb.id,
    obb.total_num,
    obb.total_amount,
    obb.recv_account_no,
    obb.recv_account_bank,
    obb.payment_summary,
    obb.attachment_count,
    obb.batchno,
    obb.service_status,
    obb.persist_version,
    obb.org_id
#end

#sql("attclist")
  SELECT
    *
  FROM
    collect_batch_bus_attach_detail
  where 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #(x.key) = #para(x.value)
        #end
    #end
  #end
#end

#sql("morebillsum")
  SELECT
    obb.id,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (obbad.payment_amount)
      ),
      0.00
    ) AS success_amount,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        obb.total_amount
      ),
      0.00
    ) AS total_amount
  FROM
    collect_batch_baseinfo obb
  LEFT JOIN collect_batch_bus_attach_detail obbad ON obb.batchno = obbad.batchno
  left join organization org on org.org_id = obb.org_id
  and obbad.pay_status in(
      #for(y : map.pay_status)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
    )
  WHERE
    1 = 1
    and obb.delete_flag = 0
    #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("pay_status".equals(x.key))
              #continue
            #end
            #if("service_status".equals(x.key) && map.service_status.size()==0)
              #continue
            #end
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.create_on) <= 0
            #elseif("recv_account_no".equals(x.key))
              obb.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("pay_account_no".equals(x.key))
              obbad.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("min_amount".equals(x.key))
              obbad.payment_amount >= convert(decimal(18,2),#para(x.value))
            #elseif("max_amount".equals(x.key))
              obbad.payment_amount <= convert(decimal(18,2),#para(x.value))
            #elseif("service_status".equals(x.key))
              obb.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("level_code".equals(x.key))
              org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("level_num".equals(x.key))
              org.level_num >= #para(x.value)
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
    #end
    GROUP BY
      obb.id,
      obb.total_amount
#end

#sql("morebill")
  SELECT
    obb.id,
    obb.batchno,
    obb.total_num,
    obb.total_amount,
    obb.persist_version as version,
    COUNT (obbad.batchno) AS success_num,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (obbad.payment_amount)
      ),
      0.00
    ) AS success_amount,
    obb.service_status
  FROM
    collect_batch_baseinfo obb
  LEFT JOIN collect_batch_bus_attach_detail obbad ON obb.batchno = obbad.batchno
left join organization org on obb.org_id = org.org_id
  and obbad.pay_status in(
      #for(y : map.pay_status)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
    )
  WHERE
    1 = 1
    and obb.delete_flag = 0
    #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("pay_status".equals(x.key))
              #continue
            #end
            #if("service_status".equals(x.key) && map.service_status.size()==0)
              #continue
            #end
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.create_on) <= 0
            #elseif("recv_account_no".equals(x.key))
              obb.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("pay_account_no".equals(x.key))
              obbad.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("min_amount".equals(x.key))
              obbad.payment_amount >= convert(decimal(18,2),#para(x.value))
            #elseif("max_amount".equals(x.key))
              obbad.payment_amount <= convert(decimal(18,2),#para(x.value))
            #elseif("service_status".equals(x.key))
              obb.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("level_code".equals(x.key))
              org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("level_num".equals(x.key))
              org.level_num >= #para(x.value)
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
    #end
  GROUP BY
    obb.id,
    obb.batchno,
    obb.total_num,
    obb.total_amount,
    obb.service_status,
    obb.persist_version
  order by service_status asc,id desc
#end

#sql("billdetailsum")
  SELECT
    count(batchno) as total_num,
    isnull(convert(decimal(16,2),sum(payment_amount)),0.00) as total_amount
  FROM
    collect_batch_bus_attach_detail
  WHERE
    1 = 1
  AND batchno = #para(map.batchno)
  #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("batchno".equals(x.key))
              #continue
            #end

            AND
            #if("query_key".equals(x.key))
              (pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
              pay_account_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("min_amount".equals(x.key))
              payment_amount >= convert(decimal(18,2),#para(x.value))
            #elseif("max_amount".equals(x.key))
              payment_amount <= convert(decimal(18,2),#para(x.value))
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
    #end
#end

#sql("billdetaillist")
  SELECT
    *
  FROM
    collect_batch_bus_attach_detail
  WHERE
    1 = 1
  AND batchno = #para(map.batchno)
  #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("batchno".equals(x.key))
              #continue
            #end

            AND
            #if("query_key".equals(x.key))
              (pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
              pay_account_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("min_amount".equals(x.key))
              payment_amount >= convert(decimal(18,2),#para(x.value))
            #elseif("max_amount".equals(x.key))
              payment_amount <= convert(decimal(18,2),#para(x.value))
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
  #end
  order by  detail_id desc
#end

#sql("findAttachDetailToPage")
SELECT
	detail_id,
	batchno,
	info_id,
	pay_account_id,
	pay_account_no,
	pay_account_name,
	pay_account_cur,
	pay_account_bank,
	pay_bank_cnaps,
	pay_bank_prov,
	pay_bank_city,
	payment_amount,
	pay_status,
	memo,
	update_on,
	update_by,
	feed_back
FROM
	collect_batch_bus_attach_detail
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_status".equals(x.key))
              pay_status in(
                #for(y : map.pay_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
          #else
            #(x.key) = '#(x.value)'
          #end
        #end
    #end
  #end
  order by detail_id desc
#end

#sql("findAttachInfoByIDBatchno")
SELECT
	[number] as total_num,
	amount as total_amount
FROM
	collect_batch_bus_attach_info d
WHERE 1=1
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          and
          #(x.key) = '#(x.value)'
        #end
    #end
  #end
#end