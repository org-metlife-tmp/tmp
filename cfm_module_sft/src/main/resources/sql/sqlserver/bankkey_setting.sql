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
	bankkey_setting bankkey
	inner join channel_setting channel on bankkey.channel_id = channel.id
	inner join organization org on bankkey.org_id = org.org_id
	inner join const_bank_type bank on bankkey.bank_type = bank.code
	left join user_info ui on ui.usr_id = bankkey.update_by
WHERE 1 = 1
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

#sql("getcurrentorg")
SELECT
	org_id,
	name
FROM
	organization org
WHERE 1 = 1
   #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
       #if("org_ids".equals(x.key))
            org_id in(
              #for(z : map.org_ids)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
        #end
      #end
    #end
  #end
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
and bankkey.id = ?
#end


