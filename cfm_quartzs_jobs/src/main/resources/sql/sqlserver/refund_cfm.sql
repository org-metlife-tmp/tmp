#namespace("refund_cfm")
  #sql("get_histrans_group")
	SELECT
	  acc_id,
	  amount,
	  direction
	FROM
	  acc_his_transaction
	WHERE 
	  trans_date >= ? AND trans_date <= ?  AND direction = ? AND is_refund_scan = 0
	GROUP BY
	  acc_id,amount,direction
	HAVING 
	  count(1) = 1
  #end
  
  #sql("get_histrans_detail")
	SELECT
	  id,
	  acc_id,
	  acc_no,
	  acc_name,
	  bank_type,
	  opp_acc_no,
	  opp_acc_name,
	  opp_acc_bank,
	  summary,
	  post_script,
	  amount,
	  direction,
	  trans_date,
	  trans_time,
	  data_source,
	  identifier,
	  CONCAT(trans_date,' ',trans_time) as transTime
	FROM
	  acc_his_transaction
	WHERE 
	  acc_id = ? AND amount = ?  AND direction = ? AND is_refund_scan = 0
  #end
  #end