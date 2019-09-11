#namespace("no_resp_callback")

#sql("getLACallBackOriginList")
select *
  from la_origin_recv_data recv  LEFT JOIN la_mail_status la ON recv.pay_code=la.ref_key
  where recv.tmp_status in (?,?) and recv.la_callback_status in (?) and recv.la_callback_send_time < ?
  and la.mail_status = 0 or la.mail_status is null ;
#end

#end
