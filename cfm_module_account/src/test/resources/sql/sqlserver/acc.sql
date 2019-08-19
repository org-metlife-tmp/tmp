#sql("findAccountToPage")
select
	acc.acc_id,
	acc.acc_no,
	acc.acc_name,
	acc.acc_attr,
	acc.acc_purpose,
	acc.open_date,
	acc.lawfull_man,
	acc.cancel_date,
	acc.curr_id,
	acc.bank_cnaps_code,
	acc.org_id,
	acc.interactive_mode,
	acc.memo,
	acc.is_activity,
	acc.is_virtual,
	acc.status,
	bank.name as bank_name,
	org.name as org_name,
	(select [value] from category_value where cat_code = 'acc_attr' and [key] = acc.acc_attr) as acc_attr_name
from
	account acc,
	all_bank_info bank,
	organization org
where acc.bank_cnaps_code = bank.cnaps_code
and acc.org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("query_key".equals(x.key))
            acc.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("service_status".equals(x.key))
            acc.status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("org_name".equals(x.key))
            org.name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findAccountExtType")
select
	[code],
	[name]
from
	acc_extra_info_type
#end

#sql("findAccountExtInfo")
SELECT
  id,
  acc_id,
  type_code,
  [value]
FROM acc_extra_info
where acc_id = ?
#end

#sql("findCategory")
  select
    id,
    cat_code,
    [key],
    [value]
  from
    category_value
  where cat_code = ?
  and [key] = ?
#end
###根据状态获取账户列表
#sql("listByST")
SELECT
  acc_id,
  acc_no,
  acc_name,
  org.name AS org_name,
  acc.lawfull_man,
  curr.name AS curr_name,
  bank.name AS bank_name,
  acc.interactive_mode,
  acc.acc_purpose,
  (select [value] from category_value where cat_code = 'acc_purpose' and [key] = acc.acc_purpose) as acc_purpose_name,
  acc.acc_attr,
  cv.value as acc_attr_name,
  CONVERT(DATE,acc.open_date,110) as open_date
FROM
  account acc
  JOIN organization org
    ON org.org_id = acc.org_id
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  JOIN all_bank_info bank
    ON bank.cnaps_code = acc.bank_cnaps_code
    left join category_value cv
  	ON acc.acc_attr = cv.[key]
WHERE cv.cat_code='acc_attr'
  AND NOT EXISTS
  (SELECT
    1
  FROM
    acc_process_lock l
  WHERE l.acc_id = acc.acc_id
  #if(acc_id != null && !"".equals(acc_id))
    AND l.acc_id <> #para(acc_id)
  #end
  )
  AND acc.status = #para(status)
  AND acc.org_id = #para(org_id)
  AND acc.is_activity = 1
#end

###根据账户ID获取账户
#sql("getAccByAccId")
SELECT
  top 1
  acc_id,
  acc_no,
  acc_name,
  org.name AS org_name,
  acc.lawfull_man,
  curr.name AS curr_name,
  bank.name AS bank_name,
  acc.interactive_mode,
  acc.acc_purpose,
  acc.acc_attr,
  CONVERT(DATE,acc.open_date,110) as open_date,
  cv.value as acc_attr_name,
  pur.value as acc_purpose_name
FROM
  account acc
  JOIN organization org
    ON org.org_id = acc.org_id
  LEFT JOIN currency curr
    ON acc.curr_id = curr.id
  JOIN all_bank_info bank
    ON bank.cnaps_code = acc.bank_cnaps_code
    left join category_value cv
  	ON acc.acc_attr = cv.[key]
  	LEFT JOIN category_value pur
  	ON acc.acc_purpose = pur.[key]
WHERE cv.cat_code='acc_attr' and pur.cat_code = 'acc_purpose' and acc.acc_id = ?
#end

#sql("chgAccountExtraInfo")
  update
    acc_extra_info
  set
    [value] = ?
  where acc_id = ? and type_code = ?
#end

#sql("findMainAccount")
SELECT
	acc.acc_id ,
	acc.acc_no,
	acc.acc_name,
	bank.address,
	org.org_id,
	org.name as org_name,
	org.level_num,
	bank.bank_type,
	bank.name as bank_name
FROM
	account acc,
	organization org,
	all_bank_info bank
WHERE
	acc.org_id = org.org_id
AND acc.bank_cnaps_code = bank.cnaps_code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("query_key".equals(x.key))
          charindex(convert(varchar(255),#para(x.value)), acc.acc_no ) > 0
        #elseif("status".equals(x.key))
          acc.status = #para(x.value)
        #elseif("level_code".equals(x.key))
          charindex(convert(varchar(255),#para(x.value)), org.level_code ) = 1
        #elseif("level_num".equals(x.key))
          org.level_num >= #para(x.value)
        #elseif("exclude_ids".equals(x.key))
          acc.acc_id NOT IN (#para(x.value))
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end