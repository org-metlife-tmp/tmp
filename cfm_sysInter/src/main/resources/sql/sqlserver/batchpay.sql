###通过：bank_service_number查询付款指令汇总信息表
#sql("findInstrTotal")
   select 
   	* 
   from 
   	batch_pay_instr_queue_total
   where 
   	bank_serial_number = ?
#end

#sql("findInstrTotalByDetail")
   select 
   	* 
   from 
   		batch_pay_instr_queue_total total
   where
   		id = (
   			select
   				base_id
   			from
   				batch_pay_instr_queue_detail detail
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

#sql("findInstrDetail")
	select
		*
	from
		batch_pay_instr_queue_detail
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

###计算付款指令明细表，一个批次中，处于处理中的条数
#sql("countInstrDetailInHandling")
	select
		count(*)
	from
		batch_pay_instr_queue_detail
	where
		base_id = ?
		and status=3
#end

###查询支付指令明细表
#sql("findInstrDetailByBaseId")
	select
		*
	from
		batch_pay_instr_queue_detail
	where base_id=?
#end

###回写原始数据
#sql("updOrginSuccLa")
	update origin
	set
		origin.tmp_status = bill.status,
		origin.tmp_err_message = bill.bank_err_msg
	from
		la_origin_pay_data origin,
		(
			select 
				a.origin_id, b.status, b.bank_err_msg 
			from 
				pay_batch_detail a 
			left join 
				batch_pay_instr_queue_detail b 
			on 	
				a.id=b.detail_id 
			where 
				b.base_id = ?
		) bill
	where 
		origin.id = bill.origin_id
#end

#sql("updOrginSuccEbs")
	update origin
	set
		origin.tmp_status = bill.status,
		origin.tmp_err_message = bill.bank_err_msg,
		origin.paydate=?,
		origin.paytime=?,
		origin.paybankcode=?,
		origin.paybankaccno=?
	from
		ebs_origin_pay_data origin,
		(
			select 
				a.origin_id, b.status, b.bank_err_msg 
			from 
				pay_batch_detail a 
			left join 
				batch_pay_instr_queue_detail b 
			on 	
				a.id=b.detail_id 
			where 
				b.base_id = ?
		) bill
	where 
		origin.id = bill.origin_id
#end

#sql("updBillTotalToFail")
	update
		pay_batch_total
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
		origin.tmp_err_message = bill.bank_err_msg
	from
		la_origin_pay_data origin,
		(
			select 
				a.origin_id, b.status, b.bank_err_msg 
			from 
				pay_batch_detail a 
			left join 
				batch_pay_instr_queue_detail b 
			on 	
				a.id=b.detail_id 
			where 
				b.base_id = ?
		) bill
	where 
		origin.id = bill.origin_id
#end
#sql("updOriginFailEbs")
	update origin
	set
		origin.tmp_status = 2,
		origin.tmp_err_message = '发送失败',
		origin.paydate=?,
		origin.paytime=?,
		origin.paybankcode=?,
		origin.paybankaccno=?
	from
		ebs_origin_pay_data origin,
		(
			select 
				a.origin_id, b.status, b.bank_err_msg 
			from 
				pay_batch_detail a 
			left join 
				batch_pay_instr_queue_detail b 
			on 	
				a.id=b.detail_id 
			where 
				b.base_id = ?
		) bill
	where 
		origin.id = bill.origin_id
#end

#sql("qryIntrTotalByBSN")
	select 
		*
	from
		batch_pay_instr_queue_total
	where
		bank_serial_number = ? and
		status = 1 and
		reqsta = 0
#end

#sql("selIntrDetailByAcc")
   select
   	*
   from
   	batch_pay_instr_queue_detail
   where
   	base_id = ? and
   	recv_account_no = ? and
   	amount = ?
#end