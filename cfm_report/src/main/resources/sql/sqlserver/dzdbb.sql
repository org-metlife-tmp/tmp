#sql("dzdList")
SELECT
  dzd.acc_no,
  dzd.acc_name,
	dzd.trans_time,
	dzd.ref_bill_id,
	dzd.bank_type,
	dzd.opp_acc_no,
	dzd.opp_acc_bank,
	dzd.amount,
	dzd.summary,
	dzd.statement_code,
	dzd.is_checked,
	dzd.check_user_name
FROM
  acc_his_transaction dzd
WHERE
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&& !"[]".equals(x.value.toString()))
	  AND
        #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(ce.execute_time,8)) >= 0
        #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(ce.execute_time,8)) <= 0
			  #elseif("acc_name ".equals(x.key))
             dzd.acc_name = #para(x.value)
        #else
            1 = 1
        #end
      #end
    #end
    #end
    order by dzd.id
  #end
