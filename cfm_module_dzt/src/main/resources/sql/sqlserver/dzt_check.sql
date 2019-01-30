#sql("billList")
SELECT
	item.id,
	item.base_id,
	item.acc_id,
	item.data_type,
	item.credit_or_debit,
	item.amount,
	item.memo,
	base.acc_no,
	base.acc_name,
	base.year,
	base.month,
	base.balance 
FROM
	check_voucher_initdata_item item,check_voucher_initdata base
where item.base_id = base.id and is_enabled = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("acc_no".equals(x.key))
          	base.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("acc_name".equals(x.key))
          	base.acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min".equals(x.key))
            item.amount >= #para(x.value)
          #elseif("max".equals(x.key))
            item.amount <= #para(x.value)
          #elseif("is_checked".equals(x.key))
            item.is_checked = #para(x.value)
          #elseif("credit_or_debit".equals(x.key))
            item.credit_or_debit = #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by base.acc_no
#end

#sql("tradingList")
  SELECT
	his.id,
	his.acc_id,
	his.acc_no,
	his.acc_name,
	case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
	his.opp_acc_no,
	his.opp_acc_name,
	his.amount,
	his.summary,
	convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
	bank.name bank_name 
FROM
	acc_his_transaction his,account acc,all_bank_info bank  
where his.acc_id = acc.acc_id  and his.is_checked = 0 and acc.bank_cnaps_code = bank.cnaps_code
	and his.acc_no = #para(map.acc_no)
	and his.direction = #para(map.credit_or_debit)
	and his.amount = #para(map.amount)
	and convert(varchar,his.trans_date) >= #para(map.create_on)						
#end

#sql("confirmTradingList")
SELECT
	his.id,
	his.acc_id,
	his.acc_no,
	his.acc_name,
	case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
	his.opp_acc_no,
	his.opp_acc_name,
	his.amount,
	his.summary,
	convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
	bank.name bank_name 
FROM
	acc_his_transaction his,account acc,all_bank_info bank  
where his.acc_id = acc.acc_id  and acc.bank_cnaps_code = bank.cnaps_code
    and his.is_checked = 1
	and his.ref_bill_id  = ?
	and his.checked_ref = 'check_voucher_initdata_item'
#end