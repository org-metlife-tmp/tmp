#sql("list")
select
	t2.bank_type,
	left(cdate, len(cdate)-1) AS cdate 
from
	(
		select bank_type,
			(select DATE_FORMAT(offer_date,'%Y-%m-%d') + ',' 
					from
						working_offerdate_setting 
					where
						bank_type = t1.bank_type FOR XML PATH ('')) AS cdate
		from
			working_offerdate_setting t1
		where 1 = 1
		  #if(map != null)
		    #for(x : map)
		      #if(x.value && x.value!="")
		        AND
		        #if("year".equals(x.key))
		          SUBSTRING(DATE_FORMAT(offer_date,'%Y-%m-%d'), 1, 4) = #para(x.value)
		        #else
		          #(x.key) = #para(x.value)
		        #end
		      #end
		    #end
		  #end
		group by
			bank_type 
	)t2
#end

#sql("add")
insert into working_offerdate_setting(bank_type, offer_date)
values
	(
	?,?)
#end

#sql("deloffer")
delete working_offerdate_setting
    where 
    bank_type = #para(bank_type)
    and SUBSTRING(DATE_FORMAT(offer_date,'%Y-%m-%d'), 1, 4) = #para(offer_date)
#end