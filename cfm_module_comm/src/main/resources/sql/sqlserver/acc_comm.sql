#sql("normallist")
  SELECT
    acc_id,
    acc_no,
    acc_name,
    org.org_id,
    curr.iso_code as curr_code,
    org.name AS org_name,
    acc.lawfull_man,
    acc.bank_cnaps_code,
    acc.deposits_mode,
    curr.name AS curr_name,
    curr.iso_code,
    bank.name AS bank_name,
    bank.bank_type ,
    bank.province,
    bank.city,
    acc.interactive_mode,
    acc.acc_purpose,
    (select [value] from category_value where cat_code = 'acc_purpose' and [key] = acc.acc_purpose) as acc_purpose_name,
    acc.acc_attr,
    (select [value] from category_value where cat_code = 'acc_attr' and [key] = acc.acc_attr) as acc_attr_name,
    CONVERT(DATE,acc.open_date,110) as open_date
  FROM
    account acc
    JOIN organization org
      ON org.org_id = acc.org_id
    LEFT JOIN currency curr
      ON acc.curr_id = curr.id
    JOIN all_bank_info bank
      ON bank.cnaps_code = acc.bank_cnaps_code
  WHERE acc.status = 1
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
    #if(interactive_mode != null && !"".equals(interactive_mode))
      AND interactive_mode = #para(interactive_mode)
    #end
#end