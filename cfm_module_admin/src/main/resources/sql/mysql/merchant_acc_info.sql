#sql("getMerchantList")
  SELECT
    id,
    acc_no,
    acc_name,
    org_id,
    org_name,
    curr_id,
    curr_name,
    channel_code,
    channel_name,
    open_date,
    org_seg,
    detail_seg,
    pay_recv_attr,
    status,
    settle_acc_id,
    settle_acc_no,
    memo
  FROM
    merchant_acc_info
where 1=1 and status != 3
    #for(x:cond)
      #if(x.value&&x.value!="")
        and
          #if("acc_no".equals(x.key) || "acc_name".equals(x.key) || "channel_code".equals(x.key))
            #(x.key) like '%#(x.value)%'
          #else
            #(x.key) = '#(x.value)'
          #end
      #end
    #end
#end

#sql("getMerchantAccInfoById")
  SELECT
    id,
    acc_no,
    acc_name,
    org_id,
    org_name,
    curr_id,
    curr_name,
    channel_code,
    channel_name,
    open_date,
    org_seg,
    detail_seg,
    pay_recv_attr,
    status,
    settle_acc_id,
    settle_acc_no
  FROM
    merchant_acc_info
  WHERE
    id = ?
  AND status != ?
#end

#sql("getMerchantAccInfoByAccNo")
  SELECT
    id,
    acc_no,
    acc_name,
    org_id,
    org_name,
    curr_id,
    curr_name,
    channel_code,
    channel_name,
    open_date,
    org_seg,
    detail_seg,
    pay_recv_attr,
    status,
    settle_acc_id,
    settle_acc_no
  FROM
    merchant_acc_info
  WHERE
    acc_no = ?
#end