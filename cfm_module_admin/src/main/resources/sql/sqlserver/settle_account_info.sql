#sql("getSettleById")
SELECT
  top 1
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
 WHERE id = ? AND [status] != 3
#end

#sql("settlePage")
SELECT
  sett.id,
  sett.acc_no,
  sett.acc_name,
  sett.org_id,
  sett.org_name,
  sett.curr_id,
  sett.curr_name,
  sett.bank_name,
  sett.cnaps_code,
  sett.open_date,
  sett.org_seg,
  sett.detail_seg,
  sett.pay_recv_attr,
  sett.[status],
  sett.memo,
  bank.bank_type,
  bank.province,
  bank.city
FROM settle_account_info sett,all_bank_info bank
where 1=1 and status !=3 and sett.cnaps_code = bank.cnaps_code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("query_key".equals(x.key))
          sett.acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("acc_no".equals(x.key))
          sett.acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          sett.#(x.key) = #para(x.value)
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
  status,
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
  select top 1 count(id) as counts from settle_account_info where acc_no = ?
#end

#sql("chgByAccno")
  select top 1 count(id) as counts from settle_account_info where acc_no = ? and id != ?
#end