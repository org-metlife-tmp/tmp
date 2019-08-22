#sql("getSettleById")
SELECT
  id,
  acc_no,
  acc_name,
  org_id,
  org_name,
  curr_id,
  curr_name,
  bank_name,
  cnaps_code,
  open_date,
  org_seg,
  detail_seg,
  pay_recv_attr,
  status
FROM
  settle_account_info
WHERE id = ? AND `status` != 3
 LIMIT 1
#end

#sql("settlePage")
SELECT
  id,
  acc_no,
  acc_name,
  org_id,
  org_name,
  curr_id,
  curr_name,
  bank_name,
  cnaps_code,
  open_date,
  org_seg,
  detail_seg,
  pay_recv_attr,
  `status`,
  memo
FROM settle_account_info
where 1=1 and status !=3
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("query_key".equals(x.key))
          acc_name like concat('%', #para(x.value), '%')
        #elseif("acc_no".equals(x.key))
          acc_no like concat('%', #para(x.value), '%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("settleList")
SELECT
  id,
  acc_no,
  acc_name,
  org_id,
  org_name,
  curr_id,
  curr_name,
  bank_name,
  cnaps_code,
  open_date,
  org_seg,
  detail_seg,
  pay_recv_attr,
  `status`,
  memo
FROM settle_account_info
  where 1=1 and status != 3
    #for(x:cond)
      #if(x.value&&x.value!="")
        and
          #if("acc_no".equals(x.key) || "acc_name".equals(x.key))
            #(x.key) like '%#(x.value)%'
          #else
            #(x.key) '#(x.value)'
          #end
      #end
    #end
#end

#sql("getAccnoNumByAccno")
  select count(id) as counts from settle_account_info where acc_no = ? limit 1
#end

#sql("chgByAccno")
  select count(id) as counts from settle_account_info where acc_no = ? and id != ? limit 1
#end