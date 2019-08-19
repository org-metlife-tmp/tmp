  #sql("allCatgList")
    select ct.code, ct.desc , cv.key, cv.value
    from category_type ct, category_value cv
    where cv.cat_code = ct.code
    #if(map != null)
      #for(x:map)
        #if(x.value&&x.value!="")
          and ct.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
