#namespace("currency")
  #include("currency.sql")
#end

#namespace("org")
  #include("organization.sql");
#end

#namespace("department")
  #include("department.sql")
#end

#namespace("user")
  #include("user.sql")
#end

#namespace("merchant")
  #include("merchant_acc_info.sql")
#end

#namespace("channel")
  #include("handle_channel_setting.sql");
#end

#namespace("settle")
  #include("settle_account_info.sql");
#end

#namespace("usergroup")
  #include("usergroup.sql")
#end

#namespace("route")
  #include("handle_route_setting.sql")
#end

#namespace("routeitem")
  #include("handle_route_setting_item.sql")
#end

#namespace("position")
  #include("position.sql")
#end

#namespace("biztype")
  #include("biztype.sql")
#end
