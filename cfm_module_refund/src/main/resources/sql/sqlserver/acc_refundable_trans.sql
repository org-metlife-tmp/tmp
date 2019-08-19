#sql("RefundTradeList")
  select 
      aht.* 
  from 
      acc_refundable_trans aht 
  where 
    1 = 1 
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
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
       order by trans_date  desc, trans_time desc    
#end