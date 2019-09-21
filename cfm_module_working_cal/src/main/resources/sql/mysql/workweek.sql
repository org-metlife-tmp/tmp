#sql("findWeekList")
 select year, serial_no, left(CONVERT(VARCHAR(100), start_date, 120),10) start_date,left(CONVERT(VARCHAR(100), end_date, 120),10) end_date  from working_week_setting where year = ?
#end

#sql("deleteWeekList")
  delete  working_week_setting where year = ?
#end