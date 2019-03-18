#sql("findPayCounterList")
SELECT
    pay.op_date ,
    pay.op_user_name ,
	pay.id AS pay_id ,
	pay.source_sys,
	pay.origin_id,
	pay.pay_code,
	pay.channel_id,
	pay.org_id,
	pay.org_code,
	pay.amount,
	pay.recv_acc_name,
	pay.recv_cert_type,
	pay.recv_cert_code,
	pay.recv_bank_name,
	pay.recv_acc_no,	
	pay.status ,
	pay.process_msg,
	pay.bank_fb_msg,
	pay.create_time,
	pay.persist_version,
	la.bank_key,
	la.id AS la_id,
	la.legal_id ,
	la.branch_code ,
	la.preinsure_bill_no ,
	la.insure_bill_no ,
	la.biz_type ,
	la.pay_mode ,
	la.pay_date ,
	la.sale_code ,
	la.sale_name ,
	la.op_code ,
	la.op_name ,
	org2.name,
	channel.channel_code ,
	channel.channel_desc ,
	biztype.type_name
FROM
	pay_legal_data AS pay,
	channel_setting AS channel,
	la_pay_legal_data_ext AS  la ,
	organization AS org ,
	organization AS org2 ,
	la_biz_type AS biztype 
WHERE
	pay.id = la.legal_id AND
	channel.org_id = org.org_id AND
	channel.id = pay.channel_id AND
	org2.org_id = pay.org_id AND
	biztype.type_code = la.biz_type 
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
         AND
        #if("source_sys".equals(x.key))
        	pay.source_sys = #para(x.value)
        #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),la.pay_date) >= 0
        #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),la.pay_date) <= 0
        #elseif("preinsure_bill_no".equals(x.key))
            la.preinsure_bill_no  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
            la.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("pay_mode".equals(x.key))
            la.pay_mode = #para(x.value)
        #elseif("biz_type".equals(x.key))
            la.biz_type = #para(x.value)
        #elseif("bank_key".equals(x.key))
            la.bank_key  like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("status".equals(x.key))
            pay.status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
        #elseif("codes".equals(x.key))
            org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
         #elseif("org_id".equals(x.key))
           pay.org_id = #para(x.value)
         #else
           pay.#(x.key) = #para(x.value)    
        #end
      #end
    #end
  #end
  order by pay.pay_code asc
#end

#sql("findDistinctStatus")
SELECT
  DISTINCT(status) AS status
FROM
  pay_legal_data AS pay
WHERE 
pay.id  in (
      #for(y : map)
          #if(for.index > 0)
            #(",")
          #end
          #(y.pay_id)
     #end
     )
#end