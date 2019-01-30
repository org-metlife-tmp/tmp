###对账初始化设置
#namespace("dztinit")
  #include("dzt_initsetting.sql")
#end

###对账管理
#namespace("dztmgt")
  #include("dzt_mgt.sql")
#end

###余额调节表
#namespace("dztadjust")
  #include("dzt_adjust.sql")
#end

###已勾兑查询
#namespace("dztcheck")
  #include("dzt_check.sql")
#end
