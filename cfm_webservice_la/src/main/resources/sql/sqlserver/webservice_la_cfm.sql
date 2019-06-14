#namespace("webservice_la_cfm")
 #sql("updateCallbackingStatus")
  update la_origin_pay_data set la_callback_status = ?, la_callback_send_time = ?, persist_version = persist_version + 1
  where id = ? and la_callback_status in(?,?) and persist_version = ?
#end

#sql("getStatusByPayCode")
  select tmp_status,pay_mode from la_origin_pay_data
  where pay_code = ?
#end

#sql("getPayLegalByPayCode")
  SELECT
    legal.*,
		ext.insure_bill_no,
		detail.id detailId,
    total.back_on
  FROM
    pay_batch_total total,
    pay_batch_detail detail,
    pay_legal_data legal,
		la_pay_legal_data_ext ext
  WHERE
    legal.id = detail.legal_id
    AND detail.base_id = total.id
		AND legal.id = ext.legal_id
    AND legal.source_sys = 0
    AND legal.pay_code = ?
#end

#sql("getPayGmLegalByPayCode")
  SELECT
    legal.*,
		ext.insure_bill_no,
		gmf.id detailId,
    gmf.pay_account_no
  FROM
    pay_legal_data legal,
		la_pay_legal_data_ext ext,
    gmf_bill gmf
  WHERE
    legal.id = gmf.legal_id
		AND legal.id = ext.legal_id
    AND legal.source_sys = 0
		AND legal.pay_code = '000946222'
#end

#end
