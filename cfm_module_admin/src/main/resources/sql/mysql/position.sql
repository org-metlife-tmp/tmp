#sql("getPositionPage")
  SELECT *
  FROM position_info
  WHERE `status` != 3
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("name".equals(x.key))
            #(x.key) like concat('%', #para(x.value), '%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end


#sql("checkUse")
  SELECT
    COUNT(id) AS counts
  FROM `user_org_dept_pos`
  WHERE  pos_id = ?
#end

#sql("getPosInfo")
  SELECT
    *
  FROM position_info
  WHERE pos_id = ?
    AND `status` != 3
 limit 1
#end

#sql("getPosNumByName")
SELECT
  count(pos_id) as counts
FROM position_info
WHERE `name` = ?
 limit 1
#end

#sql("getPosNumByNameExcludeId")
SELECT
  count(pos_id) as counts
FROM position_info
WHERE `name` = ?
  and pos_id != ?
 limit 1
#end