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
  select * from pay_legal_data
  where source_sys = 1 and pay_code = ?
#end


#end
