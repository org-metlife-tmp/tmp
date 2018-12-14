#sql("findPoolByCodeAccid")
SELECT
	* 
FROM
	pooling_acc_setting 
WHERE
  delete_flag = 0
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
            #(x.key) = #para(x.value)
        #end
    #end
  #end
#end

#sql("acclist")
SELECT
	p.id,
	a.acc_no,
	a.acc_name,
	p.bank_type code,
	b.name bank_name,
	p.default_flag
FROM
	pooling_acc_setting p,
	const_bank_type b,
	account a 
WHERE
  p.delete_flag = 0
  AND p.acc_id = a.acc_id 
  AND p.bank_type = b.code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("acc_no".equals(x.key))
        	a.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("bank_type".equals(x.key))
          	p.bank_type = #para(x.value)
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by default_flag desc,bank_type, p.id desc
#end

#sql("getaccbybank")
SELECT
	a.acc_id,
	a.acc_no,
	a.acc_name,
	bi.cnaps_code,
	bi.name bank_name,
	bi.province,
	bi.city
FROM
	account a,
	organization o,
	all_bank_info bi
WHERE
	a.org_id = o.org_id 
	AND a.bank_cnaps_code = bi.cnaps_code
	AND o.level_num = 1 
	AND a.interactive_mode = 1 
	AND bi.bank_type = ?
#end

#sql("getDefaultAcc")
SELECT
	pool.id,
	pool.acc_id,
	pool.bank_type,
	pool.delete_flag,
	pool.default_flag,
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
	acc.is_close_confirm,
	acc.is_virtual,
	acc.status,
	acc.deposits_mode,
	acc.subject_code,
	bank.cnaps_code,
	bank.name as bank_name,
	bank.swift_code,
	bank.bank_type,
	bank.province,
	bank.city,
	bank.country,
	bank.address,
	bank.area_code,
	bank.pinyin,
	bank.jianpin,
	curr.name as curr_name,
	curr.en_name,
	curr.symbol,
	curr.iso_code,
	curr.is_default
FROM
	pooling_acc_setting pool,
	account acc,
	all_bank_info bank,
	currency curr
WHERE
	pool.acc_id = acc.acc_id
	AND bank.cnaps_code = acc.bank_cnaps_code
	AND curr.id = acc.curr_id
	AND pool.delete_flag = 0
	#if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("default_flag".equals(x.key))
        	pool.default_flag = #para(x.value)
        #elseif("bank_type".equals(x.key))
          bank.bank_type = #para(x.value)
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end