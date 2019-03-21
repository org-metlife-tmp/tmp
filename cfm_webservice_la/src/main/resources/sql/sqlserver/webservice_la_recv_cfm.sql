#namespace("webservice_la_recv_cfm")

	#sql("updateCallbackingStatus")
	  update 
	  	la_origin_recv_data 
	  set 
	  	la_callback_status = ?, la_callback_send_time = ?, persist_version = persist_version + 1
	  where 
	  	id = ? and 
	  	la_callback_status in(?,?) and 
	  	persist_version = ?
	#end

	#sql("getStatusByPayCode")
	  select 
	  	tmp_status 
	  from 
	  	la_origin_recv_data
	  where 
	  	pay_code = ?
	#end

  #sql("getPayLegalByPayCode")
    SELECT
      legal.*,
      total.back_on
    FROM
      recv_batch_total total,
      recv_batch_detail detail,
      recv_legal_data legal
    WHERE
      legal.id = detail.legal_id
      AND detail.base_id = total.id
      AND legal.source_sys = 0
      AND legal.pay_code = ?
  #end
	
	
	#sql("getBankCodeByOrgin")
		select 
			bankcode
		from 
			channel_setting
		where id in (
			select 
		        b.channel_id
		    from 
		        bankkey_setting b,
		        const_bank_type d
		    where 
		        b.bank_type=d.code and 
		        b.os_source = 0 and 
		        b.bankkey = ? and 
		        b.bankkey_status = 1 and 
				b.org_id in (
					select 
						o.org_id
					from 
						la_org_mapping m,
						organization o
				   where 
						m.tmp_org_code = o.code and 
						m.la_org_code = ? and 
						m.la_branch_code = ?
				)
		) and is_checkout = 1
	#end
	
	#sql("ddhDecryptD")
	    Exec jem ?
	#end
#end
