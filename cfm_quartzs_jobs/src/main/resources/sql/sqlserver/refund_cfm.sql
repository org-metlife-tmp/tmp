#namespace("refund_cfm")
  #sql("get_histrans_group")
  select recv.* from(
    select acc_id,amount,min(convert(varchar(30), trans_date)+' '+convert(varchar(30), trans_time)) as datetime from acc_his_transaction
      where direction = 1 and is_refund_scan = 0 and trans_date >= ? and trans_date < ? group by acc_id,amount
      HAVING count(1) >= 1)pay,(
    select acc_id,amount,max(convert(varchar(30), trans_date)+' '+convert(varchar(30), trans_time)) as datetime from acc_his_transaction
      where direction = 2 and is_refund_scan = 0 and trans_date >= ? and trans_date < ? group by acc_id,amount
      HAVING count(1) >= 1)recv
  where pay.acc_id = recv.acc_id
  and pay.amount = recv.amount
  and recv.datetime > pay.datetime
  #end

  #sql("get_histrans_detail")
	SELECT
	  *,CONCAT(trans_date,' ',trans_time) as transTime
	FROM
	  acc_his_transaction
	WHERE 
	  acc_id = ? AND amount = ?  AND direction = ? AND trans_date >= ? AND trans_date < ? AND is_refund_scan = 0
  #end
#end