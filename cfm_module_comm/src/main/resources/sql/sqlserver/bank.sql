#sql("allBankType")
SELECT code, name, pinyin, jianpin, display_name FROM const_bank_type_query
where 1=1
#if(map != null)
    #for(x:map)
      #if(x.value&&x.value!="")
        and
        #if("name".equals(x.key) || "pinyin".equals(x.key) || "jianpin".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findConstBankType")
SELECT code, [name], [name] as display_name FROM const_bank_type
#end

#sql("bankList")
SELECT cnaps_code, name, swift_code, bank_type, province, city, country, address, area_code, pinyin, jianpin FROM all_bank_info
where 1=1
#if(map != null)
    #for(x:map)
      #if(x.value&&x.value!="")
        and
        #if("name".equals(x.key) || "pinyin".equals(x.key) || "jianpin".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end