#namespace("batchrecvjob")

#sql("getTradeResultBatchQrySourceList")
	select 
		* 
	from 
		batch_recv_instr_queue_total
	where 
		status = 1 and 
		reqsta = 1 and
		reqnbr <> null and 
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
		top 1 *
	from
		batch_recv_instr_queue_total
	where
		status = 1 and
		reqsta = 0 and
		rtnflg = 0
#end

#end