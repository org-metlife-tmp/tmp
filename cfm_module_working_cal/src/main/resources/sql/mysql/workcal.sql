#sql("list")
select
	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'),6,2) 'month',
	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 9, 2) cdate,
	day_of_week,
	is_holiday,
	is_checkout 
from
	working_cal_setting
where
	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = ?
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
SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = ?
and 'type' = 'day'
#end

#sql("findisactive")
select cdate,is_active from working_year_setting
where 
is_active = 1
and 'type' = 'day'
and
SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = ?
#end


#sql("upholiday")
update working_cal_setting 
set is_holiday = 1 
where
	DATE_FORMAT(cdate,'%Y-%m-%d') in (
    #for(x : holiday)
		#(for.index == 0 ? "" : ",") #para(x)
    #end
)
#end

#sql("upnotholiday")
update working_cal_setting 
set is_holiday = 0 
where
	DATE_FORMAT(cdate,'%Y-%m-%d') in (	
    #for(x : holiday)
		#(for.index == 0 ? "" : ",") #para(x)
    #end
)
#end

#sql("upactive")
update working_year_setting 
set is_active = 1
and 'type' = 'day'
where
	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = ?
#end

#sql("checkoutsetn")
update working_cal_setting 
set is_checkout = 0
where 
	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = ?
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
        	SUBSTRING(DATE_FORMAT(cdate,'%Y-%m-%d'), 1, 4) = #para(x.value)
        #elseif("dates".equals(x.key))
            DATE_FORMAT(cdate,'%Y-%m-%d') in(
              #for(x : map.dates)
                #(for.index > 0 ? ", " : "") #(x)
              #end
            )
        #else
        	#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end


