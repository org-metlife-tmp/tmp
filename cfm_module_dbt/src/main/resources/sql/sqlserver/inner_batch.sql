#sql("findTempBatchBaseInfo")
  SELECT
	uuid,
	usr_id,
	total_num,
	total_amount
FROM
	inner_batchpay_baseinfo_temp
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

#sql("getCanBatchnoByUser")
SELECT
	DISTINCT ibb.batchno
FROM
	inner_batchpay_baseinfo AS ibb
LEFT OUTER JOIN inner_batchpay_bus_attach_detail AS ibbat ON
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
	inner_batchpay_baseinfo AS ibb
LEFT OUTER JOIN inner_batchpay_bus_attach_detail AS ibbat ON
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
		inner_batchpay_baseinfo ibb
	left join inner_batchpay_bus_attach_detail ibbat on
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

#sql("getBatchBaseInfoByBatchNoPayList")
SELECT
	fin.id,
	fin.batchno,
	fin.total_num,
	fin.pay_account_no,
	fin.pay_account_name,
	fin.pay_account_bank,
	fin.pay_bank_cnaps,
	fin.total_amount,
	fin.pending_num,
	fin.pending_amount,
	fin.version as persist_version,
	fin.service_status,
	fin.create_by,
	fin.apply_on,
	fin.update_by,
	fin.update_on
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
		count(ibbat.batchno) as pending_num,
		ISNULL( SUM(ibbat.payment_amount), 0 ) AS pending_amount ,
		ibb.persist_version as version ,
		ibb.service_status,
		ibb.create_by,
		ibb.apply_on,
		ibb.update_by,
		ibb.update_on
	FROM
		inner_batchpay_baseinfo ibb
	left join inner_batchpay_bus_attach_detail ibbat on
		( ibb.batchno = ibbat.batchno )
		and ibbat.pay_status in(0,2)
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
		ibb.update_on
	) fin
	ORDER by fin.service_status asc, fin.id desc
#end

#sql("insertBatchAttachInfo")
insert into inner_batchpay_bus_attach_info (
	id,
	batchno,
	origin_name,
	file_extension_suffix,
	number,
	amount
)
(
select
	id,
	batchno,
	origin_name,
	file_extension_suffix,
	number,
	amount
from
	inner_batchpay_bus_attach_info_temp
where
	batchno = ?
AND	uuid = ?
and [status] = ?
)
#end

#sql("insertBatchAttachDetail")
insert into inner_batchpay_bus_attach_detail (
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
	memo
	)
	(
	select
		detail.batchno,
		detail.info_id,
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
		detail.memo
	from
		inner_batchpay_bus_attach_detail_temp detail ,
		inner_batchpay_bus_attach_info_temp info
	WHERE
		info.id = detail.info_id
		AND detail.batchno = ?
		and [status] = ?
	)
#end

#sql("insertBatchAttachInfoTemp")
insert into inner_batchpay_bus_attach_info_temp (
	id,
	batchno,
	uuid,
	origin_name,
	file_extension_suffix,
	number,
	amount,
	status
)
(
select
	id,
	batchno,
	? as uuid,
	origin_name,
	file_extension_suffix,
	number,
	amount,
	? as status
from
	inner_batchpay_bus_attach_info
where
	batchno = ?
)
#end

#sql("insertBatchAttachDetailTemp")
insert into inner_batchpay_bus_attach_detail_temp (
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
	memo
	)
	(
	select
		detail.batchno,
		detail.info_id,
		detail.recv_account_id,
		detail.recv_account_no,
		detail.recv_account_name,
		detail.recv_account_bank,
		detail.recv_account_cur,
		detail.recv_bank_cnaps,
		detail.recv_bank_prov,
		detail.recv_bank_city,
		detail.payment_amount,
		detail.pay_status,
		detail.memo
	from
		inner_batchpay_bus_attach_detail detail
	WHERE
		detail.batchno = ?
	)
#end

