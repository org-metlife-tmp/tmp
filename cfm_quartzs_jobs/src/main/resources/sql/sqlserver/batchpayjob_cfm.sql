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
			where status=1
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

#end