  #sql("recvcounterlist")
SELECT
    recv.*
FROM
	recv_counter_bill AS recv
WHERE
	recv.delete_flag = 0
	AND recv.confirm_status = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
         AND
        #if("bill_type".equals(x.key))
        	recv.bill_type = #para(x.value)
        #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.recv_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.recv_date) <= 0
        #elseif("batch_no".equals(x.key))
            recv.batch_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("preinsure_bill_no".equals(x.key))
            recv.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            recv.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("consumer_no".equals(x.key))
            recv.consumer_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_bank_name".equals(x.key))
            recv.recv_bank_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_acc_no".equals(x.key))
            recv.recv_acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("terminal_no".equals(x.key))
            recv.terminal_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("batch_process_no".equals(x.key))
            recv.batch_process_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_org_id".equals(x.key))
              recv.recv_org_id = #para(x.value) 
        #elseif("is_checked".equals(x.key))
            recv.is_checked in(
              #for(z : map.is_checked)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
        #elseif("recv_mode".equals(x.key))
            recv.recv_mode in(
              #for(z : map.recv_mode)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
        #elseif("pay_status".equals(x.key))
            recv.pay_status in(
              #for(z : map.pay_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("min".equals(x.key))
              recv.amount >= #para(x.value)
         #elseif("max".equals(x.key))
              recv.amount <= #para(x.value)
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  order by recv.id asc
#end

#sql("findBillList")
SELECT
	*
FROM
	recv_counter_bill
WHERE
	delete_flag = 0
	and id in (
    #for(x : batchNo)
      #(for.index == 0 ? "" : ",") #para(x)
    #end
	)
#end