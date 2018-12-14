#sql("list")
  select
	ct.code,
	ct.`desc`,
	cv.cat_code,
	cv.id,
	cv.`key`,
	cv.value
from
	category_type ct,
	category_value cv
where
	ct.code = cv.cat_code
    #for(x:map)
      #if(x.value&&x.value!="")
        and
        #if("query_key".equals(x.key))
          cv.value like concat('%', #para(x.value), '%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
#end