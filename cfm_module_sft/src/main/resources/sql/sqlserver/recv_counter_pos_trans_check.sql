#sql("findBillList")
SELECT
	*,
	trade_date trans_date
FROM
	recv_counter_pos
WHERE
	delete_flag = 0
	and id in (
    #for(x : batchNo)
      #(for.index == 0 ? "" : ",") #para(x)
    #end
	)
#end