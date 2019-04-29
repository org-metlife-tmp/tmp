#namespace("batchrecvjob")

#sql("getTradeResultBatchQrySourceList")
	select 
		id,reqnbr,bank_serial_number,source_ref,bill_id,recv_bank_type,trade_date,
		recv_bank_cnaps as bank_cnaps_code
	from 
		batch_recv_instr_queue_total
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
		batch_recv_instr_queue_lock
	where
		bank_serial_number = ?
		and reqnbr = ?
#end


#sql('getNeedQueryStatusBatchRecv')
	select
		top 1 
		id,
		2 as biz_type,
		recv_bank_cnaps as bank_cnaps_code,
		trade_date,
		trade_date as begin_date,
		trade_date as end_date,
		recv_bank_type as process_bank_type
	from
		batch_recv_instr_queue_total
	where
		status = 1 and
		reqsta = 0 and
		rtnflg in (0, 9)
#end

#end