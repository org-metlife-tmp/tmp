###收付费管理
#namespace("channel_setting")
  #include("channel_setting.sql")
#end
#namespace("bankkey_setting")
  #include("bankkey_setting.sql")
#end

#namespace("disk_downloading")
  #include("disk_downloading.sql")
#end

#namespace("disk_backing")
  #include("disk_backing.sql")
#end

#namespace("paycheck")
  #include("paycheck.sql")
#end

#namespace("recvcheck")
  #include("recvcheck.sql")
#end

#namespace("doubtful")
  #include("doubtful.sql")
#end

#namespace("recvdoubtful")
  #include("recvdoubtful.sql")
#end

#namespace("check_batch")
  #include("check_batch.sql")
#end

#namespace("except")
  #include("except.sql")
#end

#namespace("recvexcept")
  #include("recvexcept.sql")
#end

#namespace("recv_check_batch")
  #include("recv_check_batch.sql")
#end

#namespace("recv_disk_downloading")
  #include("recv_disk_downloading.sql")
#end

#namespace("recv_disk_backing")
  #include("recv_disk_backing.sql")
#end

#namespace("pay_counter")
  #include("pay_counter.sql")
#end

#namespace("paycountercheck")
  #include("paycountercheck.sql")
#end

#namespace("voucher")
  #include("voucher.sql")
#end

#namespace("recv_counter")
  #include("recv_counter.sql")
#end

#namespace("recv_group_counter")
  #include("recv_group_counter.sql")
#end

#namespace("recvcountercheck")
  #include("recvcountercheck.sql")
#end

#namespace("recv_counter_pos_import")
  #include("recv_counter_pos_import.sql")
#end

#namespace("recv_counter_pos_trans_check")
  #include("recv_counter_pos_trans_check.sql")
#end

#namespace("recv_counter_wait")
  #include("recv_counter_wait.sql")
#end




