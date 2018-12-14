#sql("getAllBizTypes")
  select biz_id,biz_name,p_id from cfm_biz_type_setting where is_delete = 0 and is_activity = 1
#end

#sql("getBiztypesByPid")
  select biz_id,biz_name,p_id from cfm_biz_type_setting where is_delete = 0 and is_activity = 1 and p_id = ?
#end