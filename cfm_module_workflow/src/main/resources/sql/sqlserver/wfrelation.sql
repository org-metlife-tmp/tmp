#sql("findRelationToPage")
select
	id,
	base_id,
	[name],
	create_on,
	is_activity,
	org_exp,
	dept_exp,
	biz_exp,
	biz_setting_exp,
	persist_version
from
	cfm_workflow_relation
where 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("delRelationById")
delete
from
	cfm_workflow_relation
where
	id = ? and persist_version = ?
#end