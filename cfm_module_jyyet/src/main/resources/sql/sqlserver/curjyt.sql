###当日交易明细列表
#sql("curdetaillist")
SELECT
  act.id,
  act.acc_id,
  act.amount,
  case act.direction when '1' then '付' when '2' then '收' else '其他' end direction,
  act.trans_date,
  act.trans_time,
  act.summary,
  bnk.name AS bank_name ,
  bnk.province,
  bnk.city,
  cbt.name as bank_type_name,
  acc.acc_no,
  acc.acc_name,
  acc.interactive_mode,
  acc.acc_attr,
  acc.acc_attr_name,
  act.opp_acc_no,
  act.opp_acc_name,
  act.opp_acc_bank,
  acc.org_name
FROM
  acc_cur_transaction act
  JOIN
    (SELECT
      acc.*,
      cv.[value] AS acc_attr_name,
      org.name AS org_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.cat_code = 'acc_attr'
        AND cv.[key] = acc.acc_attr
      JOIN
        (SELECT DISTINCT
          org2.org_id,
          org2.name
        FROM
          organization org
          JOIN organization org2
            ON charindex(org.level_code, org2.level_code) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = act.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
  JOIN const_bank_type cbt
    ON cbt.[code] = bnk.bank_type
WHERE 1 = 1
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key)
          || "acc_no".equals(x.key)
          || "bank_type".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

        AND
        #if("org_ids".equals(x.key))
          acc.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("cnaps_codes".equals(x.key))
          bnk.cnaps_code in(
            #for(y : map.cnaps_codes)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          acc.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("interactive_modes".equals(x.key))
          acc.interactive_mode in(
            #for(y : map.interactive_modes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
       #elseif("bank_type".equals(x.key))
          bnk.#(x.key) = #para(x.value)
       #elseif("acc_no".equals(x.key))
          acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')        
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  ORDER BY act.amount desc, act.id desc
#end

###统计总交易额
#sql("curdetailsum")
  SELECT
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 1 THEN
            act.amount
          END
        )
      ),
      0.00
    ) AS totalpay,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 2 THEN
            act.amount
          END
        )
      ),
      0.00
    ) totalrecv,
    (
      isnull(
        CONVERT (
          DECIMAL (18, 2),
          SUM (
            CASE act.direction
            WHEN 2 THEN
              act.amount
            END
          )
        ),
        0.00
      )
    ) - (
      isnull(
        CONVERT (
          DECIMAL (18, 2),
          SUM (
            CASE act.direction
            WHEN 1 THEN
              act.amount
            END
          )
        ),
        0.00
      )
    ) AS totalnetrecv
  FROM
    acc_cur_transaction act
  JOIN (
    SELECT
      acc.*
    FROM
      account acc
    JOIN (
      SELECT DISTINCT
        org2.org_id
      FROM
        organization org
      JOIN organization org2 ON charindex(
        org.level_code,
        org2.level_code
      ) = 1
      WHERE
        org.org_id = #(map.org_id)
    ) org ON acc.org_id = org.org_id
  ) acc ON acc.acc_id = act.acc_id
  JOIN all_bank_info bnk ON bnk.cnaps_code = acc.bank_cnaps_code
  WHERE
    1 = 1
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key)
          || "acc_no".equals(x.key)
          || "bank_type".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

        AND
        #if("org_ids".equals(x.key))
          acc.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("cnaps_codes".equals(x.key))
          bnk.cnaps_code in(
            #for(y : map.cnaps_codes)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          acc.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("interactive_modes".equals(x.key))
          acc.interactive_mode in(
            #for(y : map.interactive_modes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
       #elseif("bank_type".equals(x.key))
          bnk.#(x.key) = #para(x.value)
       #elseif("acc_no".equals(x.key))
          acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')  
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

###按账户分组查询交易
#sql("curcollectacclist")
  SELECT
    acc.acc_id,
    acc.acc_no,
    acc.acc_name,
    CONVERT (
      DECIMAL (18, 2),
      isnull(
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        ),
        0.00
      )
    ) AS totalpay,
    CONVERT (
      DECIMAL (18, 2),
      isnull(
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        ),
        0.00
      )
    ) AS totalrecv,
    CONVERT (
      DECIMAL (18, 2),
      isnull(
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        ),
        0.00
      ) - isnull(
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        ),
        0.00
      )
    ) AS totalnetrecv
  FROM
    acc_cur_transaction act
  JOIN (
    SELECT
      acc.*
    FROM
      account acc
    JOIN (
      SELECT DISTINCT
        org2.org_id
      FROM
        organization org
      JOIN organization org2 ON charindex(
        org.level_code,
        org2.level_code
      ) = 1
      WHERE
        org.org_id = #(map.org_id)
    ) org ON acc.org_id = org.org_id
  ) acc ON acc.acc_id = act.acc_id
  JOIN all_bank_info bnk ON bnk.cnaps_code = acc.bank_cnaps_code
  WHERE
    1 = 1
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

        AND
        #if("org_ids".equals(x.key))
          acc.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("cnaps_codes".equals(x.key))
          bnk.cnaps_code in(
            #for(y : map.cnaps_codes)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          acc.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("interactive_modes".equals(x.key))
          acc.interactive_mode in(
            #for(y : map.interactive_modes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY acc.acc_id,
	         acc.acc_no,
	         acc.acc_name
  order by totalnetrecv desc, acc.acc_id desc
#end

#sql("curcollectorglist")
  SELECT
    org.org_id,
    org.code,
    org.name,
    org.address,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        )
      ),
      0.00
    ) AS totalpay,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        )
      ),
      0.00
    ) AS totalrecv,
    CONVERT (
      DECIMAL (18, 2),
      isnull(
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        ),
        0
      ) - isnull(
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        ),
        0
      )
    ) AS totalnetrecv
  FROM
    acc_cur_transaction act
  JOIN (
    SELECT
      acc.*, org.code,
      org.name,
      org.address
    FROM
      account acc
    JOIN (
      SELECT
        org2.org_id,
        org2.code,
        org2.name,
        org2.address
      FROM
        organization org
      JOIN organization org2 ON charindex(
        org.level_code,
        org2.level_code
      ) = 1
      WHERE
        org.org_id = 1
    ) org ON acc.org_id = org.org_id
  ) org ON org.acc_id = act.acc_id
  JOIN all_bank_info bnk ON bnk.cnaps_code = org.bank_cnaps_code
  WHERE
    1 = 1
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

        AND
        #if("org_ids".equals(x.key))
          org.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("cnaps_codes".equals(x.key))
          bnk.cnaps_code in(
            #for(y : map.cnaps_codes)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          org.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("interactive_modes".equals(x.key))
          org.interactive_mode in(
            #for(y : map.interactive_modes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),act.trans_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),act.trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY
    org.org_id,
    org.code,
    org.name,
    org.address
  order by org.org_id
#end

#sql("curcollectbanklist")
  SELECT
    bnk.name,
    bnk.cnaps_code,
    bnk.address,
    bnk.province,
    bnk.city,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        )
      ),
      0.00
    ) AS totalpay,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        )
      ),
      0.00
    ) AS totalrecv,
    CONVERT (
      DECIMAL (18, 2),
      isnull(
        SUM (
          CASE act.direction
          WHEN 2 THEN
            amount
          END
        ),
        0
      ) - isnull(
        SUM (
          CASE act.direction
          WHEN 1 THEN
            amount
          END
        ),
        0
      )
    ) AS totalnetrecv
  FROM
    acc_cur_transaction act
  JOIN (
    SELECT
      acc.*, org.code,
      org.name,
      org.address
    FROM
      account acc
    JOIN (
      SELECT
        org2.org_id,
        org2.code,
        org2.name,
        org2.address
      FROM
        organization org
      JOIN organization org2 ON charindex(
        org.level_code,
        org2.level_code
      ) = 1
      WHERE
        org.org_id = #(map.org_id)
    ) org ON acc.org_id = org.org_id
  ) org ON act.acc_id = org.acc_id
  JOIN all_bank_info bnk ON bnk.cnaps_code = org.bank_cnaps_code
  WHERE
    1 = 1
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

        AND
        #if("org_ids".equals(x.key))
          org.org_id in(
            #for(y : map.org_ids)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("cnaps_codes".equals(x.key))
          bnk.cnaps_code in(
            #for(y : map.cnaps_codes)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          org.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              '#(y)'
            #end
          )
        #elseif("interactive_modes".equals(x.key))
          org.interactive_mode in(
            #for(y : map.interactive_modes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),act.trans_date) >= 0
        #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),act.trans_date) <= 0
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY
    bnk.name,
    bnk.cnaps_code,
    bnk.address,
    bnk.province,
    bnk.city
  order by bnk.cnaps_code
#end