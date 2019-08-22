#sql("getChannelById")
SELECT
  [code],
  [name],
  third_party_flag,
  is_activate,
  memo
FROM
  handle_channel_setting
WHERE code = ?
#end

#sql("getChannelPage")
SELECT
  [code],
  [name],
  third_party_flag,
  is_activate,
  memo
FROM
  handle_channel_setting
where 1=1
  #if(map??)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("query_key".equals(x.key))
          [name] like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end