#sql("updateTempAttachInfoByStatus")
  update inner_batchpay_bus_attach_info_temp
  set status = ?
  where status = ?
  and uuid = ?
  and batchno = ?
#end

#sql("updateTempBaseInfoBybatchUuid")
  update inner_batchpay_baseinfo_temp
  set total_num = ?,
  total_amount = ?,
  usr_id = ?
  where  uuid = ?
  and batchno = ?
#end

#sql("delBaseInfoTemp")
delete from inner_batchpay_baseinfo_temp
where batchno = ?
-- and uuid = ?
#end

#sql("delAttachmentInfoTemp")
delete from inner_batchpay_bus_attach_info_temp
where batchno = ?
-- and uuid = ?
#end

#sql("delAttachmentDetailTemp")
delete from inner_batchpay_bus_attach_detail_temp
where batchno = ?
#end

#sql("findAttachInfoByBatchno")
SELECT
	id,
	batchno,
	origin_name,
	file_extension_suffix,
	[number],
	amount
FROM
	inner_batchpay_bus_attach_info
WHERE
	batchno = ?
#end

#sql("findAttachDetalByIDBatchno")
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
	inner_batchpay_bus_attach_detail
WHERE info_id = ?
and batchno = ?
#end

#sql("findAttachInfoByIDBatchno")
SELECT
	[number] as total_num,
	amount as total_amount
FROM
	inner_batchpay_bus_attach_info d
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

#sql("delAttachmentInfo")
delete from inner_batchpay_bus_attach_info
where batchno = ?
#end

#sql("delAttachDetalTempByInfoid")
delete from inner_batchpay_bus_attach_detail_temp
where info_id = ?
#end

#sql("delAttachmentDetail")
delete from inner_batchpay_bus_attach_detail
where batchno = ?
#end

#sql("getBatchViewBillByBatchno")
SELECT
	obb.id ,
	obb.batchno ,
	obb.pay_account_no,
	obb.pay_account_name,
	obb.pay_account_bank,
	obb.pay_bank_cnaps,
	obb.total_amount,
	obb.total_num,
	obb.persist_version as persist_version,
	obb.service_status as service_status ,
	obb.update_on as process_date,
	obb.payment_summary,
	obb.org_id,
	o.name as org_name,
	obb.biz_id,
	obb.biz_name,
	obb.pay_mode,
	sum( case when obbad.pay_status = 0 then obbad.payment_amount else 0.00 end ) saved_amount,
	sum( case when obbad.pay_status = 0 then 1 else 0 end ) saved_num,
	sum( case when obbad.pay_status = 2 then obbad.payment_amount else 0.00 end ) failed_amount,
	sum( case when obbad.pay_status = 2 then 1 else 0 end ) failed_num,
	sum( case when obbad.pay_status = 1 then obbad.payment_amount else 0.00 end ) success_amount,
	sum( case when obbad.pay_status = 1 then 1 else 0 end ) success_num,
	sum( case when obbad.pay_status = 4 then obbad.payment_amount else 0.00 end ) cancel_amount,
	sum( case when obbad.pay_status = 4 then 1 else 0 end ) cancel_num,
	sum( case when obbad.pay_status = 3 then obbad.payment_amount else 0.00 end ) process_amount,
	sum( case when obbad.pay_status = 3 then 1 else 0 end ) process_num
from
	inner_batchpay_baseinfo obb,
	inner_batchpay_bus_attach_detail obbad,
	organization o
where
	obb.batchno = obbad.batchno
	and obb.org_id = o.org_id
	and obb.batchno = ?
group by
	obb.id ,
	obb.batchno,
	obb.pay_account_no,
	obb.pay_account_name,
	obb.pay_account_bank,
	obb.pay_bank_cnaps,
	obb.total_amount,
	obb.total_amount,
	obb.total_num,
	obb.persist_version,
	obb.service_status,
	obb.update_on,
	obb.payment_summary,
	obb.org_id ,
	o.name,
	obb.biz_id,
	obb.biz_name,
	obb.pay_mode
