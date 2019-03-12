#sql("bankkeylist")
SELECT
	bankkey.*,
	org.name orgname,
	channel.channel_code,
	channel.channel_desc,
	channel.bankcode,
	channel.acc_no,
	bank.name bank_name,
	bank.name display_name,
	ui.name update_user
FROM
	bankkey_setting bankkey,
	channel_setting channel,
	organization org,
	const_bank_type bank,
	user_info ui
where bankkey.channel_id = channel.id
and bankkey.org_id = org.org_id
and bankkey.bank_type = bank.code
and ui.usr_id = bankkey.update_by
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("bankkey".equals(x.key))
        	bankkey.bankkey like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("bankkey_desc".equals(x.key))
          bankkey.bankkey_desc like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("channel_code".equals(x.key))
        	channel.id  = #para(x.value)
        #elseif("channel_desc".equals(x.key))
          channel.id  = #para(x.value)
        #elseif("bankkey_status".equals(x.key))
          bankkey.bankkey_status in(
            #for(y : map.bankkey_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #else
          bankkey.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by id desc
#end

#sql("getorg")
SELECT
	org_id,
	name
FROM
	organization
ORDER BY
	org_id
#end

#sql("getbankkeybyid")
SELECT
	bankkey.*,
	org.name orgname,
	channel.channel_code,
	channel.channel_desc,
	channel.bankcode,
	channel.acc_no,
	bank.name bank_name,
	bank.name display_name
FROM
	bankkey_setting bankkey,
	channel_setting channel,
	organization org,
	const_bank_type bank
where bankkey.channel_id = channel.id
and bankkey.org_id = org.org_id
and bankkey.bank_type = bank.code
and bankkey.id = ?
#end


