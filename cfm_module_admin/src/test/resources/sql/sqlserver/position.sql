#sql("getPositionPage")
  SELECT *
  FROM position_info
  WHERE status != 3
  #if(map != null)
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
#end


#sql("checkUse")
  SELECT
    COUNT(id) AS counts
  FROM user_org_dept_pos
  WHERE  pos_id = ?
#end

#sql("getPosInfo")
  SELECT
    top 1
    *
  FROM position_info
  WHERE pos_id = ?
    AND status != 3
#end

#sql("getPosNumByName")
SELECT
  count(pos_id) as counts
 FROM position_info
 WHERE [name] = ?
#end

#sql("getPosNumByNameExcludeId")
SELECT
  count(pos_id) as counts
 FROM position_info
 WHERE [name] = ?
  and pos_id != ?
#end