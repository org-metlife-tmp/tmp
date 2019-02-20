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
    outer_batchpay_bus_attach_detail_temp dt
  WHERE
    dt.uuid = ?
  AND dt.batchno = ?
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
    outer_batchpay_bus_attach_detail_temp
  WHERE
    info_id = ?
    and batchno = ?
    and uuid = ?
#end

#sql("insertOBBAI")
  INSERT INTO outer_batchpay_bus_attach_info (
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
    outer_batchpay_bus_attach_info_temp
  WHERE
    uuid = ?
    and batchno = ?
#end

#sql("insertOBBAD")
  INSERT INTO outer_batchpay_bus_attach_detail (
	batchno,
	info_id,
	recv_account_id,
	recv_account_no,
	recv_account_name,
	recv_account_bank,
	recv_account_cur,
	recv_bank_cnaps,
	recv_bank_prov,
	recv_bank_city,
	payment_amount,
	pay_status,
	memo,
	update_on,
	update_by,
	feed_back
) SELECT
	dt.batchno,
	dt.info_id,
	dt.recv_account_id,
	dt.recv_account_no,
	dt.recv_account_name,
	dt.recv_account_bank,
	dt.recv_account_cur,
	dt.recv_bank_cnaps,
	dt.recv_bank_prov,
	dt.recv_bank_city,
	dt.payment_amount,
	dt.pay_status,
	dt.memo,
	dt.update_on,
	dt.update_by,
	dt.feed_back
FROM
	outer_batchpay_bus_attach_detail_temp dt
WHERE
	dt.uuid = ?
AND dt.batchno = ?
#end

#sql("deleteOBBAITemp")
  DELETE
  FROM
    outer_batchpay_bus_attach_info_temp
  WHERE
    uuid = ?
  AND batchno = ?
#end

#sql("deleteOBBADTemp")
  DELETE
  FROM
    outer_batchpay_bus_attach_detail_temp
  WHERE
    info_id IN (
      SELECT
        it.id
      FROM
        outer_batchpay_bus_attach_info_temp it
      WHERE
        it.uuid = ?
      AND it.batchno = ?
    )
#end

#sql("deleteOBBADTempByInfoId")
  DELETE
  FROM
    outer_batchpay_bus_attach_detail_temp
  WHERE
    info_id = ?
#end

#sql("deleteOBBAITempByInfoId")
  DELETE
  FROM
    outer_batchpay_bus_attach_info_temp
  WHERE
    id = ?
#end

#sql("deleteOBBTemp")
  delete from outer_batchpay_baseinfo_temp where uuid = ? and batchno = ?
#end

#sql("insertOBBAITemp")
  INSERT INTO outer_batchpay_bus_attach_info_temp (
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
    outer_batchpay_bus_attach_info
  WHERE
    batchno = ?
#end

#sql("insertOBBADTemp")
  INSERT INTO outer_batchpay_bus_attach_detail_temp (
    batchno,
    info_id,
    recv_account_id,
    recv_account_no,
    recv_account_name,
    recv_account_cur,
    recv_account_bank,
    recv_bank_cnaps,
    recv_bank_prov,
    recv_bank_city,
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
    recv_account_id,
    recv_account_no,
    recv_account_name,
    recv_account_cur,
    recv_account_bank,
    recv_bank_cnaps,
    recv_bank_prov,
    recv_bank_city,
    payment_amount,
    pay_status,
    memo,
    update_on,
    update_by,
    feed_back,
    ? AS uuid
  FROM
    outer_batchpay_bus_attach_detail
  WHERE
    batchno = ?
#end

#sql("getCurrentOBBAITemp")
  SELECT
    *
  FROM
    outer_batchpay_bus_attach_info_temp
  WHERE
    uuid = ?
  AND batchno = ?
#end

#sql("deleteOBBAD")
  DELETE
  FROM
    outer_batchpay_bus_attach_detail
  WHERE
    batchno = ?
#end

#sql("deleteOBBAI")
  DELETE
  FROM
    outer_batchpay_bus_attach_info
  WHERE
    batchno = ?
#end

#sql("attclist")
  SELECT
    *
  FROM
    outer_batchpay_bus_attach_detail
  where 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #(x.key) = #para(x.value)
        #end
    #end
  #end
  order by detail_id
#end

#sql("morebill")
  SELECT
    obb.id,
    obb.batchno,
    obb.total_num,
    obb.total_amount,
    obb.persist_version as version,
    obb.pay_mode,
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
    outer_batchpay_baseinfo obb
  LEFT JOIN outer_batchpay_bus_attach_detail obbad ON obb.batchno = obbad.batchno

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
              DATEDIFF(day,#para(x.value),obb.apply_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.apply_on) <= 0
            #elseif("pay_account_no".equals(x.key))
              obb.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("recv_account_no".equals(x.key))
              obbad.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
    obb.persist_version,
    obb.pay_mode
  order by obb.id desc
#end

#sql("getAllCollectRecord")
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
    outer_batchpay_bus_attach_detail dt
  WHERE
    dt.batchno = ?
#end

#sql("getCollectRecord")
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
    outer_batchpay_bus_attach_detail
  WHERE
    info_id = ?
    and batchno = ?
#end


#sql("findOBPPendingList")
SELECT
	obb.id as obb_id,
	obb.pay_account_id,
	obb.pay_account_no,
	obb.pay_account_name,
	obb.pay_account_bank,
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
	outer_batchpay_baseinfo obb,
	cfm_workflow_run_execute_inst cwrei
WHERE
	obb.id = cwrei.bill_id
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
    obb.pay_account_no,
    obb.pay_account_bank,
    obb.payment_summary,
    obb.attachment_count,
    obb.batchno,
    obb.org_id,
    obb.service_status,
    obb.persist_version,
    obb.biz_id,
    obb.biz_name,
    obb.pay_mode,
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
    outer_batchpay_baseinfo obb
  JOIN outer_batchpay_bus_attach_detail obbad ON obbad.batchno = obb.batchno
  WHERE
    obb.id = ?
  GROUP BY
    obb.id,
    obb.total_num,
    obb.total_amount,
    obb.pay_account_no,
    obb.pay_account_bank,
    obb.payment_summary,
    obb.attachment_count,
    obb.batchno,
    obb.service_status,
    obb.org_id,
    obb.persist_version,
    obb.biz_id,
    obb.biz_name,
    obb.pay_mode
#end

#sql("billdetaillist")
  SELECT
    *
  FROM
    outer_batchpay_bus_attach_detail
  WHERE
    1 = 1
  AND batchno = #para(map.batchno)
  #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            #if("batchno".equals(x.key))
              #continue
            #end

            #if("pay_status".equals(x.key) && map.pay_status.size()==0)
              #continue
            #end

            AND
            #if("query_key".equals(x.key))
              (recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
              recv_account_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("pay_status".equals(x.key))
              pay_status in(
                #for(y : map.pay_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
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
    order by detail_id
#end

#sql("billdetailsum")
  SELECT
    count(batchno) as total_num,
    isnull(convert(decimal(16,2),sum(payment_amount)),0.00) as total_amount
  FROM
    outer_batchpay_bus_attach_detail
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
              (recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
              recv_account_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("pay_status".equals(x.key))
              pay_status in(
                #for(y : map.pay_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
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
    outer_batchpay_baseinfo obb
  LEFT JOIN outer_batchpay_bus_attach_detail obbad ON obb.batchno = obbad.batchno

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
              DATEDIFF(day,#para(x.value),obb.apply_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),obb.apply_on) <= 0
            #elseif("pay_account_no".equals(x.key))
              obb.pay_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("recv_account_no".equals(x.key))
              obbad.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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

#sql("findMoreInfo")
select * from (
SELECT DISTINCT
	zft.*,
	(select count(ad.detail_id) from outer_batchpay_bus_attach_detail ad where ad.batchno = zft.batchno and ad.pay_status in(0,2)) as todo_num,
	(select isnull(convert(decimal(18,2),SUM(ad.payment_amount)), 0.00) from outer_batchpay_bus_attach_detail ad where ad.batchno = zft.batchno and ad.pay_status in(0,2)) as todo_sum,
	bank.bank_type as pay_bank_type
FROM
	outer_batchpay_baseinfo zft left join organization org on (zft.org_id = org.org_id) ,
	all_bank_info bank,outer_batchpay_bus_attach_detail oad
WHERE 1=1 and zft.pay_bank_cnaps = bank.cnaps_code and oad.batchno = zft.batchno
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key))
            zft.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_account_name".equals(x.key) || "recv_account_no".equals(x.key))
            oad.#(x.key) >= #para(x.value)
          #elseif("service_status".equals(x.key))
            zft.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
            zft.total_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            zft.total_amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),zft.apply_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),zft.apply_on) <= 0
          #else
            zft.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  ) zftbatch
  order by id
#end


#sql("findAllAmount")

select SUM(zftbatch.total_amount) as total_amount,
	sum (zftbatch.total_num) as total_num
	from (
SELECT
  distinct
  zft.id,
  zft.total_amount,
  zft.total_num
FROM
	outer_batchpay_baseinfo zft left join organization org on (zft.org_id = org.org_id) ,
	outer_batchpay_bus_attach_detail oad
WHERE 1=1 and oad.batchno = zft.batchno
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key))
            zft.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("recv_account_name".equals(x.key) || "recv_account_no".equals(x.key))
            oad.#(x.key) >= #para(x.value)
          #elseif("service_status".equals(x.key))
            zft.service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
            zft.total_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            zft.total_amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),zft.apply_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),zft.apply_on) <= 0
          #else
            zft.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
) zftbatch
#end

