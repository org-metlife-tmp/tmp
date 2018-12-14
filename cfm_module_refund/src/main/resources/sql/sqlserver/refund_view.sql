#sql("alreadyRefundTradeList")
  select 
      aht.id  AS trade_id ,
      aht.* 
  from 
      acc_his_transaction aht 
  where 
      aht.refund_bill_id is not null
      and
      aht.is_refund_scan = 1 
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
      *
    from
   #(map.table_name) AS tb,
   #(map.table_name1) AS tbDetail
   where 
   tb.batchno = tbDetail.batchno
   and tbDetail.detail_id = #(map.detail_id)   
#end

