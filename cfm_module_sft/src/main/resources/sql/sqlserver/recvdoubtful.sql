#sql("doubtfulLalist")
SELECT
	la.id,
	la.recv_date,
	la.pay_code,
	case la.pay_mode when 'C' then '批量收付' when 'Q' then '实时收付' when 'H' then '第三方' when '0' then '网银' end pay_mode,
	la.bank_key,
  biz.type_name biz_type,
	la.tmp_org_id,
	la.preinsure_bill_no,
	la.insure_bill_no,
	la.amount,
  la.pay_acc_name,
	la.pay_cert_code,
	la.pay_acc_no,
	la.status,
	la.op_user_name,
	la.op_date,
	la.op_reason,
	la.persist_version,
	'0' os_source,
	org.name org_name,
	bankkey.bankkey_desc
FROM
	la_recv_check_doubtful la,
	channel_setting chan,
	bankkey_setting bankkey,
	organization org,
	la_biz_type biz
where la.bank_key = bankkey.bankkey
and la.tmp_org_id = bankkey.org_id
and la.channel_id = chan.id
and la.tmp_org_id = org.org_id
and la.biz_type = biz.type_code
and bankkey.pay_mode = 0
and biz.type = 0
and la.is_doubtful = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("bank_key".equals(x.key))
          la.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("bankkey_desc".equals(x.key))
          bankkey.bankkey_desc like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),recv_date) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),recv_date) <= 0
        #elseif("tmp_org_id".equals(x.key))
          chan.org_id = #para(x.value)
        #elseif("status".equals(x.key))
          la.status in(
            #for(y : map.status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("codes".equals(x.key))
          org.code in(
            #for(y : map.codes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #else
          la.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by la.id desc
#end


