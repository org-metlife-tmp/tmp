#namespace("charge_off_cfm")

  #sql("getcalbydate")
    SELECT
      cdate,
      is_holiday,
      is_checkout,
      day_of_week
    FROM
      working_cal_setting
    WHERE cdate = ?
    AND is_checkout = 1
  #end
  
  #sql("chargeofflist")
    select
      org.name org_name,
      acc.bankcode,
      his.id,
      his.acc_id,
      his.acc_no,
      his.acc_name,
      case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
      his.opp_acc_no,
      his.opp_acc_name,
      his.opp_acc_bank,
      -his.amount,
      his.summary,
      case his.is_checked when '0' then '未核对' when '1' then '已核对' else '' end is_checked,
      case his.precondition when '1' then '已提交' when '2' then '已冲销' else '' end precondition,
      his.trans_date,
      his.is_checked
    from
    acc_his_transaction his
    inner join account acc on acc.acc_id = his.acc_id
    inner join organization org on acc.org_id = org.org_id
    inner join all_bank_info bank on acc.bank_cnaps_code = bank.cnaps_code
    left join acc_his_transaction_ext ext on his.id = ext.trans_id
    where acc.acc_id = his.acc_id
    and acc.org_id = org.org_id
    and acc.bank_cnaps_code = bank.cnaps_code
    and ext.precondition = 1
    and DATEDIFF(day,?,trans_date) < 0
  #end

#end
