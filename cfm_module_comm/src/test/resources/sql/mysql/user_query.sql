###用户查询接口
#sql("list")
  select
    usr.usr_id as id,
    usr.`name`
  FROM user_info usr
  JOIN `user_org_dept_pos` uodp
    ON uodp.usr_id = usr.usr_id
    AND uodp.is_default = 1
  WHERE 1=1 and usr.status = 1
  #if(map != null)
    #for(x : map)
      #if(x.value && x.value!="")
        AND
        #if("name".equals(x.key))
          usr.name like concat('%', #para(x.value), '%')
        #elseif("org_id".equals(x.key))
          uodp.org_id = #para(x.value)
        #end
      #end
    #end
  #end
  LIMIT 150
#end