#namespace("webservice_ebs_cfm")
  #sql("updateCallbackingStatus")
  update ebs_origin_pay_data set ebs_callback_status = ?, ebs_callback_send_time = ?, persist_version = persist_version + 1
  where id = ? and ebs_callback_status in(?,?) and persist_version = ?
#end

#sql("getStatusByPayCode")
  select tmp_status from ebs_origin_pay_data
  where pay_code = ?
#end

#sql("getPayLegalByPayCode")
  SELECT
    legal.*,
    total.back_on
  FROM
    pay_batch_total total,
    pay_batch_detail detail,
    pay_legal_data legal
  WHERE
    legal.org_id = detail.origin_id
    AND detail.base_id = total.id
    AND legal.source_sys = 1
    AND legal.pay_code = ?
#end


#end
