#namespace("common")
  #sql("category")
    SELECT
      ct.code,
      ct.[desc],
      cv.[key] AS item_code,
      cv.[value]
      AS item_desc
    FROM
    category_type ct,
    category_value cv
    WHERE
    cv.cat_code = ct.code
    ORDER BY ct.code
  #end

  #sql("findFiles")
    select * from common_attachment_info_ref where biz_type = ? and bill_id = ?
  #end

  #sql("updateQuartz")
    update cfm_quartz set enable = ?,is_need_scaned=1 where groups = ?
  #end

  #sql("update")
    update
      #(map.table_name)
    set
      #for(x : map.set)
        #if(for.index > 0)
          #(",")
        #end
        [#(x.key)] = #para(x.value)
      #end
      where
      #for(y : map.where)
        #if(for.index > 0)
          and
        #end
        [#(y.key)] = #para(y.value)
      #end
  #end
  
 #sql("getQueue")
    SELECT
      id
    FROM
    single_pay_instr_queue
    WHERE
    bill_id = ? and ref_attr = ?
 #end

#end