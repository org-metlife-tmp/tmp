#namespace("oa_hotfix_cfm")

  #sql("getOaHotfixOriginData")
    select *
    from oa_origin_data
    where 
    	apply_date > #para(start_date) and
    	apply_date < #para(end_date) and
    	interface_status in (3, 4) and
    	process_status in (3, 5)
  #end

#end
  
