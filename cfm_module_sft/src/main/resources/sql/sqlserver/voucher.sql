#sql("voucherlist")
select tab.*,
       case tab.precondition_id when '0' then '未预提' when '1' then '预提复核中' when '2' then '已预提' when '3' then '撤销复核中'
            when '4' then '已撤销' when '5' then '已冲销' else '' end precondition
from(
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
      isnull(precondition,0) precondition_id,
      his.trans_date,
      ext.presubmit_user_name,
      ext.period_date,
      ext.presubmit_date,
      ext.presubmit_code,
      ext.chargeoff_date,
      ext.chargeoff_code
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
            #elseif("period_date".equals(x.key))
              ext.period_date = #para(x.value)
            #elseif("presubmit_user_name".equals(x.key))
              ext.presubmit_user_name = #para(x.value)
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
              acc.acc_id in(
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
            #elseif("precondition".equals(x.key))
              1 = 1
            #else
              his.#(x.key) = #para(x.value)
            #end
          #end
        #end
      #end)tab
    where 1 =1
      #if(map != null)
        #for(x : map)
          #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
            AND
            #if("precondition".equals(x.key))
              tab.precondition_id in(
                #for(y : map.precondition)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #else
              1 = 1
            #end
          #end
        #end
      #end
    order by tab.id desc
#end

#sql("getaccbyorg")
  SELECT
    acc.acc_id,
    acc.acc_no,
    isnull( acc.bankcode, '' ) bankcode
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
		AND acc.acc_purpose in ('BFZC','BFSR')
    AND charindex( org1.level_code, org2.level_code ) = 1
#end

#sql("findLoadSubmitTrad")
  SELECT
    *
  FROM
    (
  SELECT
    ext.*,
    ISNULL(ext.precondition, 0) precondition_id,
    trans.is_checked,
    trans.id transId
  FROM
    acc_his_transaction trans
    LEFT JOIN acc_his_transaction_ext ext ON trans.id = ext.trans_id
    ) tab
  WHERE
    tab.precondition_id = 0
    AND tab.is_checked = 0
    AND tab.transId IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("findLoadCancelTrad")
  SELECT
    ext.*
  FROM
    acc_his_transaction trans,
    acc_his_transaction_ext ext
  WHERE
    trans.id = ext.trans_id
    AND ext.precondition = 2
    AND datediff(day,ext.presubmit_date,GETDATE()) = 0
    AND trans.id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("findConfirmSubmitTrad")
  SELECT
    ext.*
  FROM
    acc_his_transaction trans,
    acc_his_transaction_ext ext
  WHERE
    trans.id = ext.trans_id
    AND ext.precondition = 1
    AND trans.id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("findConfirmCancelTrad")
  SELECT
    ext.*
  FROM
    acc_his_transaction trans,
    acc_his_transaction_ext ext
  WHERE
    trans.id = ext.trans_id
    AND ext.precondition = 3
    AND trans.id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("delVoucherList")
  DELETE
  FROM
    sun_voucher_data
  WHERE
    voucher_type = 1
    AND trans_id IN (
      #for(y : map.id)
        #if(for.index > 0)
          #(",")
        #end
        #(y)
      #end
  )
#end

#sql("findTrad")
  SELECT
    trans.*
  FROM
    acc_his_transaction trans
  WHERE trans.id IN (
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
    trans.*,
    ext.presubmit_code,
    ext.service_serial_number,
    ext.id extId
  FROM
    acc_his_transaction trans,
    acc_his_transaction_ext ext
  WHERE
    trans.id = ext.trans_id
    AND trans.id IN (
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
  where voucher.voucher_type in(1,2)
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


