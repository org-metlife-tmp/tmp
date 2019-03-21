#namespace("test_cfm")
#sql("getAccNo")
  select distinct #(col) from #(tb)
#end

#sql("updAccNo")
  update #(tb) set #(col) = ? where #(col) = ?
#end
#end
