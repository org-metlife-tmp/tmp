#namespace("common")
  #sql("category")
    SELECT
      ct.code,
      ct.`desc`,
      cv.`key` AS item_code,
      cv.`value`
      AS item_desc
    FROM
    category_type ct,
    category_value cv
    WHERE
    cv.cat_code = ct.code
    ORDER BY ct.code
  #end
#end