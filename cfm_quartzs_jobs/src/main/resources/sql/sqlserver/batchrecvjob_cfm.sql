#namespace("batchrecvjob")

#sql("getTradeResultBatchQrySourceList")
	select 
		* 
	from 
		batch_recv_instr_queue_total
	where 
		status = 1 and reqnbr <> null and reqnbr <> ''
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

#end