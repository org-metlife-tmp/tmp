#sql("personalWaitList")
SELECT
   recv.* ,
   mat.*  
FROM
   (select mat.* ,
        bank.name ,
	    org1.name AS recv_org_name 
		from 
	    recv_counter_match AS mat ,
		organization AS org1 ,
		const_bank_type AS bank 
		where 
		org1.org_id = mat.recv_org_id AND
		bank.code = mat.consumer_bank_name
   )  mat
left join (
	select 
	org2.name  AS bill_org_name ,
	recv.wait_match_id ,
    recv.id AS recv_id,
    recv.source_sys ,
    recv.insure_bill_no ,
    recv.preinsure_bill_no ,
    recv.insure_acc_no,
    case recv.use_funds  when '0' then '客户账户' when '1' then '新单签发' when '2' then '保全收费'
    when '3' then '定期结算收费' when '4' then '续期收费' when '5' then '不定期收费' when '6' then '保单暂记' 
    when '7' then '追加投资悬账' end use_funds,
    recv.bill_type ,
    recv.consumer_no ,
    recv.batch_no ,
    recv.insure_name ,
    recv.consumer_acc_name,
    recv.insure_cer_no ,
    recv.isnot_electric_pay ,
    recv.isnot_bank_transfer_premium  ,
    recv.third_payment,
    recv.check_service_number ,
    recv.business_acc ,
    recv.business_acc_no ,
    recv.payer_relation_insured ,
    recv.pay_reason ,
    recv.pay_code ,
    recv.attachment_count ,
    recv.pay_status 
	from		
	organization AS org2 ,
	recv_counter_bill AS recv 
	where 
		org2.org_id = recv.bill_org_id AND
	    recv.delete_flag = 0 AND
	    recv.wait_match_flag = 1 ) recv
  on mat.id = recv.wait_match_id
  where 	
	mat.delete_flag = 0 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&!"[]".equals(x.value.toString()))
         AND
        #if("recv_org_id".equals(x.key))
            mat.recv_org_id = #para(x.value)
        #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),mat.recv_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),mat.recv_date) <= 0
        #elseif("terminal_no".equals(x.key))
            mat.terminal_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_bank_name".equals(x.key))
            mat.recv_bank_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_mode".equals(x.key))
            mat.recv_mode = #para(x.value)
        #elseif("bill_status".equals(x.key))
            mat.bill_status in(
              #for(y : map.bill_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
           #elseif("org_ids".equals(x.key))
              mat.recv_org_id in(
                #for(z : map.org_ids)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(z)
                #end
              )
        #elseif("match_status".equals(x.key))
            mat.match_status in(
              #for(z : map.match_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("min".equals(x.key))
              mat.amount >= #para(x.value)
         #elseif("max".equals(x.key))
              mat.amount <= #para(x.value)
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  order by mat.id asc
#end
