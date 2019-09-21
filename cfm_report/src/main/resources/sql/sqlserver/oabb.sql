#sql("oabbList")
select *  from
(
SELECT
origin.id ,
origin.flow_id ,
org.org_id ,
org.[name] AS org_name,
origin.apply_date ,
left(head.bank_serial_number,8) AS send_on ,
head.pay_account_no,
head.pay_account_bank,
head.recv_account_name,
head.recv_account_no ,
head.recv_account_bank,
head.payment_amount,
head.payment_summary,
origin.process_status,
origin.process_msg
FROM
oa_origin_data AS origin  ,
oa_head_payment AS head ,
organization AS org
where
head.ref_id  =  origin.id
and
org.org_id = head.org_id
and
head.bank_serial_number is not null
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&& !"[]".equals(x.value.toString()))
         AND
        #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(head.bank_serial_number,8)) >= 0
        #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(head.bank_serial_number,8)) <= 0
			  #elseif("flow_id".equals(x.key))
             origin.flow_id = #para(x.value)
			  #elseif("pay_org_type".equals(x.key))
             origin.pay_org_type = #para(x.value)
			  #elseif("recv_account_name".equals(x.key))
             head.recv_account_name  like convert(varchar(5),'%')+convert(varchar(255),     #para(x.value))+convert(varchar(5),'%')
			  #elseif("min".equals(x.key))
            head.payment_amount >= #para(x.value)
        #elseif("max".equals(x.key))
            head.payment_amount <= #para(x.value)
        #else
            1 = 1
        #end
      #end
    #end
  #end

UNION ALL

SELECT
origin.id ,
origin.flow_id ,
org.org_id ,
org.[name] AS org_name,
origin.apply_date ,
left(item.bank_serial_number,8) AS send_on ,
item.pay_account_no,
item.pay_account_bank,
item.recv_account_name,
item.recv_account_no ,
item.recv_account_bank,
item.payment_amount,
branch.payment_summary,
origin.process_status,
origin.process_msg
FROM
oa_origin_data AS origin  ,
oa_branch_payment AS branch ,
oa_branch_payment_item AS item ,
organization AS org
where
branch.ref_id  =  origin.id
and
org.org_id = branch.org_id
and
item.base_id = branch.id
and
item.bank_serial_number is not null
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&& !"[]".equals(x.value.toString()))
         AND
        #if("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(item.bank_serial_number,8)) >= 0
        #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),left(item.bank_serial_number,8)) <= 0
			  #elseif("flow_id".equals(x.key))
             origin.flow_id = #para(x.value)
			  #elseif("pay_org_type".equals(x.key))
             origin.pay_org_type = #para(x.value)
			  #elseif("recv_account_name".equals(x.key))
             item.recv_account_name  like convert(varchar(5),'%')+convert(varchar(255),     #para(x.value))+convert(varchar(5),'%')
			  #elseif("min".equals(x.key))
            item.payment_amount >= #para(x.value)
        #elseif("max".equals(x.key))
            item.payment_amount <= #para(x.value)
        #else
            1 = 1
        #end
      #end
    #end
  #end
) tab   order by tab.id  desc
#end