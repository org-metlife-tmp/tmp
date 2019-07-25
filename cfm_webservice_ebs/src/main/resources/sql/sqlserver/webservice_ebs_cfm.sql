#namespace("webservice_ebs_cfm")
  #sql("updateCallbackingStatus")
  update ebs_origin_pay_data set ebs_callback_status = ?, ebs_callback_send_time = ?, persist_version = persist_version + 1
  where id = ? and ebs_callback_status in(?,?) and persist_version = ?
#end

#sql("getStatusByPayCode")
  select tmp_status,pay_mode from ebs_origin_pay_data
  where pay_code = ?
#end

#sql("getPayLegalByPayCode")
  SELECT
    legal.*,
    ext.insure_bill_no,
    detail.id detailId,
    origin.ebs_callback_resp_time resp_time,
    total.back_on
  FROM
    pay_batch_total total,
    pay_batch_detail detail,
    pay_legal_data legal,
    ebs_pay_legal_data_ext ext,
    ebs_origin_pay_data origin
  WHERE
    legal.id = detail.legal_id
    AND legal.id = ext.legal_id
    AND origin.id = legal.origin_id
    AND detail.base_id = total.id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
#end

#sql("getPayGmLegalByPayCode")
  SELECT
    legal.*,
		ext.insure_bill_no,
		gmf.id detailId,
		gmf.actual_payment_date,
    gmf.pay_account_no
  FROM
    pay_legal_data legal,
		ebs_pay_legal_data_ext ext,
    gmf_bill gmf
  WHERE
    legal.id = gmf.legal_id
		AND legal.id = ext.legal_id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
#end


#end
