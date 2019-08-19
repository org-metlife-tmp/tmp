#sql("innerPaymentFrom")
FROM
	inner_db_payment
WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_no".equals(x.key) || "pay_account_name".equals(x.key) ||
              "recv_account_no".equals(x.key) || "recv_account_name".equals(x.key))
            #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("service_status".equals(x.key))
            service_status in(
              #for(y : map.service_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("min".equals(x.key))
            payment_amount >= #para(x.value)
          #elseif("max".equals(x.key))
            payment_amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,create_on,110)) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("innerPaymentTotalRowSql")
  SELECT COUNT(*)
#end

#sql("findInnerDbPaymentMoreList")
  SELECT
    id,
    pay_account_id,
    pay_account_no,
    pay_account_name,
    pay_account_bank,
    recv_account_id,
    recv_account_no,
    recv_account_name,
    recv_account_bank,
    payment_amount,
    pay_mode,
    payment_type,
    payment_summary,
    service_status,
    service_serial_number,
    bank_serial_number,
    create_by,
    create_on,
    update_by,
    update_on,
    delete_flag,
    org_id,
    dept_id,
    pay_account_cur,
    recv_account_cur,
    pay_bank_cnaps,
    recv_bank_cnaps,
    pay_bank_prov,
    recv_bank_prov,
    pay_bank_city,
    recv_bank_city,
    process_bank_type,
    persist_version,
    attachment_count,
    feed_back,
    biz_id,
    biz_name
#end

#sql("innerPaymentOrderBy")
  order by service_status,pay_mode asc
#end
