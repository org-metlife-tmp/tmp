#sql("findCollectionList")
SELECT
	ct.id,
	ct.topic,
	ct.service_serial_number ,
	case ct.collect_type when '1' then '定额' when '2' then '留存余额'  when '3' then '全额'  else '其他' end collect_type,
	ct.collect_amount,
	case ct.collect_frequency when '1' then '每日' when '2' then '每周' when '3' then '每月' else '其他' end collect_frequency,
	ct.collect_main_account_count ,
	ct.collect_child_account_count ,
    ct.service_status ,
    ct.summary ,
    ct.is_activity ,
    ct.execute_id ,
    ct.create_by ,
    ct.create_on,
    ct.update_by ,
    ct.update_on ,
    ct.persist_version ,
    ct.attachment_count,
    #--cma.id   AS  main_accountRecord_id,
    cma.main_acc_org_id ,
    cma.main_acc_org_name ,
    cma.main_acc_id ,
    cma.main_acc_no,
    cma.main_acc_name ,
    cma.main_acc_bank_name ,
    cma.main_acc_bank_cnaps_code ,
    cma.child_account_count ,--#
    cct.collect_time
FROM
	collect_topic  ct,
	###collect_main_account  cma ,
   (SELECT
   max (cct.collect_time) collect_time,cct.collect_id
   FROM 
   collect_timesetting cct group by cct.collect_id) cct,
   organization org
WHERE
      org.org_id = ct.create_org_id
     and cct.collect_id =  ct.id
     #--AND
     ct.id = cma.collect_id--#
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("main_acc_no".equals(x.key) || "main_acc_name".equals(x.key) ||
          "topic".equals(x.key))
            #(x.key) like concat('%', #para(x.value), '%')
          #elseif("service_status".equals(x.key))
            service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
            #elseif("is_activity".equals(x.key))
            is_activity in(
              #for(y : map.is_activity)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by service_status asc,ct.id desc
#end


#sql("findMainAccountList")
SELECT
   cma.tab ,
   cma.id AS main_accountRecord_id ,
   cma.main_acc_id ,
   cma.main_acc_org_id,
   cma.main_acc_org_name,
   cma.main_acc_no,
   cma.main_acc_name,
   cma.main_acc_bank_name,
   cma.main_acc_bank_cnaps_code,
   cma.child_account_count
   FROM 
   collect_main_account cma
   WHERE 
   cma.collect_id = ? 
#end

#sql("findChildAccountList")
SELECT
   cca.id AS child_accountRecord_id,
   cca.collect_main_account_id AS main_accountRecord_id,
   cca.child_acc_org_id,
   cca.child_acc_org_name,
   cca.child_acc_id,
   cca.child_acc_no,
   cca.child_acc_name,
   cca.child_acc_bank_name,
   cca.child_acc_bank_cnaps_code
   FROM 
   collect_child_account cca
   WHERE 
   collect_id = ? 
   AND
   collect_main_account_id = ?
#end


#sql("findExcuteTime")
SELECT
   TOP 1
   *
   FROM 
   collect_timesetting cca
   WHERE 
   collect_id = ? 
#end

#sql("findTopicById")
SELECT
   ct.id,
	ct.topic,
	ct.service_serial_number ,
	ct.collect_type,
	ct.collect_amount,
	ct.collect_frequency ,
	ct.collect_main_account_count ,
	ct.collect_child_account_count ,
    ct.service_status ,
    ct.summary ,
    ct.is_activity ,
    ct.execute_id ,
    ct.create_by ,
    ct.create_on,
    ct.update_by ,
    ct.update_on ,
    ct.persist_version ,
    ct.attachment_count 
   FROM 
   collect_topic ct
   WHERE 
   id = ? 
#end

#sql("findInstrList")
SELECT
   cma.main_acc_org_id ,
   cma.main_acc_org_name,
   cma.main_acc_id,
   cma.main_acc_no,
   cma.main_acc_name,
   cma.main_acc_bank_name,
   cma.main_acc_bank_cnaps_code,
   cma.main_acc_cur,
   cma.main_acc_bank_prov,
   cma.main_acc_bank_city,
   cca.child_acc_org_id,
   cca.child_acc_org_name,
   cca.child_acc_id,
   cca.child_acc_no,
   cca.child_acc_cur,
   cca.child_acc_name,
   cca.child_acc_bank_name,
   cca.child_acc_bank_cnaps_code,
   cca.child_acc_bank_prov,
   cca.child_acc_bank_city,
   topic.summary 
   FROM 
   collect_topic topic, collect_main_account cma,collect_child_account cca 
   WHERE
   topic.id = cma.collect_id and cma.id = cca.collect_main_account_id and cma.collect_id = ?
#end
   
#sql("findExecuteList")
SELECT
   ce.id ,
   ce.collect_id ,
   ce.execute_time ,
   ce.collect_amount ,
   ce.success_count ,
   ce.fail_count ,
   ce.collect_status 
   FROM 
   collect_execute ce
   WHERE 
   ce.collect_status = ?
#end

#sql("findExecuteListByCollectId")
SELECT
   ce.id ,
   ce.collect_id ,
   ce.execute_time ,
   ce.collect_amount ,
   ce.success_count ,
   ce.fail_count ,
   ce.collect_status 
   FROM 
   collect_execute ce
   WHERE 
   ce.collect_id = ? and ce.collect_status = ?
#end

#sql("findInstructionGroup")
SELECT
   cei.collect_status ,
   count(1) as cnt 
   FROM 
   collect_execute_instruction cei
   WHERE 
   cei.collect_execute_id = ?
   GROUP BY
   cei.collect_status
#end

#sql("getSuccessMainAccount")
select 
  DISTINCT 
  recv_account_id 
  FROM 
  collect_execute_instruction cei
  WHERE 
  collect_execute_id = ? and collect_status = ?
#end

#sql("getSuccessChildAccount")
select 
  DISTINCT 
  pay_account_id 
  FROM 
  collect_execute_instruction cei
  WHERE 
  collect_execute_id = ? and collect_status = ?
#end

#sql("getSuccessAmount")
select 
  sum(cei.collect_amount) as sm 
  FROM 
  collect_execute_instruction cei
  WHERE 
  cei.collect_execute_id = ? and cei.collect_status = ?
#end