#end

#sql("findAttachDetailToPage")
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
	inner_batchpay_bus_attach_detail
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("recv_account_name".equals(x.key) || "recv_account_no".equals(x.key) || "pay_account_name".equals(x.key) || "pay_account_no".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),'#(x.value)')+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            payment_amount >= #(x.value)
          #elseif("max".equals(x.key))
            payment_amount <= #(x.value)
          #elseif("pay_status".equals(x.key))
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

#sql("findAttachDetailTotal")
SELECT
	sum( d.payment_amount ) as total_amount,
	count(0) as total_num
FROM
	inner_batchpay_bus_attach_detail d
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("recv_account_name".equals(x.key) || "recv_account_no".equals(x.key) || "pay_account_name".equals(x.key) || "pay_account_no".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),'#(x.value)')+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            payment_amount >= #(x.value)
          #elseif("max".equals(x.key))
            payment_amount <= #(x.value)
          #else
            #(x.key) = '#(x.value)'
          #end
        #end
    #end
  #end
#end

#sql("findInnerBatchBaseInfoPendingList")
SELECT
	ibb.id as ibb_id,
	ibb.org_id,
	ibb.dept_id,
	ibb.biz_id,
	ibb.biz_name,
	ibb.pay_account_id,
	ibb.pay_account_no,
	ibb.pay_account_name,
	ibb.pay_account_cur,
	ibb.pay_account_bank,
	ibb.pay_bank_cnaps,
	ibb.pay_bank_prov,
	ibb.pay_bank_city,
	ibb.process_bank_type,
	ibb.total_num,
	ibb.total_amount,
	ibb.delete_flag,
	ibb.create_by,
	ibb.create_on,
	ibb.update_by,
	ibb.update_on,
	ibb.persist_version,
	ibb.batchno,
	ibb.payment_summary,
	ibb.service_status,
	ibb.attachment_count,
	ibb.pay_mode,
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
	inner_batchpay_baseinfo ibb,cfm_workflow_run_execute_inst cwrei
WHERE ibb.id = cwrei.bill_id
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
  order by ibb_id
#end

#sql("findBaseInfoByIdAndVersion")
SELECT
	id,
	org_id,
	dept_id,
	biz_id,
	biz_name,
	pay_account_id,
	pay_account_no,
	pay_account_name,
	pay_account_cur,
	pay_account_bank,
	pay_bank_cnaps,
	pay_bank_prov,
	pay_bank_city,
	process_bank_type,
	total_num,
	total_amount,
	delete_flag,
	create_by,
	create_on,
	update_by,
	update_on,
	persist_version,
	batchno,
	payment_summary,
	service_status,
	attachment_count,
	pay_mode
FROM
	inner_batchpay_baseinfo
where id = ?
and persist_version = ?
#end

#sql("fingAttachInfoTemp")
SELECT
	id,
	uuid,
	batchno,
	origin_name,
	file_extension_suffix,
	[number],
	amount,
	status
FROM
	inner_batchpay_bus_attach_info_temp
WHERE
	id = ?
AND uuid = ?
AND batchno = ?
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
	inner_batchpay_bus_attach_detail
where 1=1
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
	inner_batchpay_baseinfo base ,
	inner_batchpay_bus_attach_detail detail
where
	base.batchno = detail.batchno
	and detail.detail_id = ?;
#end

#sql("updateDetailById")
   update inner_batchpay_bus_attach_detail
   set bank_serial_number = ?,repeat_count = ?,pay_status = ?,instruct_code = ? 
    where detail_id = ? and repeat_count = ? and pay_status = ?
#end

#sql("findmorelisttotal")
SELECT DISTINCT
	sum(detail.payment_amount) as total_amount,
	(SELECT DISTINCT sum(payment_amount) FROM inner_batchpay_bus_attach_detail detail,inner_batchpay_baseinfo base
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
	inner_batchpay_baseinfo base,inner_batchpay_bus_attach_detail detail
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
