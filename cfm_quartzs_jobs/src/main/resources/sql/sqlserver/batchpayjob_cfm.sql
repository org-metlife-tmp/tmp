#namespace("batchpayjob")

#sql("getTradeResultBatchQrySourceList")
	select * from 
		(
			select top (?) 
				id,
				bank_serial_number,
				source_ref,
				bill_id,
				pay_account_no,
				pay_account_name,
				pay_account_cur,
				pay_account_bank,
				pay_bank_cnaps,
				pay_bank_cnaps as bank_cnaps_code,
				pay_bank_prov,
				pay_bank_city,
				pay_bank_type,
				trade_date 
			from 
				batch_pay_instr_queue_total 
			where status=1 and reqnbr is null
		) t
		join 
		(
			select
				id as pay_detail_id,
				base_id,
				detail_id,
				detail_bank_service_number,
				package_seq,
				amount,
				recv_account_no,
				recv_account_name,
				recv_account_cur,
				recv_account_bank,
				recv_bank_cnaps,
				recv_bank_prov,
				recv_bank_city,
				recv_bank_type,
				is_cross_bank
			from
				batch_pay_instr_queue_detail 
			where status=3
		)d
		on t.id=d.base_id
	order by t.id
#end

#sql("getTradeResultBatchQrySourceListNew")
	select 
		id,reqnbr,bank_serial_number,source_ref,bill_id,pay_bank_type,trade_date,
		pay_bank_cnaps as bank_cnaps_code
	from 
		batch_pay_instr_queue_total
	where 
		status = 1 and 
		reqsta = 1 and
		reqnbr is not null and
		reqnbr <> ''
#end

#sql('getOldBatchTrade')
	select
		*
	from
		trade_result_query_batch_instr_queue_lock
	where
		bank_serial_number = ?
		and detail_bank_service_number = ?
		and package_seq = ?
#end

#sql('getNeedQueryStatusBatchPay')
	select
		top 1 
		id,
		1 as biz_type,
		pay_bank_cnaps as bank_cnaps_code,
		trade_date,
		trade_date as begin_date,
		trade_date as end_date,
		pay_bank_type as process_bank_type
	from
		batch_pay_instr_queue_total
	where
		status = 1 and
		reqsta = 0 and
		rtnflg in (0, 9) and 
		pay_bank_cnaps like '308%'
#end


#end