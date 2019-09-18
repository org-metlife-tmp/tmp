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


#sql("findBills")
  select * from(
    select
        zf.id  AS bill_id,
        zf.org_id ,
        zf.pay_account_id,
        zf.pay_account_no,
        zf.pay_account_name,
        zf.pay_account_cur,
        zf.pay_account_bank,
        zf.pay_bank_cnaps,
        zf.pay_bank_prov,
        zf.recv_account_id,
        zf.recv_account_no,
        zf.recv_account_name,
        zf.recv_account_cur,
        zf.recv_account_bank,
        zf.recv_bank_cnaps,
        zf.recv_bank_prov,
        zf.process_bank_type,
        zf.pay_bank_city,
        zf.create_by,
        zf.create_on,
        zf.persist_version AS bill_persist_version,
        zf.payment_summary,
        zf.payment_amount ,
        zf.service_serial_number ,
        '9' AS biz_type
     from
     outer_zf_payment  AS zf
     where
       zf.service_status in(7, 11)
   #if(map != null)
     #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),zf.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),zf.create_on) <= 0
            #elseif("payment_amount".equals(x.key))
              zf.payment_amount = #para(x.value)
            #elseif("refund_flag".equals(x.key))
              zf.refund_flag = #para(x.value)
            #elseif("pay_account_id".equals(x.key))
              zf.pay_account_id = #para(x.value)
            #elseif("delete_flag".equals(x.key))
              zf.delete_flag = #para(x.value)
            #elseif("is_checked".equals(x.key))
              zf.is_checked = #para(x.value)
            #else
              1 = 1
            #end
          #end
      #end
    #end
   union all
      select
        detail.detail_id  AS bill_id,
        batch_zf.org_id ,
        batch_zf.pay_account_id,
        batch_zf.pay_account_no,
        batch_zf.pay_account_name,
        batch_zf.pay_account_cur,
        batch_zf.pay_account_bank,
        batch_zf.pay_bank_cnaps,
        batch_zf.pay_bank_prov,
        detail.recv_account_id,
        detail.recv_account_no,
        detail.recv_account_name,
        detail.recv_account_cur,
        detail.recv_account_bank,
        detail.recv_bank_cnaps,
        detail.recv_bank_prov,
		    batch_zf.process_bank_type,
        batch_zf.pay_bank_city,
        batch_zf.create_by,
        batch_zf.create_on,
		    detail.persist_version AS bill_persist_version,
		    batch_zf.payment_summary,
        detail.payment_amount,
        detail.bank_serial_number service_serial_number,
        '11' AS biz_type
     from
     outer_batchpay_baseinfo  AS batch_zf ,
     outer_batchpay_bus_attach_detail AS detail
     where
       batch_zf.batchno =  detail.batchno
       and detail.pay_status in (1)
   #if(map != null)
     #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),batch_zf.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),batch_zf.create_on) <= 0
            #elseif("payment_amount".equals(x.key))
              detail.payment_amount = #para(x.value)
            #elseif("refund_flag".equals(x.key))
              detail.refund_flag = #para(x.value)
            #elseif("pay_account_id".equals(x.key))
              batch_zf.pay_account_id = #para(x.value)
            #elseif("delete_flag".equals(x.key))
              batch_zf.delete_flag = #para(x.value)
            #elseif("is_checked".equals(x.key))
              detail.is_checked = #para(x.value)
            #else
              1 = 1
            #end
          #end
      #end
    #end
  union all
    select
    head.id  AS bill_id,
    head.org_id ,
    head.pay_account_id,
    head.pay_account_no,
    head.pay_account_name,
    head.pay_account_cur,
    head.pay_account_bank,
    head.pay_bank_cnaps,
    head.pay_bank_prov,
    head.recv_account_id,
    head.recv_account_no,
    head.recv_account_name,
    head.recv_account_cur,
    head.recv_account_bank,
    head.recv_bank_cnaps,
    head.recv_bank_prov,
    head.process_bank_type,
    head.pay_bank_city,
    head.create_by,
    head.create_on,
    head.persist_version AS bill_persist_version,
    head.payment_summary,
    head.payment_amount ,
    head.service_serial_number ,
      '20' AS biz_type
     from
     oa_head_payment as head
     where
       head.service_status in(7, 11)
   #if(map != null)
     #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),head.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),head.create_on) <= 0
            #elseif("payment_amount".equals(x.key))
              head.payment_amount = #para(x.value)
            #elseif("refund_flag".equals(x.key))
              head.refund_flag = #para(x.value)
            #elseif("pay_account_id".equals(x.key))
              head.pay_account_id = #para(x.value)
            #elseif("delete_flag".equals(x.key))
              head.delete_flag = #para(x.value)
            #elseif("is_checked".equals(x.key))
              head.is_checked = #para(x.value)
            #else
              1 = 1
            #end
          #end
      #end
    #end
   union all
    select
      branch.id AS bill_id,
      branch.org_id ,
      branch.pay_account_id,
      branch.pay_account_no,
      branch.pay_account_name,
      branch.pay_account_cur,
      branch.pay_account_bank,
      branch.pay_bank_cnaps,
      branch.pay_bank_prov,
      branch.recv_account_id,
      branch.recv_account_no,
      branch.recv_account_name,
      branch.recv_account_cur,
      branch.recv_account_bank,
      branch.recv_bank_cnaps,
      branch.recv_bank_prov,
      branch.process_bank_type,
      branch.pay_bank_city,
      branch.create_by,
      branch.create_on,
      branch.persist_version AS bill_persist_version,
      branch.payment_summary,
      branch.payment_amount ,
      branch.service_serial_number ,
      '21' AS biz_type
    from
    oa_branch_payment_item AS branch
    where
       branch.service_status in(7, 11)
   #if(map != null)
     #for(x : map)
          #if(x.value&&x.value!="")
            AND
            #if("start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),branch.create_on) >= 0
            #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),branch.create_on) <= 0
            #elseif("payment_amount".equals(x.key))
              branch.payment_amount = #para(x.value)
            #elseif("refund_flag".equals(x.key))
              branch.refund_flag = #para(x.value)
            #elseif("pay_account_id".equals(x.key))
              branch.pay_account_id = #para(x.value)
            #elseif("delete_flag".equals(x.key))
              branch.delete_flag = #para(x.value)
            #elseif("is_checked".equals(x.key))
              branch.is_checked = #para(x.value)
            #else
              1 = 1
            #end
          #end
      #end
    #end
    )tab
    order by service_serial_number desc
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
