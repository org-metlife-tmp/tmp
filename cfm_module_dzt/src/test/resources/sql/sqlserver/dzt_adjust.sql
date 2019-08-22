#sql("findCheckVoucherBalAdjust")
  SELECT
    id,
    acc_id,
    acc_no,
    acc_name,
    org_name,
    [year],
    [month],
    checkout_date,
    pre_voucher_bal,
    voucher_bal,
    acc_bal,
    voucher_adjust_bal,
    acc_adjust_bal,
    is_confirm,
    persist_version
  FROM
    check_voucher_bal_adjust
  WHERE  1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #(x.key) = #para(x.value)
      #end
    #end
  #end
  order by id desc
#end

#sql("findCheckVoucherBal")
  SELECT
    id,
	acc_id,
	acc_no,
	acc_name,
	[year],
	[month],
	balance
  FROM
    check_voucher_acc_balance
  WHERE  1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #(x.key) = #para(x.value)
      #end
    #end
  #end
#end

#sql("findWorkingCalSetting")
SELECT
	cdate,
	is_holiday,
	is_checkout,
	day_of_week
FROM
	working_cal_setting
WHERE  1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("year".equals(x.key))
          year(cdate) = #para(x.value)
        #elseif("month".equals(x.key))
          month(cdate) = #para(x.value)
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findAccHisTrans")
SELECT
	SUM(amount) as amount,
	direction
FROM
	acc_his_transaction
WHERE 1=1
	#if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("pre".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) > 0
        #elseif("now".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
	  #end
  #end
GROUP BY direction
#end

#sql("findAccHisTransBydirec")
SELECT
	amount,
	direction,
	summary
FROM
	acc_his_transaction
WHERE 1=1
#if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("pre".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) > 0
        #elseif("now".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
	  #end
  #end
#end

#sql("findAccBalance")
SELECT
	id,
	acc_id,
	acc_no,
	acc_name,
	[year],
	[month],
	balance
FROM
	check_voucher_acc_balance
WHERE acc_id = ?
AND [year] = ?
AND [month] = ?
#end

#sql("findInitdata")
SELECT
	id,
	acc_id,
	acc_no,
	acc_name,
	[year],
	[month],
	balance,
	is_enabled
FROM
	check_voucher_initdata
WHERE acc_id = ?
AND [year] = ?
AND [month] = ?
#end

#sql("findAccHisBalByAccId")
SELECT
	id,
	acc_id,
	acc_no,
	acc_name,
	bank_type,
	bal,
	available_bal,
	frz_amt,
	data_source,
	bal_date,
	import_time
FROM
	acc_his_balance
WHERE acc_id = ?
AND bal_date = ?
#end

#sql("findInitdataItem")
SELECT
	id,
	base_id,
	acc_id,
	data_type,
	credit_or_debit,
	amount,
	memo,
	is_checked
FROM
	check_voucher_initdata_item
WHERE base_id = ?
AND acc_id = ?
AND credit_or_debit = ?
#end

#sql("deleteAdjustItem")
DELETE FROM check_voucher_bal_adjust_item
WHERE base_id = ?
#end