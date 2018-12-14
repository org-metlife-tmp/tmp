#sql("findList")
  SELECT
   gat.id ,
   gat.service_serial_number ,
   gat.topic ,
   case gat.gyl_allocation_type when '1' then '定额' when '2' then '留存余额'  when '3' then '全额'  else '其他' end gyl_allocation_type ,
   gat.gyl_allocation_amount ,
   case gat.gyl_allocation_frequency when '1' then '每日' when '2' then '每周' when '3' then '每月' else '其他' end gyl_allocation_frequency,
   gat.service_status ,
   gat.summary ,
   gat.is_activity ,
   gat.execute_id ,
   gat.create_by ,
   gat.create_on ,
   gat.update_by ,
   gat.update_on ,
   gat.create_org_id ,
   gat.delete_flag ,
   gat.persist_version ,
   gat.attachment_count ,
   gat.pay_acc_org_id ,
   gat.pay_acc_org_name ,
   gat.pay_acc_id ,
   gat.pay_acc_no ,
   gat.pay_acc_name ,
   gat.pay_acc_bank_name ,
   gat.pay_acc_bank_cnaps_code ,
   gat.recv_acc_no ,
   gat.recv_acc_name ,
   ga.gyl_allocation_time
  FROM
  gyl_allocation_topic gat ,
  (SELECT
   max (ga.gyl_allocation_time) gyl_allocation_time,ga.gyl_allocation_id
   FROM 
   gyl_allocation_timesetting ga group by ga.gyl_allocation_id) ga
  WHERE  
     gat.id = ga.gyl_allocation_id 
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_acc_no".equals(x.key) || "pay_acc_name".equals(x.key) ||
              "recv_acc_no".equals(x.key) || "recv_acc_name".equals(x.key)
              || "topic".equals(x.key))
            #(x.key) like concat('%', #para(x.value), '%')
            #elseif("is_activity".equals(x.key))
            is_activity in(
              #for(y : map.is_activity)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
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
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),create_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by gat.id desc
#end
  