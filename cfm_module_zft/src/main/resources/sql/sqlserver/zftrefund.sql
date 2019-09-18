#sql("findZftRefundList")
SELECT
	aht.id,
	aht.acc_id,
	aht.acc_no,
	aht.acc_name,
	aht.bank_type,
	(SELECT name FROM all_bank_info abi WHERE abi.cnaps_code = acc.bank_cnaps_code)as bank_type_name,
	aht.direction,
	aht.amount,
	aht.opp_acc_no,
	aht.opp_acc_name,
	aht.opp_acc_bank,
	aht.summary,
	aht.post_script,
	aht.trans_date,
	aht.trans_time,
	aht.data_source,
	aht.identifier,
	aht.import_time,
	aht.is_checked,
	aht.is_refund_scan,
	aht.checked_ref,
	aht.ref_bill_id,
	aht.refund_ref,
	aht.refund_bill_id,
	aht.instruct_code,
	aht.statement_code,
	aht.business_check,
	aht.check_service_number,
	aht.check_user_id,
	aht.check_user_name,
	aht.check_date
FROM
	acc_his_transaction aht
LEFT JOIN account acc ON(aht.acc_id = acc.acc_id)
WHERE 1=1
  #for(x : map)
    #if(x.value&&x.value!="")
      AND
      #if("acc_no".equals(x.key) || "opp_acc_no".equals(x.key))
        aht.#(x.key) like '%#(x.value)%'
      #elseif("start_trans_date".equals(x.key))
        DATEDIFF(day,#para(x.value),aht.trans_date) >= 0
      #elseif("end_trans_date".equals(x.key))
        DATEDIFF(day,#para(x.value),aht.trans_date) <= 0
      #elseif("min".equals(x.key))
        aht.amount >= #para(x.value)
      #elseif("max".equals(x.key))
        aht.amount <= #para(x.value)
      #elseif("checked_ref".equals(x.key))
          aht.checked_ref in(
            #for(y : map.checked_ref)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
      #else
        aht.#(x.key) = '#(x.value)'
      #end
    #end
  #end
#end