#sql("list")
SELECT
	ocd.id,
	ood.bill_no,
	case ood.pay_org_type when 1 then '总公司付款' when 2 then '分公司付款' end pay_org_type,
	ood.apply_user,
	ood.budget_user,
	ood.apply_date,
	ood.recv_acc_no,
	ood.recv_acc_name,
	ood.recv_bank,
	ood.recv_bank_prov,
	ood.recv_bank_city,
	ood.recv_bank_addr,
	ood.recv_bank_type,
	ood.recv_bank_cnaps,
	ood.amount,
	ood.send_count,
	ood.cashier_process,
	ood.cashier_process_date,
	ood.memo,
	ood.apply_org,
	ood.apply_dept,
	ocd.identification,
	ocd.persist_version,
	ocd.create_on,
	org.name as org_name
FROM
	oa_check_doubtful ocd,
	oa_origin_data ood,
	oa_org_mapping oom,
	organization org
WHERE
	ocd.origin_id = ood.id
	AND ood.apply_org = oom.oa_org_code
	and oom.tmp_org_code = org.code
	AND ood.interface_status = 1
	AND ocd.is_doubtful = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("recv_acc_no".equals(x.key) || "recv_acc_name".equals(x.key) || "apply_dept".equals(x.key))
            ood.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("min_amount".equals(x.key))
            ood.amount >= #para(x.value)
          #elseif("max_amount".equals(x.key))
            ood.amount <= #para(x.value)
          #elseif("org_name".equals(x.key))
            org.name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            ood.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
  order by ocd.id
#end

#sql("doubtlist")
SELECT
	id,
	bill_no,
	origin_id,
	case pay_org_type when 1 then '总公司付款' when 2 then '分公司付款' end pay_org_type,
	identification,
	ref_bill_id,
	ref_service_serial_number,
	ref_table,
	CONVERT(DATE,create_on,110) AS create_on,
	persist_version
FROM
	oa_check_doubtful
WHERE 1 = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("bill_no".equals(x.key))
            bill_no != #para(x.value)
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end