#sql("getHandleRouteSettingList")
  SELECT
    id,
    source_code,
    pay_recv_mode,
    pay_item,
    is_activate,
    memo,
    org_exp,
    biz_type_exp,
    insurance_type_exp
  FROM
    handle_route_setting
  where 1=1
    #for(x:cond)
      #if(x.value&&x.value!="")
        and
          #if("org_exp".equals(x.key) || "biz_type_exp".equals(x.key) || "insurance_type_exp".equals(x.key))
            #(x.key) like '%#(x.value)%'
          #else
            #(x.key) = '#(x.value)'
          #end
      #end
    #end
#end