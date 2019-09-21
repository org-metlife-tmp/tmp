#sql("findAuthorizeName")
SELECT
	usr_id,
	[name]
FROM
	user_info 
WHERE
	is_admin != 1 
	AND status = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
          	[name] like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("notin".equals(x.key))
            #if(map.notin != null)
              usr_id NOT IN (
                #for(y : map.notin)
                  #for(z : y.excludeIds)
                    #if(for.index > 0)
                      #(",")
                    #end
                    #(z)
                  #end
                #end
              )
            #end
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("getLog")
SELECT
	* 
FROM
	cfm_workflow_authorize_relation_log 
WHERE
	authorize_usr_id = #para(map.usr_id)
ORDER BY
	import_time DESC
#end	
	