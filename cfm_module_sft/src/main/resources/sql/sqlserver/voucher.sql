#sql("voucherlist")
  select
    org.name org_name,
    acc.bankcode,
    his.id,
    his.acc_id,
    his.acc_no,
    his.acc_name,
    bank.name bank_name,
    case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
    his.opp_acc_no,
    his.opp_acc_name,
    his.opp_acc_bank,
    his.amount,
    his.summary,
    case his.is_checked when '0' then '未核对' when '1' then '已核对' else '' end is_checked,
    case ext.precondition when '1' then '已提交' when '2' then '已冲销' else '' end precondition,
    his.trans_date,
    his.check_user_name,
    ext.period_date
  from
  acc_his_transaction his
  inner join account acc on acc.acc_id = his.acc_id
  inner join organization org on acc.org_id = org.org_id
  inner join all_bank_info bank on acc.bank_cnaps_code = bank.cnaps_code
  left join acc_his_transaction_ext ext on his.id = ext.trans_id
  where 1 = 1
    #if(map != null)
      #for(x : map)
        #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
          AND
          #if("min".equals(x.key))
            his.amount >= #para(x.value)
          #elseif("max".equals(x.key))
            his.amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) <= 0
          #elseif("period_start".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) > 0
          #elseif("period_end".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) <= 0
          #elseif("precondition".equals(x.key))
            (ext.precondition not in(
              #for(y : map.precondition)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            ) or ext.precondition is null)
          #elseif("preconditions".equals(x.key))
            ext.precondition in(
              #for(y : map.preconditions)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("org_id".equals(x.key))
            org.org_id in(
              #for(y : map.org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("acc_no".equals(x.key))
            acc.acc_no in(
              #for(y : map.acc_no)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("bankcode".equals(x.key))
            acc.bankcode in(
              #for(y : map.bankcode)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            his.#(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    order by his.id desc
#end

#sql("getaccbyorg")
  SELECT
    acc.*
  FROM
    account acc,
    (
  SELECT
    org_id,
    level_code
  FROM
    organization
  WHERE
    org_id IN (
        #for(y : map.org_id)
          #if(for.index > 0)
            #(",")
          #end
          #(y)
        #end
    )) org1,
    organization org2
  WHERE
    acc.org_id = org2.org_id
    AND charindex( org1.level_code, org2.level_code ) = 1
#end

#sql("findTradById")
  SELECT
    *
  FROM
    acc_his_transaction
  WHERE
    is_checked = 1
    AND id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("findTradList")
  SELECT
    *
  FROM
    acc_his_transaction
  WHERE 1 = 1
    AND id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("getcheckoutday")
  SELECT
    *
  FROM
    working_cal_setting
  WHERE is_checkout = 1
  AND convert(varchar(7),cdate,120) = ?
#end

#sql("getvoucherlist")
  SELECT
    voucher.*
  FROM
    acc_his_transaction his
  inner join account acc on acc.acc_id = his.acc_id
  inner join organization org on acc.org_id = org.org_id
  inner join all_bank_info bank on acc.bank_cnaps_code = bank.cnaps_code
  INNER JOIN sun_voucher_data voucher ON his.id = voucher.trans_id
  left join acc_his_transaction_ext ext on his.id = ext.trans_id
  where 1 = 1
    #if(map != null)
      #for(x : map)
        #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
          AND
          #if("min".equals(x.key))
            his.amount >= #para(x.value)
          #elseif("max".equals(x.key))
            his.amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) <= 0
          #elseif("period_start".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) > 0
          #elseif("period_end".equals(x.key))
            DATEDIFF(day,#para(x.value),his.trans_date) <= 0
          #elseif("precondition".equals(x.key))
            ext.precondition in(
              #for(y : map.precondition)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("org_id".equals(x.key))
            org.org_id in(
              #for(y : map.org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("acc_no".equals(x.key))
            acc.acc_no in(
              #for(y : map.acc_no)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("bankcode".equals(x.key))
            acc.bankcode in(
              #for(y : map.bankcode)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            his.#(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
#end


