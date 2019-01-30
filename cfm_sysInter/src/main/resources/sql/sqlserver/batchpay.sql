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

###更新支付指令汇总表
#sql("updInstrTotal")
	update
		batch_pay_instr_queue_total
	set
		status=(
			case when 
				(select count(*) from batch_pay_instr_queue_detail where base_id=? and status=1)>0 
			then 1 
			else 2 
			end
		)
	where id=?
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
#sql("updOrginLa")
	update origin
	set
		origin.tmp_status = detail.status,
		origin.tmp_err_message = detail.bank_err_msg
	from
		#(tb) origin
	left join
		batch_pay_instr_queue_detail detail
		on origin.id = detail.origin_id
	where detail.base_id = ?
#end

#sql("updBillTotalToFail")
	update
		pay_batch_total
	set
		success_num = 0,
		success_amount = 0,
		fail_num = total_num,
		fail_amount = total_amount
	where id = ?
#end

#sql("updOriginFail")
	update origin
	set
		origin.tmp_status = 2,
		origin.tmp_err_message = '发送失败'
	from
		#(tb) origin
	left join
		pay_legal_data legal
		on origin.id = legal.origin_id
	left join
		pay_batch_detail bill
		on legal.id = bill.legal_id
	left join
		batch_pay_instr_queue_detail detail
		on bill.id = detail.detail_id
	where detail.base_id = ?
#end