###统计当前批次除已勾选批次的其他明细数量
#sql("billDetailCancelSum")
  SELECT
	COUNT (detail_id)
FROM
	outer_batchpay_bus_attach_detail
WHERE
	batchno = #para(batchno)
AND pay_status != #para(pay_status)
and detail_id not in(
  #for(y : ids)
    #if(for.index > 0)
      #(",")
    #end
    #(y)
  #end
)
#end

#sql("updateBillDetail")
  update outer_batchpay_bus_attach_detail
  set pay_status = #para(pay_status),
      update_on = #para(update_on),
      update_by = #para(update_by),
      feed_back = #para(feed_back)
  where detail_id in(
    #for(y : ids)
      #if(for.index > 0)
        #(",")
      #end
      #(y)
    #end
  )
#end

#sql("updateBillDetail2")
  update outer_batchpay_bus_attach_detail
  set pay_status = #para(pay_status),
      update_on = #para(update_on),
      update_by = #para(update_by)
  where detail_id in(
    #for(y : ids)
      #if(for.index > 0)
        #(",")
      #end
      #(y)
    #end
  )
#end



#sql("findBatchSendInfoByDetailId")
select
  detail.bank_serial_number,
  detail.repeat_count,
	detail.detail_id,
	detail.batchno,
	base.pay_account_id,
	base.pay_account_no,
	base.pay_account_name,
	base.pay_account_cur,
	base.pay_account_bank,
	base.pay_bank_cnaps,
	base.pay_bank_prov,
	base.pay_bank_city,
	base.biz_id,
	base.service_status,
	detail.recv_account_id,
	detail.recv_account_no,
	detail.recv_account_name,
	detail.recv_account_cur,
	detail.recv_account_bank,
	detail.recv_bank_cnaps,
	detail.recv_bank_prov,
	detail.recv_bank_city,
	detail.payment_amount,
	detail.pay_status,
	detail.memo,
    detail.repeat_count,
    detail.instruct_code,
    detail.persist_version
