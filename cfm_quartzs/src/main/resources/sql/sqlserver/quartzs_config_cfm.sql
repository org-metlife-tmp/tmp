#namespace("quartzs_config_cfm")
  #sql("init_jobs_sql")
    update cfm_quartz set is_need_scaned = 1 where [type] = 1
  #end
  
  #sql("get_main_job")
    select id,name,class_name,groups,cron,[type],is_need_scaned,enable,param from cfm_quartz where [type] = 0
  #end
  
  #sql("get_needScanjobs_list_sql")
    select id,name,class_name,groups,cron,[type],is_need_scaned,enable,param from cfm_quartz where [type] = 1 and is_need_scaned = 1
  #end
  
  #sql("update_needScan_sql")
    update cfm_quartz set is_need_scaned = ? where [type] = 1 and id = ?
  #end
  
  
  
  
#end