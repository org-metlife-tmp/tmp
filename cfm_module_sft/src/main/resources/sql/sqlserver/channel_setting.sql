#sql("channellist")
SELECT
	chan.*,
	case chan.charge_mode when 0 then charge_amount else null end amount_percent,
	case chan.charge_mode when 1 then charge_amount else null end amount_number,
	org.name org_name,
	config.document_name document_name,
	ui.name update_user
FROM
	channel_setting chan
	inner join organization org on chan.org_id = org.org_id
	left join user_info ui on chan.update_by = ui.usr_id
	left join document_detail_config config on chan.document_moudle = config.id
WHERE 1 = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("channel_code".equals(x.key))
        	chan.id  = #para(x.value)
        #elseif("channel_desc".equals(x.key))
          chan.id  = #para(x.value)
        #elseif("bankcode".equals(x.key))
          chan.bankcode like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("is_checkout".equals(x.key))
          chan.is_checkout in(
            #for(y : map.is_checkout)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #else
          chan.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by id desc
#end

#sql("getAllBankcode")
SELECT
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
  acc.status,
	acc.bankcode
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
WHERE bankcode IS NOT NULL
#end

#sql("getAccByBankCode")
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
WHERE acc.bankcode = ?
#end

#sql("getallaccountno")
SELECT
  acc.acc_id,
  acc.acc_no
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
#end

#sql("getAllChannel")
SELECT
  id channel_id,
	channel_code,
	channel_desc,
	bankcode
FROM
	channel_setting 
where is_checkout = 1 
     #if(null != map.pay_attr  && !"".equals(map.date_validate))
	   and  pay_attr = #para(map.pay_attr) 
	 #end
order by id
#end

#sql("getChannelCode")
SELECT
	*
FROM
	channel_setting
WHERE is_checkout = 1
  and channel_code = ?
#end

#sql("getdoucument")
SELECT
	id,
	document_name
FROM
	document_detail_config
WHERE
	document_type = ?
	and pay_mode = ?
#end

#sql("getchanbypaymode")
SELECT
  id channel_id,
	channel_code,
	channel_desc
FROM
	channel_setting
WHERE is_checkout = 1
	and pay_attr = ?
#end

#sql("getchannelbyid")
SELECT
	chan.*,
	ui.name update_user,
	org.name org_name
FROM
	channel_setting chan,
	user_info ui,
	organization org
WHERE chan.org_id = org.org_id
  AND chan.update_by = ui.usr_id
  AND chan.id = ?
#end

#sql("getchannelbybankcode")
SELECT
	*
FROM
	channel_setting
WHERE bankcode = ?
#end

