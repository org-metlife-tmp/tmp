#sql("findWeekList")
 select year, serial_no, left(CONVERT(VARCHAR(100), start_date, 120),10) start_date,left(CONVERT(VARCHAR(100), end_date, 120),10) end_date  from working_week_setting where year = ?
#end

#sql("deleteWeekList")
  delete  working_week_setting where year = ?
#end

#sql("isActive")
select cdate,is_active from working_year_setting
where 
left(convert(varchar(100), cdate, 120),4) = ?
and  [type] = 'week'
#end

#sql("upActiveFlag")
update working_year_setting 
set is_active = 1
where
	left(convert(varchar(100), cdate, 120),4) = ?
	and [type] = 'week'
#end
