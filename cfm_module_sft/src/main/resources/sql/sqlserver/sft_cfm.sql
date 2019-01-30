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

#namespace("doubtful")
  #include("doubtful.sql")
#end

#namespace("check_batch")
  #include("check_batch.sql")
#end

#namespace("except")
  #include("except.sql")
#end