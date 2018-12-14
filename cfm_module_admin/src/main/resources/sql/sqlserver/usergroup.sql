#sql("modules")
SELECT
  [code],
  [name],
  [description],
  [isAdmin],
  [display_order]
FROM
  modules
  #if(isAdmin??)
    WHERE isAdmin = #para(isAdmin)
  #end
 ORDER BY display_order
#end

#sql("menus")
SELECT
  [code],
  [name],
  [module_code],
  [displayOrder]
FROM menus
  #if(mc??)
    WHERE module_code = #para(mc)
  #end
 order by displayOrder
#end

#sql("usergroup")
SELECT
  [group_id],
  [name],
  [memo],
  [is_builtin]
FROM
  user_group
  where 1=1
  #if(map??)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("name".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by group_id
#end

#sql("usergroupMenus")
SELECT
  menu_code
FROM
  user_group_with_menu
WHERE group_id = #para(0)
#end


#sql("uodp_ids")
SELECT
  uodp_id
FROM user_group_with_user
WHERE group_id = ?
#end