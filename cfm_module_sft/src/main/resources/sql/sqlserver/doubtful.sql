#sql("doubtfulLalist")
SELECT
	la.id,
	la.pay_date,
	la.pay_code,
	case la.pay_mode when 'C' then '批量收付' when 'Q' then '实时收付' when 'H' then '第三方' when '0' then '网银' end pay_mode,
	la.bank_key,
  biz.type_name biz_type,
	la.tmp_org_id,
	la.preinsure_bill_no,
	la.insure_bill_no,
	la.amount,
  la.recv_acc_name,
	la.recv_cert_code,
	la.recv_acc_no,
	la.status,
	la.op_user_name,
	la.op_date,
	la.op_reason,
	la.persist_version,
	'0' os_source,
	org.name org_name,
	bankkey.bankkey_desc
FROM
	la_check_doubtful la,
	channel_setting chan,
	bankkey_setting bankkey,
	organization org,
	la_biz_type biz
where la.bank_key = bankkey.bankkey
and la.tmp_org_id = bankkey.org_id
and la.channel_id = chan.id
and la.tmp_org_id = org.org_id
and la.biz_type = biz.type_code
and bankkey.pay_mode = 1
and biz.type = 1
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
          DATEDIFF(day,#para(x.value),pay_date) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),pay_date) <= 0
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

#sql("doubtfulEbslist")
SELECT
	ebs.id,
	ebs.pay_date,
	ebs.pay_code,
	case ebs.pay_mode when 'C' then '批量收付' when 'Q' then '实时收付' when 'H' then '第三方' when '0' then '网银' end pay_mode,
	ebs.bank_key,
  case ebs.biz_type when 1 then '定期结算退费' when 5 then '理赔给付' when 10 then '保全退费' when 12 then '基金单满期退费' when 13 then '客户账户退费' end biz_type,
	ebs.tmp_org_id,
	ebs.preinsure_bill_no,
	ebs.insure_bill_no,
	ebs.amount,
  ebs.recv_acc_name,
	ebs.recv_cert_code,
	ebs.recv_acc_no,
	ebs.status,
	ebs.op_user_name,
	ebs.op_date,
	ebs.op_reason,
	ebs.persist_version,
	'1' os_source,
	org.name org_name,
	bankkey.bankkey_desc
FROM
	ebs_check_doubtful ebs,
	bankkey_setting bankkey,
	channel_setting chan,
	organization org
where ebs.bank_key = bankkey.bankkey
and ebs.tmp_org_id = bankkey.org_id
and ebs.channel_id = chan.id
and chan.org_id = org.org_id
and ebs.is_doubtful = 1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("bank_key".equals(x.key))
          ebs.bank_key like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("bankkey_desc".equals(x.key))
          bankkey.bankkey_desc like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("start_date".equals(x.key))
           DATEDIFF(day,#para(x.value),pay_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),pay_date) <= 0
        #elseif("tmp_org_id".equals(x.key))
          chan.org_id = #para(x.value)
        #elseif("status".equals(x.key))
          ebs.status in(
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
          ebs.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by ebs.id desc
#end




