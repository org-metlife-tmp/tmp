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

#sql('getBatchRecvDetail')
	select
		*
	from
		batch_recv_instr_queue_detail
	where
		bank_serial_number_unpack = ?
#end

#sql('getBatchRecvDetailNo')
	select
		*
	from
		batch_recv_instr_queue_detail
	where
		bank_serial_number = ?
#end

#sql('getBankunPack')
	select
		distinct (bank_serial_number_unpack)
	from
		batch_recv_instr_queue_detail
	where
		bank_serial_number = ? and status=3 order by bank_serial_number_unpack
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
		rtnflg in (0, 9) and
		recv_bank_cnaps like '308%'
#end

#sql('queryProtocolTotal')
	select 
		bank_seriral_no,
		cnaps as bank_cnaps_code 
	from 
		protocol_import_instr_total
	where
		status in ('0','3')
#end

#sql('queryOldProtocolImportQueryLock')
	select 
		* 
	from 
		protocol_import_instr_query_lock
	where
		bank_seriral_no = ? and
		channel_code = ?
#end

#sql("getTradeResultBatchQry")
	select
		id,reqnbr,bank_serial_number,source_ref,bill_id,recv_bank_type,trade_date,total_num,total_amount,recv_account_no,
		recv_bank_cnaps as bank_cnaps_code
	from
		batch_recv_instr_queue_total
	where
		bank_serial_number = ?
#end

#sql("findInstrTotal")
   select
   	*
   from
   	batch_recv_instr_queue_total
   where
   	bank_serial_number = ?
#end

#sql("qryChannel")
   select r.channel_id from recv_batch_total_master r where r.recv_acc_no = ?
#end

#sql("qryChannelSet")
   select c.direct_channel shortPayCnaps from channel_setting c where c.id = ?
#end

#end