###自动归集设置
#namespace("collect_setting")
  #include("collect_setting.sql")
#end

###自动归集管理
#namespace("collect_manage")
  #include("collect_manage.sql")
#end

###自动归集查看
#namespace("collect_view")
  #include("collect_view.sql")
#end

###自动归集图表
#namespace("collect_report")
  #include("collect_report.sql")
#end

###交易核对
#namespace("collect_check")
  #include("collect_check.sql")
#end

###非直连归集
#namespace("collect_ndc")
  #include("ndc.sql")
#end

###非直连归集交易核对
#namespace("collect_batch_check")
  #include("collect_batch_check.sql")
#end
