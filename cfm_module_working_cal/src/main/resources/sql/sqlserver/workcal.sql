#sql("list")
select 
	substring(convert(varchar(100), cdate, 120 ), 6, 2) [month],
	substring(convert(varchar(100), cdate, 120 ), 9, 2) cdate,
	day_of_week,
	is_holiday,
	is_checkout 
from
	working_cal_setting
where
	left(convert(varchar(100), cdate, 120),4) = ?
	order by [month],cdate
#end

#sql("init")
insert into working_cal_setting(cdate, day_of_week, is_holiday, is_checkout)
values
	(
	?,?,?,?)
#end

#sql("findactive")
select cdate,is_active from working_year_setting
where 
    [type] = ?
and left(convert(varchar(100), cdate, 120),4) = ?
#end

#sql("findisactive")
select cdate,is_active from working_year_setting
where 
is_active = 1
and [type] = ?
and
left(convert(varchar(100), cdate, 120),4) = ?
#end


#sql("upholiday")
update working_cal_setting 
set is_holiday = 1 
where
	left(convert(varchar(100), cdate, 120),10) in (
    #for(x : holiday)
		#(for.index == 0 ? "" : ",") #para(x)
    #end
)
#end

#sql("upnotholiday")
update working_cal_setting 
set is_holiday = 0 
where
	left(convert(varchar(100), cdate, 120),10) in (	
    #for(x : holiday)
		#(for.index == 0 ? "" : ",") #para(x)
    #end
)
#end

#sql("upactive")
update working_year_setting 
set is_active = 1
where
    [type] = ?
and	left(convert(varchar(100), cdate, 120),4) = ?
#end

#sql("checkoutsetn")
update working_cal_setting 
set is_checkout = 0
where 
	left(convert(varchar(100),cdate,120),4) = ?
#end

#sql("checkoutsety")
update working_cal_setting 
set is_checkout = 1
where 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value && x.value!="")
        AND
        #if("year".equals(x.key))
        	left(convert(varchar(100),cdate,120),4) = #para(x.value)
        #elseif("dates".equals(x.key))
            left(convert(varchar(100), cdate, 120),10) in(
              #for(x : map.dates)
                #(for.index > 0 ? ", " : "") #para(x)
              #end
            )
        #else
        	#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end


#sql("checkoutlist")
select convert(varchar(100), cdate, 120) cdate from working_cal_setting
where left(convert(varchar(100),cdate,120),4) = ?
and is_checkout = 1
#end

#sql("findWorkingCalInterval")
  SELECT
	cdate,
	is_holiday,
	is_checkout,
	day_of_week
FROM
	working_cal_setting
WHERE
	  DATEDIFF(day,?,CONVERT(DATE,cdate,110)) >= 0
AND DATEDIFF(day,?,CONVERT(DATE,cdate,110)) <= 0
AND is_checkout = 1
#end
