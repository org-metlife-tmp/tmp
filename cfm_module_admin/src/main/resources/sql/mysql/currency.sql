#sql("getCurrencyList")
  select id, name, en_name, symbol, iso_code, is_default from currency
  where 1=1
  #for(x:cond)
    #if(x.value&&x.value!="")
      and
      #(x.key) like '%#(x.value)%'
    #end
  #end
#end

#sql("findCurrencyById")
  select id, name, en_name, symbol, iso_code, is_default from currency where id = ?
#end

#sql("currencyDefaultReset")
  update currency set is_default = ?
#end
