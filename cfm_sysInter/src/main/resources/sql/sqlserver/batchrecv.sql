#sql("findInstrTotal")
   select
   	*
   from
   	batch_recv_instr_queue_total
   where
   	bank_serial_number = ?
#end

#sql("findInstrDetail")
	select
		*
	from
		batch_recv_instr_queue_detail
	where 1=1
		#for(x:map)
	   		#if(x.value&&x.value!="")
	   			and
	   			#if("detailBankServiceNumber".equals(x.key))
	            	detail_bank_service_number = #para(x.value)
	            #elseif("packageSeq".equals(x.key))
	            	package_seq = #para(x.value)
	            #elseif("bankServiceNumber".equals(x.key))
	            	bank_serial_number = #para(x.value)
	            #end
	        #end
		#end
#end

###计算付款指令明细表,一个批次中,处于处理中的条数
#sql("countInstrDetailInHandling")
	select
		count(*)
	from
		batch_recv_instr_queue_detail
	where
		base_id = ?
		and status=3
#end

###更新支付指令汇总表
#sql("updInstrTotal")
	update
		batch_recv_instr_queue_total
	set
		status=(
			case when
				(select count(*) from batch_recv_instr_queue_detail where base_id=? and status=1)>0
			then 1
			else 2
			end
		)
	where id=?
#end

###回写原始数据
#sql("updOrginSuccLa")
	update origin
	set
		origin.tmp_status = bill.status,
		origin.tmp_err_message = bill.bank_err_msg
	from
		la_origin_recv_data origin,
		(
			select
				a.origin_id, b.status, b.bank_err_msg
			from
				recv_batch_detail a
			left join
				batch_recv_instr_queue_detail b
			on
				a.id=b.detail_id
			where
				b.base_id = ?
		) bill
	where
		origin.id = bill.origin_id
#end

###查询支付指令明细表
#sql("findInstrDetailByBaseId")
	select
		*
	from
		batch_recv_instr_queue_detail
	where base_id=?
#end

#sql("updBillTotalToFail")
	update
		recv_batch_total
	set
		success_num = 0,
		success_amount = 0,
		fail_num = total_num,
		fail_amount = total_amount,
		service_status = 6
	where id = ?
#end

#sql("updOriginFailLa")
	update origin
	set
		origin.tmp_status = 2,
		origin.tmp_err_message = '发送失败'
	from
		la_origin_recv_data origin,
		(
			select
				a.origin_id, b.status, b.bank_err_msg
			from
				recv_batch_detail a
			left join
				batch_recv_instr_queue_detail b
			on
				a.id=b.detail_id
			where
				b.base_id = ?
		) bill
	where
		origin.id = bill.origin_id
#end

#sql("findInstrTotalByDetail")
   select
   	*
   from
   		batch_recv_instr_queue_total total
   where
   		id = (
   			select
   				base_id
   			from
   				batch_recv_instr_queue_detail detail
   			where 1=1
   			#for(x:map)
		   		#if(x.value&&x.value!="")
		   			and
		   			#if("detailBankServiceNumber".equals(x.key))
		            	detail.detail_bank_service_number = #para(x.value)
		            #elseif("packageSeq".equals(x.key))
		            	detail.package_seq = #para(x.value)
		            #end
		        #end
    		#end
   		)
#end



#sql("selIntrDetailByAcc")
   select
   	*
   from
   	batch_recv_instr_queue_detail
   where
   	base_id = ? and
   	pay_account_no = ? and
   	amount = ?
#end

#sql("qryIntrTotalByBSN")
	select 
		*
	from
		batch_recv_instr_queue_total
	where
		bank_serial_number = ?
#end
