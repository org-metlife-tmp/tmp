#sql("findSunVoucherDataList")
SELECT
	voucher.id,
	voucher.trans_id,
	voucher.account_code,
	voucher.account_period,
	voucher.a_code1,
	voucher.a_code2,
	voucher.a_code3,
	voucher.a_code5,
	voucher.a_code6,
	voucher.a_code7,
	voucher.a_code10,
	(SELECT [name] FROM organization WHERE code = voucher.a_code10) as a_code10_name,
	voucher.base_amount,
	voucher.currency_code,
	voucher.debit_credit,
	(CASE voucher.debit_credit  WHEN 'D' THEN '借' WHEN 'C' THEN '贷' END ) as debit_credit_name,
	voucher.description,
	voucher.journal_source,
	voucher.transaction_amount,
	voucher.transaction_date,
	voucher.transaction_reference,
	voucher.file_name,
	voucher.export_count,
	voucher.local_transaction_date,
	voucher.accounting_period,
	voucher.docking_status,
	(CASE voucher.docking_status  WHEN 0 THEN '未生成' WHEN 1 THEN '已生成' END ) as docking_status_name,
	voucher.operator,
	voucher.operator_org,
	voucher.biz_id,
	voucher.biz_name,
	voucher.statement_code,
	voucher.business_ref_no,
	voucher.biz_type,
	voucher.voucher_type,
	org.name as operator_org_name,
	usr.name as operator_name
FROM
sun_voucher_data voucher
LEFT JOIN organization org ON(org.org_id = voucher.operator_org)
LEFT JOIN user_info usr ON(usr.usr_id = voucher.operator)
WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("account_code".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("operator".equals(x.key))
          usr.name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("level_code".equals(x.key))
          org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("level_num".equals(x.key))
          org.level_num >= #para(x.value)
        #elseif("min".equals(x.key))
          base_amount >= #para(x.value)
        #elseif("max".equals(x.key))
          base_amount <= #para(x.value)
        #elseif("start_trans_date".equals(x.key))
          DATEDIFF(day,#para(x.value),local_transaction_date) >= 0
        #elseif("end_trans_date".equals(x.key))
          DATEDIFF(day,#para(x.value),local_transaction_date) <= 0
        #elseif("start_accounting_period".equals(x.key))
          accounting_period >= '#(x.value)'
        #elseif("end_accounting_period".equals(x.key))
           accounting_period <= '#(x.value)'
        #else
          #(x.key) = '#(x.value)'
        #end
      #end
    #end
  #end
  ORDER BY transaction_date desc
#end