#namespace("auto_check_cfm")

  #sql("inner_db_payment_list")
    select id,instruct_code,create_on,payment_amount,pay_account_no,recv_account_no
    from inner_db_payment
    where is_checked = 0
    and service_status = 7
    and DATEDIFF(day,create_on,GETDATE()) = 1
  #end
  
  #sql("outer_zf_payment_list")
    select id,instruct_code,create_on,payment_amount
    from outer_zf_payment
    where is_checked = 0
    and service_status = 7
    and DATEDIFF(day,create_on,GETDATE()) = 1
  #end
  
  #sql("collect_execute_instruction_list")
    select id,instruct_code,create_on,collect_amount payment_amount,pay_account_no,recv_account_no
    from collect_execute_instruction
    where is_checked = 0
    and collect_status = 3
    and DATEDIFF(day,create_on,GETDATE()) = 1
  #end
  
 
  
    #sql("oa_head_payment_list")
    select id,instruct_code,left(bank_serial_number,8) AS create_on,payment_amount
    from oa_head_payment
    where is_checked = 0
    and service_status = 7
    and bank_serial_number is not null
    and DATEDIFF(day,left(bank_serial_number,8),GETDATE()) = 1
  #end


  
  #sql("oa_branch_payment_item_inlist")
    select id,instruct_code,left(bank_serial_number,8) AS create_on,payment_amount,pay_account_no,recv_account_no
    from oa_branch_payment_item
    where is_checked = 0
    and service_status = 1
    and item_type = 1
    and bank_serial_number is not null
    and DATEDIFF(day,left(bank_serial_number,8),GETDATE()) = 1
  #end
  #sql("oa_branch_payment_item_outlist")
    select id,instruct_code,left(bank_serial_number,8) AS create_on,payment_amount
    from oa_branch_payment_item
    where is_checked = 0
    and service_status = 1
    and item_type = 2
    and bank_serial_number is not null
    and DATEDIFF(day,left(bank_serial_number,8),GETDATE()) = 1
  #end

  #sql("get_his_trans")
    select id,amount,trans_date,instruct_code
    from acc_his_transaction
    where is_checked = 0
    and amount = ?
    and instruct_code = ?
    and DATEDIFF(day,?,trans_date) >= 0
  #end

  #sql("get_his_trans_two")
    SELECT
      id,
      acc_id,
      acc_no,
      acc_name,
      case direction when '1' then '付' when '2' then '收' else '其他' end direction,
      opp_acc_no,
      opp_acc_name,
      amount,
      summary,
      convert(varchar,trans_date)+' '+convert(varchar,trans_time) trans_date
    FROM
      acc_his_transaction
    where is_checked = 0
      and direction = 1
      and acc_no = #para(map.pay_account_no)
      and amount = #para(map.payment_amount)
      and instruct_code = #para(map.instruct_code)
      and DATEDIFF(day,#para(map.create_on),trans_date) >= 0
    UNION ALL
    SELECT
      id,
      acc_id,
      acc_no,
      acc_name,
      case direction when '1' then '付' when '2' then '收' else '其他' end direction,
      opp_acc_no,
      opp_acc_name,
      amount,
      summary,
      convert(varchar,trans_date)+' '+convert(varchar,trans_time) trans_date
    FROM
      acc_his_transaction
    where is_checked = 0
      and direction = 2
      and acc_no = #para(map.recv_account_no)
      and amount = #para(map.payment_amount)
      and opp_acc_no = #para(map.pay_account_no)
      and DATEDIFF(day,#para(map.create_on),trans_date) >= 0
  #end

  #sql("inner_batchpay_list")
  select
    di.detail_id id,
    di.instruct_code,
    bi.create_on,
    di.payment_amount,
    bi.pay_account_no,
    di.recv_account_no
  from
    inner_batchpay_baseinfo bi,
    inner_batchpay_bus_attach_detail di
  where
    bi.batchno = di.batchno
    and bi.delete_flag = 0
    and di.is_checked = 0
    and di.pay_status = 1
    and DATEDIFF(day,bi.create_on,GETDATE())=1
  #end

  #sql("outer_batchpay_list")
  select
    di.detail_id id,
    di.instruct_code,
    bi.create_on,
    di.payment_amount,
    bi.pay_account_no,
    di.recv_account_no
  from
    outer_batchpay_baseinfo bi,
    outer_batchpay_bus_attach_detail di
  where
    bi.batchno = di.batchno
    and bi.delete_flag = 0
    and di.is_checked = 0
    and di.pay_status = 1
    and DATEDIFF(day,bi.create_on,GETDATE())=1
  #end

  ###生成凭证
  #sql("sun_voucher_data_list")
    SELECT
      id,
      trans_id,
      isnull(
      	account_code,''
      ) as account_code,
      account_period,
      a_code1,
      a_code2,
      a_code3,
      a_code5,
      a_code6,
      a_code7,
      a_code10,
      base_amount,
      currency_code,
      debit_credit,
      description,
      journal_source,
      transaction_amount,
      transaction_date,
      transaction_reference,
      file_name,
      export_count
    FROM
      sun_voucher_data
    WHERE file_name IS NULL
  #end

#end
