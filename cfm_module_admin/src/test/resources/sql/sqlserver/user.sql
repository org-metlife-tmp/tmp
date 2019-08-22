#sql("userPage")
SELECT
  usr.usr_id,
  usr.email,
  usr.is_admin,
  usr.is_boss,
  usr.login_name,
  usr.[name],
  usr.persist_version,
  usr.phone,
  usr.pwd_last_change_date,
  usr.register_date,
  usr.status,
  usr.is_have_extra,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name
FROM user_info usr
  JOIN user_org_dept_pos uodp
    ON usr.usr_id = uodp.usr_id
       AND uodp.is_default = 1
  JOIN organization org
    ON org.org_id = uodp.org_id
       AND org.status != 3
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
       AND dept.status != 3
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
       AND pos.status != 3
where 1=1 AND usr.status != 3
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("name".equals(x.key))
          usr.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          usr.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("checkUserLoginName")
SELECT
  count(usr_id) as counts
FROM user_info
WHERE 1=1
  #if(map != null)
    #for(x : map)
      and #(x.key) #para(x.value)
    #end
  #end
#end

#sql("userExtInfo")
SELECT
  id,
  usr_id,
  type_code,
  [value]
 FROM user_extra_info
 WHERE usr_id = ?
#end

#sql("usrUdopList")
SELECT
  ###uodp.id,
  uodp.usr_id,
  uodp.org_id,
  uodp.dept_id,
  uodp.pos_id,
  uodp.is_default#--,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name--#
FROM
  user_org_dept_pos uodp
  JOIN user_info usr
    ON usr.usr_id = uodp.usr_id
  JOIN organization org
    ON org.org_id = uodp.org_id
       AND org.status != 3
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
       AND dept.status != 3
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
       AND pos.status != 3
WHERE uodp.usr_id = ?
#end

#sql("userInfo")
SELECT top 1 *
 FROM user_info
 WHERE usr_id = ? AND status != 3
#end

#sql("userMenuPage")
SELECT
  usr.usr_id,
  usr.email,
  usr.is_admin,
  usr.is_boss,
  usr.login_name,
  usr.name,
  usr.persist_version,
  usr.phone,
  usr.pwd_last_change_date,
  usr.register_date,
  usr.status,
  usr.is_have_extra,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name,
  uodp.is_default,
  uodp.id as uodp_id
FROM user_info usr
  JOIN user_org_dept_pos uodp
    ON usr.usr_id = uodp.usr_id
  JOIN organization org
    ON org.org_id = uodp.org_id
       AND org.status != 3
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
       AND dept.status != 3
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
       AND pos.status != 3
where 1=1 AND usr.status != 3
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("name".equals(x.key))
          usr.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          usr.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
   order by usr.login_name,uodp.is_default desc
#end

#sql("userGroupIds")
SELECT
  group_id
 FROM user_group_with_user
 WHERE uodp_id = ?
#end