#namespace("no_resp_callback")

#sql("getLACallBackOriginList")
  select *
  from la_origin_recv_data
  where tmp_status in (?,?) and la_callback_status in (?) and la_callback_send_time < ?
  and callback_mail_status = 0
#end

#end
