#sql("paylist")
SELECT
	id,
	source_sys,
	org_id,
	org_name,
	case pay_mode when 'C' then '批量收付' when 'Q' then '实时收付' when 'H' then '第三方' when '0' then '网银' else '' end pay_mode,
	biz_code,
	preinsure_bill_no,
	insure_bill_no,
	biz_type,
	amount,
	service_status,
	recv_acc_name,
	recv_cert_code,
	recv_account_name,
	recv_account_no,
	recv_account_no recv_acc_no,
	recv_bank_name,
	pay_account_no,
	pay_bank_name,
	is_checked,
	check_service_number,
	check_user_name,
	check_date,
	consumer_acc_name,
	send_on
FROM
	v_gmf_index gmf
where 1 = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("service_status".equals(x.key))
          gmf.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("is_checked".equals(x.key))
          gmf.is_checked in(
            #for(y : map.is_checked)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("org_ids".equals(x.key))
          gmf.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("org_id".equals(x.key))
          gmf.org_id = #para(x.value)
        #elseif("preinsure_bill_no".equals(x.key))
          gmf.preinsure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("insure_bill_no".equals(x.key))
          gmf.insure_bill_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("consumer_acc_name".equals(x.key))
          gmf.consumer_acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("recv_account_no".equals(x.key))
          gmf.recv_account_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("source_sys".equals(x.key))
          gmf.source_sys = #para(x.value)
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,gmf.send_on,110)) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,gmf.send_on,110)) <= 0
        #elseif("min".equals(x.key))
          gmf.amount >= #para(x.value)
        #elseif("max".equals(x.key))
          gmf.amount <= #para(x.value)
        #else
          gmf.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by gmf.id desc
#end

#sql("tradingList")
SELECT
  his.id,
  his.acc_id,
  his.acc_no,
  his.acc_name,
  case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
  his.opp_acc_no,
  his.opp_acc_name,
  his.amount,
  his.summary,
  convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
  his.is_checked,
  his.business_check,
	his.check_service_number,
	his.check_user_name,
	his.check_date,
	his.opp_acc_bank,
	bank.name bank_name,
	acc.bankcode
FROM
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	his.acc_id = acc.acc_id
	AND acc.bank_cnaps_code = bank.cnaps_code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("summary".equals(x.key))
          his.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("is_checked".equals(x.key))
          his.is_checked in(
            #for(y : map.is_checked)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("min".equals(x.key))
          his.amount >= #para(x.value)
        #elseif("max".equals(x.key))
          his.amount <= #para(x.value)
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,his.trans_date,110)) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),CONVERT(DATE,his.trans_date,110)) <= 0
        #else
          his.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("getHisByInstructCode")
SELECT
  his.id,
  his.acc_id,
  his.acc_no,
  his.acc_name,
  case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
  his.opp_acc_no,
  his.opp_acc_name,
  his.amount,
  his.summary,
  convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
  his.is_checked,
  his.business_check,
	his.check_service_number,
	his.check_user_name,
	his.check_date,
	his.opp_acc_bank,
	bank.name bank_name,
	acc.bankcode
FROM
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	his.acc_id = acc.acc_id
	AND acc.bank_cnaps_code = bank.cnaps_code
  AND his.is_checked = 0
  AND his.amount = ?
  AND his.instruct_code = ?
  AND DATEDIFF(day,?,his.trans_date) = 0
#end

#sql("getHisByInfos")
SELECT
  his.id,
  his.acc_id,
  his.acc_no,
  his.acc_name,
  case his.direction when '1' then '付' when '2' then '收' else '其他' end direction,
  his.opp_acc_no,
  his.opp_acc_name,
  his.amount,
  his.summary,
  convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) trans_date,
  his.is_checked,
  his.business_check,
	his.check_service_number,
	his.check_user_name,
	his.check_date,
	his.opp_acc_bank,
	bank.name bank_name,
	acc.bankcode
FROM
	acc_his_transaction his,
	account acc,
	all_bank_info bank
WHERE
	his.acc_id = acc.acc_id
	AND acc.bank_cnaps_code = bank.cnaps_code
  AND his.is_checked = 0
  AND his.acc_no = #para(map.pay_account_no)
  AND his.amount = #para(map.amount)
  AND his.opp_acc_no = #para(map.recv_account_no)
  AND convert(varchar,his.trans_date)+' '+convert(varchar,his.trans_time) >= #para(map.send_on)
#end




