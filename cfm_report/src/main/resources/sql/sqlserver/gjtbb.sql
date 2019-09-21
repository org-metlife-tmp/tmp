#sql("gjtList")
SELECT
    ce.collect_amount,
	ce.collect_status,
	ce.execute_time,
	cei.pay_account_no,
	cei.pay_account_org_name,
	cei.recv_account_bank,
	cei.recv_account_no
FROM
  collect_execute ce,collect_execute_instruction cei
WHERE  ce.id = cei.collect_execute_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&& !"[]".equals(x.value.toString()))
	  AND
        #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(ce.execute_time,8)) >= 0
        #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(ce.execute_time,8)) <= 0
			  #elseif("collect_status".equals(x.key))
              ce.collect_status = #para(x.value)
			  #elseif("pay_account_no".equals(x.key))
              cei.pay_account_no = #para(x.value)
			  #elseif("min".equals(x.key))
              ce.collect_amount >= #para(x.value)
			  #elseif("max".equals(x.key))
			        ce.collect_amount <= #para(x.value)
        #else
            1 = 1
        #end
      #end
    #end
    #end
    order by ce.id
  #end
