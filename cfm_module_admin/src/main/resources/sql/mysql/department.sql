#sql("getDepartmentList")
  select dept_id, name, `desc`, status from department
  where 1=1 and status != 3
  #for(x:cond)
    #if(x.value&&x.value!="")
      and
          #if("name".equals(x.key))
            #(x.key) like '%#(x.value)%'
          #else
            #(x.key) '#(x.value)'
          #end
    #end
  #end
#end

#sql("findDepartmentById")
  select dept_id, name, `desc`, status from department where dept_id = ? and status != ?
#end

#sql("checkDeptUse")
  select count(id) from user_org_dept_pos where dept_id = ?
#end

#sql("getDeptNumByName")
SELECT
  count(dept_id) as counts
FROM department
WHERE `name` = ?
  limit 1
#end

#sql("getDeptNumByNameExcludeId")
SELECT
  count(dept_id) as counts
FROM department
WHERE `name` = ?
  and dept_id != ?
  limit 1
#end