#sql("RefundTradeList")
  select 
      aht.id  AS trade_id ,
      aht.* 
  from 
      acc_his_transaction aht 
  where 
      aht.direction = '2'   
      and 
      aht.refund_bill_id is null
      and
      acc_id in(
          #for(y : map.acc_id)
            #if(for.index > 0)
                #(",")
              #end
                convert(varchar,#(y))              
              #end
         )
    #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("acc_no".equals(x.key))
             acc_no like concat('%', #para(x.value), '%')
          #elseif("min".equals(x.key))
            amount >= #para(x.value)
          #elseif("max".equals(x.key))
            amount <= #para(x.value)
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),convert(datetime,convert(varchar,trans_date)+' '+convert(varchar,trans_time),20)) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),convert(datetime,convert(varchar,trans_date)+' '+convert(varchar,trans_time),20)) <= 0
          #else
            1 = 1
          #end
        #end
    #end
  #end
       order by trans_date desc , trans_time desc    
#end


#sql("findBill")
  select 
      tb.id AS bill_id ,
      tb.persist_version AS bill_persist_version,
      tb.* 
   from
   #(map.table_name) tb 
   where 
   1 = 1
   #if(map != null)
   #for(x : map.where)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("service_status".equals(x.key) )
            service_status in(
              #for(y : x.value)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
           #elseif("collect_status".equals(x.key))
            collect_status in(
              #for(z : x.value)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end  
#end


#sql("findBatchBill")
  select 
      tb.id  AS bill_id,
      tb.org_id ,
      tb.pay_account_id,
      tb.pay_account_no,
      tb.pay_account_name,
      tb.pay_account_cur,
      tb.pay_account_bank,
      tb.pay_bank_cnaps,
      tb.pay_bank_prov,
      tb.process_bank_type,
      tb.pay_bank_city,
      tb.create_by,
      tb.create_on,
      tb.persist_version AS bill_persist_version,
      tb.batchno,
      tb.payment_summary,
      tb.service_status,
      tb.attachment_count,
      tb.pay_mode,
      tbDetail.detail_id,
      tbDetail.recv_account_id,
      tbDetail.recv_account_no,
      tbDetail.recv_account_name,
      tbDetail.recv_account_cur,
      tbDetail.recv_account_bank,
      tbDetail.recv_bank_cnaps,
      tbDetail.recv_bank_prov,
      tbDetail.recv_bank_city,
      tbDetail.payment_amount,
      tbDetail.pay_status,
      tbDetail.bank_err_code,
      tbDetail.bank_serial_number,
      tbDetail.bank_err_msg,
      tbDetail.feed_back,
      tbDetail.persist_version AS detail_persist_version
   from
   #(map.table_name) AS tb,
   #(map.table_name1) AS tbDetail
   where 
   tb.batchno = tbDetail.batchno
   #if(map != null)
   #for(x : map.where)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),tb.create_on) >= 0
          #elseif("payment_amount".equals(x.key))
            tbDetail.payment_amount = #para(x.value)
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),tb.create_on) <= 0
          #elseif("refund_flag".equals(x.key))
            tbDetail.refund_flag = #para(x.value)
          #elseif("pay_status".equals(x.key))
            tbDetail.pay_status in(
              #for(y : x.value)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            tb.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end  
#end


#sql("deleteRefund")
  delete from acc_refundable_trans where identifier =  ? 
#end


#sql("findCollectBatchBill")
  select 
      tb.id  AS bill_id,
      tb.org_id ,
      tbDetail.pay_account_id,
      tbDetail.pay_account_no,
      tbDetail.pay_account_name,
      tbDetail.pay_account_cur,
      tbDetail.pay_account_bank,
      tbDetail.pay_bank_cnaps,
      tbDetail.pay_bank_prov,
      tb.process_bank_type,
      tbDetail.pay_bank_city,
      tb.create_by,
      tb.create_on,
      tb.persist_version AS bill_persist_version,
      tb.batchno,
      tb.payment_summary,
      tb.service_status,
      tb.attachment_count,
      tb.pay_mode,
      tbDetail.detail_id,
      tb.recv_account_id,
      tb.recv_account_no,
      tb.recv_account_name,
      tb.recv_account_cur,
      tb.recv_account_bank,
      tb.recv_bank_cnaps,
      tb.recv_bank_prov,
      tb.recv_bank_city,
      tbDetail.payment_amount,
      tbDetail.pay_status,
      tbDetail.persist_version AS detail_persist_version
   from
   #(map.table_name) AS tb,
   #(map.table_name1) AS tbDetail
   where 
   tb.batchno = tbDetail.batchno
   #if(map != null)
   #for(x : map.where)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),tb.create_on) >= 0
          #elseif("payment_amount".equals(x.key))
            tbDetail.payment_amount = #para(x.value)
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),tb.create_on) <= 0
          #elseif("refund_flag".equals(x.key))
            tbDetail.refund_flag = #para(x.value)
          #elseif("pay_account_id".equals(x.key))
            tbDetail.pay_account_id = #para(x.value)
          #elseif("pay_status".equals(x.key))
            tbDetail.pay_status in(
              #for(y : x.value)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #else
            tb.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end  
#end
