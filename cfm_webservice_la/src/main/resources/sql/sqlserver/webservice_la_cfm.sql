#namespace("webservice_la_cfm")
 #sql("updateCallbackingStatus")
  update la_origin_pay_data set la_callback_status = ?, la_callback_send_time = ?, persist_version = persist_version + 1
  where id = ? and la_callback_status in(?,?) and persist_version = ?
#end

#sql("getStatusByPayCode")
  select tmp_status from la_origin_pay_data
  where pay_code = ?
#end

#sql("getPayLegalByPayCode")
  select org_code,amount from pay_legal_data
  where source_sys = 0 and pay_code = ?
#end

#end
