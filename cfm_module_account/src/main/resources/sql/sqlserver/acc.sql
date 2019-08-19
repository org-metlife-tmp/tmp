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
	acc.is_close_confirm,
	acc.is_activity,
	acc.is_virtual,
	acc.status,
	acc.deposits_mode,
	acc.subject_code,
	bank.name as bank_name,
	bank.province,
	bank.city,
	org.name as org_name,
	(select [value] from category_value where cat_code = 'acc_attr' and [key] = acc.acc_attr) as acc_attr_name,
	(select [value] from category_value where cat_code = 'acc_purpose' and [key] = acc.acc_purpose) as acc_purpose_name
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
            org.name = #para(x.value)
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("level_num".equals(x.key))
            org.level_num >= #para(x.value)
          #else
            acc.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by acc.acc_id
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
  acc.bank_cnaps_code,
  acc.deposits_mode,
  curr.name AS curr_name,
  curr.iso_code,
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
  #if(exclude_ids != null && exclude_ids.size() > 0)
    and acc.acc_id not in(
    #for(y : exclude_ids)
      #if(for.index > 0)
        #(",")
      #end
      #(y)
    #end
    )
  #end
#end

###根据账户ID获取账户
#sql("getAccByAccId")
SELECT
  top 1
  acc.acc_id,
  acc.acc_no,
  acc.acc_name,
  org.org_id,
  org.name AS org_name,
  acc.lawfull_man,
  curr.name AS curr_name,
  curr.iso_code,
  bank.name AS bank_name,
  acc.interactive_mode,
  acc.acc_purpose,
  acc.acc_attr,
  acc.deposits_mode,
  CONVERT(DATE,acc.open_date,110) as open_date,
  cv.[value] as acc_attr_name,
  pur.[value] as acc_purpose_name,
  acc.bank_cnaps_code,
  bank.province as bank_pro,
  bank.city as bank_city,
  bank.bank_type,
  acc.is_activity,
  acc.status
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
  	and cv.cat_code='acc_attr'
  	LEFT JOIN category_value pur
  	ON acc.acc_purpose = pur.[key]
  	and pur.cat_code = 'acc_purpose'
WHERE acc.acc_id = ?
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
        #elseif("org_id".equals(x.key))
          org.org_id = #para(x.value)
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
  order by acc.acc_id
#end

#sql("accConfirmPage")
  SELECT
  acc.*,
  bank.name as bank_name,
  org.name as org_name,
   curr.name as curr_name,
   curr.iso_code,
  (select [value] from category_value where cat_code = 'acc_attr' and [key] = acc.acc_attr) as acc_attr_name,
	(select [value] from category_value where cat_code = 'acc_purpose' and [key] = acc.acc_purpose) as acc_purpose_name
  FROM
    acc_open_complete_apply aoca
    JOIN account acc
      ON acc.acc_id = aoca.acc_id
    JOIN organization org
      ON org.org_id = acc.org_id
    JOIN all_bank_info bank
      on bank.cnaps_code = acc.bank_cnaps_code
      join currency curr
    on curr.id = acc.curr_id
    WHERE aoca.service_status = 4
      AND acc.status = 1
      and acc.is_activity = 0
      #if(map != null)
        #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("query_key".equals(x.key))
              acc.acc_no like concat('%', #para(x.value), '%')
            #else
              acc.#(x.key) = #para(x.value)
            #end
          #end
        #end
      #end
      order by acc.acc_id
#end

#sql("accCloseConfirmPage")
  SELECT
  acc.*,
  bank.name as bank_name,
  org.name as org_name,
  curr.name as curr_name,
  curr.iso_code,
  (select [value] from category_value where cat_code = 'acc_attr' and [key] = acc.acc_attr) as acc_attr_name,
	(select [value] from category_value where cat_code = 'acc_purpose' and [key] = acc.acc_purpose) as acc_purpose_name
  FROM
    acc_close_complete_apply acca
    JOIN account acc
      ON acc.acc_id = acca.acc_id
    JOIN organization org
      ON org.org_id = acc.org_id
    join currency curr
    on curr.id = acc.curr_id
    join all_bank_info bank
    ON bank.cnaps_code = acc.bank_cnaps_code
    WHERE acca.service_status = 4
      AND acc.is_close_confirm = 0
      #if(map != null)
        #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("query_key".equals(x.key))
              acc.acc_no like concat('%', #para(x.value), '%')
            #else
              acc.#(x.key) = #para(x.value)
            #end
          #end
        #end
      #end
#end

#sql("findAccountByAccNo")
SELECT
	acc_id,
	acc_no,
	acc_name,
	acc_attr,
	acc_purpose,
	open_date,
	lawfull_man,
	cancel_date,
	curr_id,
	bank_cnaps_code,
	org_id,
	interactive_mode,
	memo,
	is_activity,
	is_virtual,
	status,
	is_close_confirm,
	deposits_mode,
	subject_code
FROM
	account
WHERE acc_no = ?
#end