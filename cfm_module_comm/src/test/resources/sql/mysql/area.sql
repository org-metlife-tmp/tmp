#sql("allTopLevel")
  SELECT name, area_type, pinyin, jianpin FROM const_top_level_area
where 1=1
  #if(map != null)
    #for(x:map)
      #if(x.value&&x.value!="")
        and
        #if("name".equals(x.key) || "pinyin".equals(x.key) || "jianpin".equals(x.key))
          #(x.key) like concat('%' , #para(x.value), '%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("areaList")
SELECT code, name, area_level, direct_super, top_super, pinyin, jianpin
FROM const_area_detail
where 1=1
#if(map != null)
    #for(x:map)
      #if(x.value&&x.value!="")
        and
        #if("name".equals(x.key) || "pinyin".equals(x.key) || "jianpin".equals(x.key))
          #(x.key) like concat('%' , #para(x.value), '%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end