from
	outer_batchpay_baseinfo base ,
	outer_batchpay_bus_attach_detail detail
where
	base.batchno = detail.batchno
	and detail.detail_id = ?;
#end

#sql("updateDetailById")
   update outer_batchpay_bus_attach_detail
   set bank_serial_number = ?,repeat_count = ?,pay_status = ?,instruct_code = ? 
   where detail_id = ? and repeat_count = ? and pay_status = ?
#end
    
#sql("findBillById")
   SELECT 
     * 
   FROM
 outer_batchpay_baseinfo
   WHERE 
   id = ?
   order by id
#end

#sql("findBillByBatchno")
   SELECT
     *
   FROM
 outer_batchpay_baseinfo
   WHERE
   batchno = ?
#end

#sql("findBillByTrade")
SELECT
	obb.*
FROM
	outer_batchpay_baseinfo obb
WHERE 1=1 
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key))
            pay_account_no in(
              #for(y : map.pay_account_no)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
           #elseif("total_amount".equals(x.key))
            total_amount in(
              #for(y : map.total_amount)
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
  order by obb.id
#end

#sql("getCanBatchnoByUser")
SELECT
	DISTINCT ibb.batchno
FROM
	outer_batchpay_baseinfo AS ibb
LEFT OUTER JOIN outer_batchpay_bus_attach_detail AS ibbat ON
	ibb.batchno = ibbat.batchno
WHERE
	( ibb.delete_flag = 0 )
-- AND ( ibb.org_id = ? )
AND ( ibb.create_by = ? )
#end

#sql("getAllCanBatchno")
SELECT
	DISTINCT ibb.batchno
FROM
	outer_batchpay_baseinfo AS ibb
LEFT OUTER JOIN outer_batchpay_bus_attach_detail AS ibbat ON
	ibb.batchno = ibbat.batchno
left join organization org on(org.org_id = ibb.org_id)
WHERE
	( ibb.delete_flag = 0 )
and org.level_code like convert(varchar(5),'%')+convert(varchar(255),?)+convert(varchar(5),'%')
and org.level_num >= ?
#end

#sql("getBatchBaseInfoByBatchNo")
SELECT
	fin.id,
	fin.batchno,
	fin.total_num,
	fin.pay_account_no,
	fin.pay_account_name,
	fin.pay_account_bank,
	fin.pay_bank_cnaps,
	fin.total_amount,
	fin.success_num,
	fin.success_amount,
	fin.version,
	fin.service_status,
	fin.create_by,
	fin.apply_on,
	fin.update_by,
	fin.update_on,
	fin.pay_mode
FROM
	(
	SELECT
		ibb.id,
		ibb.persist_version,
		ibb.batchno,
		ibb.pay_account_no,
		ibb.pay_account_name,
		ibb.pay_account_bank,
		ibb.pay_bank_cnaps,
		ibb.total_num ,
		ibb.total_amount,
		sum(case ibbat.pay_status when 1 then 1 else 0 end ) as success_num,
		ISNULL( SUM(case ibbat.pay_status when 1 then ibbat.payment_amount else 0 end ), 0 ) AS success_amount ,
		ibb.persist_version as version ,
		ibb.service_status,
		ibb.create_by,
		ibb.apply_on,
		ibb.update_by,
		ibb.update_on,
		ibb.pay_mode
	FROM
		outer_batchpay_baseinfo ibb
	left join outer_batchpay_bus_attach_detail ibbat on
		( ibb.batchno = ibbat.batchno )
-- 		and ibbat.pay_status =1
  WHERE 1=1
    #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("pay_account_name".equals(x.key) ||"pay_account_no".equals(x.key) || "recv_account_name".equals(x.key) ||"recv_account_no".equals(x.key))
              #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("batchnos".equals(x.key))
              ibb.batchno IN (
                #for(y : map.batchnos)
                  #if(for.index > 0)
                    #(",")
                  #end
                  '#(y.batchno)'
                #end
              )
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
              total_amount >= #para(x.value)
            #elseif("max".equals(x.key))
              total_amount <= #para(x.value)
            #elseif("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) <= 0
            #else
              #(x.key) = '#(x.value)'
            #end
          #end
      #end
    #end
	GROUP BY
		ibb.service_status,
		ibb.id,
		ibb.batchno,
		ibb.pay_account_no,
		ibb.pay_account_name,
		ibb.pay_account_bank,
		ibb.pay_bank_cnaps,
		ibb.total_num,
		ibb.total_amount,
		ibb.persist_version,
		ibb.create_by,
		ibb.apply_on,
		ibb.update_by,
		ibb.update_on,
		ibb.pay_mode
	) fin
	ORDER by fin.service_status asc, fin.id desc
#end

#sql("findmorelisttotal")
SELECT DISTINCT
	sum(detail.payment_amount) as total_amount,
	(SELECT DISTINCT sum(payment_amount) FROM outer_batchpay_bus_attach_detail detail,outer_batchpay_baseinfo base
	WHERE detail.batchno = base.batchno and  pay_status=1
	  #if(map.batchnos != null && !"".equals(map.batchnos))
		  AND base.batchno in (
		    #for(y : map.batchnos)
          #if(for.index > 0)
            #(",")
          #end
          '#(y.batchno)'
        #end
		  )
		#end
		#if(map.service_status != null && !"".equals(map.service_status))
        AND base.service_status in(
          #for(y : map.service_status)
            #if(for.index > 0)
              #(",")
            #end
            #(y)
          #end
        )
		#end
		#if(map != null)
		  #for(x : map)
          #if(x.value&&x.value!="")
            #if(!"batchnos".equals(x.key) && !"service_status".equals(x.key))
              AND
            #end
            #if("pay_account_name".equals(x.key) ||"pay_account_no".equals(x.key) || "recv_account_name".equals(x.key) ||"recv_account_no".equals(x.key))
              #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("min".equals(x.key))
              total_amount >= #para(x.value)
            #elseif("max".equals(x.key))
              total_amount <= #para(x.value)
            #elseif("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) <= 0
            #elseif("batchnos".equals(x.key))
              #continue
            #elseif("service_status".equals(x.key))
              #continue
            #else
              #(x.key) = '#(x.value)'
            #end
          #end
      #end
    #end
	) as success_amount
FROM
	outer_batchpay_baseinfo base,outer_batchpay_bus_attach_detail detail
WHERE base.batchno = detail.batchno
  #if(map.batchnos != null && !"".equals(map.batchnos))
    AND base.batchno in (
      #for(y : map.batchnos)
        #if(for.index > 0)
          #(",")
        #end
        '#(y.batchno)'
      #end
    )
  #end
  #if(map.service_status != null && !"".equals(map.service_status))
     AND service_status in(
        #for(y : map.service_status)
          #if(for.index > 0)
            #(",")
          #end
          #(y)
        #end
      )
  #end
  #if(map != null)
		  #for(x : map)
          #if(x.value&&x.value!="")
            #if(!"batchnos".equals(x.key) && !"service_status".equals(x.key))
              AND
            #end
            #if("pay_account_name".equals(x.key) ||"pay_account_no".equals(x.key) || "recv_account_name".equals(x.key) ||"recv_account_no".equals(x.key))
              #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("min".equals(x.key))
              total_amount >= #para(x.value)
            #elseif("max".equals(x.key))
              total_amount <= #para(x.value)
            #elseif("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_on,110)) <= 0
            #elseif("batchnos".equals(x.key))
              #continue
            #elseif("service_status".equals(x.key))
              #continue
            #else
              #(x.key) = '#(x.value)'
            #end
          #end
      #end
    #end
#end

#sql("findBatchAttachDetailByBatchnoAndPayStatus")
SELECT
	detail_id,
	batchno,
	info_id,
	recv_account_id,
	recv_account_no,
	recv_account_name,
	recv_account_cur,
	recv_account_bank,
	recv_bank_cnaps,
	recv_bank_prov,
	recv_bank_city,
	payment_amount,
	pay_status,
	memo,
	update_on,
	update_by,
	feed_back
FROM
	outer_batchpay_bus_attach_detail
WHERE 1=1
  #if(map != null)
    #for (x : map)
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
#end