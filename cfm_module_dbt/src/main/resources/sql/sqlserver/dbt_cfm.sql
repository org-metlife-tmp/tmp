###调拨通
#namespace("nbdb")
  #include("nbdb.sql")
#end

#namespace("dbttrad")
  #include("dbttrad.sql")
#end

#namespace("dbtbatchtrad")
  #include("dbtbatchtrad.sql")
#end

###调拨通批量
#namespace("batch")
  #include("inner_batch.sql")
#end

###调拨通分页处理sql
#namespace("inner_page")
  #include("inner_page.sql");
#end
