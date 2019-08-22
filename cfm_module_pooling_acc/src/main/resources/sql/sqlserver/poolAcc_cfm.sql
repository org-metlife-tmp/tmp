###资金池账户设置
#namespace("poolAcc")
  #include("poolAcc.sql")
#end
###资金池账户设置end

###资金下拨账户核对
#namespace("poolTrad")
  #include("poolTrad.sql")
#end
###资金下拨账户核对end

###资金下拨设置
#namespace("allocation")
  #include("allocation.sql")
#end
###资金下拨设置end

###资金下拨报表
#namespace("allorep")
  #include("allorep.sql")
#end
###资金下拨报表end