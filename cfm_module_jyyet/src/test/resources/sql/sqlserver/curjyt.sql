###当日交易明细列表
#sql("curdetaillist")
SELECT
  act.id,
  act.acc_id,
  act.amount,
  act.direction,
  act.trans_date,
  act.trans_time,
  act.summary,
  bnk.name AS bank_name,
  bnk.province,
  bnk.city,
  acc.acc_no,
  acc.acc_name,
  acc.interactive_mode,
  acc.acc_attr,
  acc.acc_attr_name,
  act.opp_acc_no,
  act.opp_acc_name,
  act.opp_acc_bank
FROM
  acc_cur_transaction act
  JOIN
    (SELECT
      acc.*,
      cv.[value] AS acc_attr_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.cat_code = 'acc_attr'
        AND cv.[key] = acc.acc_attr
      JOIN
        (SELECT DISTINCT
          org2.org_id
        FROM
          organization org
          JOIN organization org2
            ON charindex(org.level_code, org2.level_code) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = act.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
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
  ORDER BY act.amount desc
#end

###统计总交易额
#sql("curdetailsum")
  SELECT DISTINCT
    (
      SELECT
        isnull(
          CONVERT (
            DECIMAL (18, 2),
            SUM (act.amount)
          ),
          0.00
        )
      FROM
        acc_cur_transaction act
      WHERE
        act.direction = 1
    ) AS totalpay,
    (
      SELECT
        isnull(
          CONVERT (
            DECIMAL (18, 2),
            SUM (act.amount)
          ),
          0.00
        )
      FROM
        acc_cur_transaction act
      WHERE
        act.direction = 2
    ) totalrecv,
    (
      (
        SELECT
          isnull(
            CONVERT (
              DECIMAL (18, 2),
              SUM (act.amount)
            ),
            0.00
          )
        FROM
          acc_cur_transaction act
        WHERE
          act.direction = 1
      ) - (
        SELECT
          isnull(
            CONVERT (
              DECIMAL (18, 2),
              SUM (act.amount)
            ),
            0.00
          )
        FROM
          acc_cur_transaction act
        WHERE
          act.direction = 2
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
#end

###按账户分组查询交易
#sql("curcollectacclist")
  SELECT
    acc.acc_id,
    acc.acc_no,
    acc.acc_name,
    CONVERT (
      DECIMAL (18, 2),
      isnull(sum(pay.totalpay), 0)
    ) AS totalpay,
    CONVERT (
      DECIMAL (18, 2),
      isnull(sum(recv.totalrecv), 0)
    ) AS totalrecv,
    CONVERT (
      DECIMAL (18, 2),
      isnull(sum(recv.totalrecv), 0) - isnull(sum(pay.totalpay), 0)
    ) AS totalnetrecv
  FROM
    acc_cur_transaction act
  LEFT JOIN (
    SELECT
      aht.acc_id,
      isnull(
        CONVERT (
          DECIMAL (18, 2),
          SUM (aht.amount)
        ),
        0.00
      ) AS totalpay
    FROM
      acc_cur_transaction aht
    WHERE
      aht.direction = 1
    GROUP BY
      aht.acc_id
  ) pay ON act.acc_id = pay.acc_id
  LEFT JOIN (
    SELECT
      aht.acc_id,
      isnull(
        CONVERT (
          DECIMAL (18, 2),
          SUM (aht.amount)
        ),
        0.00
      ) AS totalrecv
    FROM
      acc_cur_transaction aht
    WHERE
      aht.direction = 2
    GROUP BY
      aht.acc_id
  ) recv ON act.acc_id = recv.acc_id
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
        org.org_id = 1
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
  GROUP BY
    acc.acc_id,
    acc.acc_no,
    acc.acc_name
#end

#sql("curcollectorglist")
SELECT
  org.org_id,
  org.code,
  org.name,
  org.address,
  isnull(convert(decimal(18,2),SUM(pay.amount)), 0.00) AS totalpay,
  isnull(convert(decimal(18,2),SUM(recv.amount)), 0.00) AS totalrecv,
  convert(
    decimal(18,2),
    isnull(SUM(recv.amount), 0) - isnull(SUM(pay.amount), 0)
  ) AS totalnetrecv
FROM
  (SELECT
    acc.*,
    org.code,
    org.name,
    org.address
  FROM
    account acc
    JOIN
      (SELECT
        org2.org_id,
        org2.code,
        org2.name,
        org2.address
      FROM
        organization org
        JOIN organization org2
          ON charindex(
            org.level_code,
            org2.level_code
          ) = 1
      WHERE org.org_id = #(map.org_id)) org
      ON acc.org_id = org.org_id) org
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = org.bank_cnaps_code
  LEFT JOIN acc_cur_transaction pay
    ON pay.acc_id = org.acc_id
    AND pay.direction = 1
  LEFT JOIN acc_cur_transaction recv
    ON recv.acc_id = org.acc_id
    AND recv.direction = 2
WHERE EXISTS
  (SELECT
    1
  FROM
    acc_cur_transaction act
  WHERE act.acc_id = org.acc_id)
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
          (
            DATEDIFF(day,#para(x.value),pay.trans_date) >= 0
            or
            DATEDIFF(day,#para(x.value),recv.trans_date) >= 0
          )
        #elseif("end_date".equals(x.key))
          (
            DATEDIFF(day,#para(x.value),pay.trans_date) <= 0
            or
            DATEDIFF(day,#para(x.value),recv.trans_date) <= 0
          )
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY org.org_id,org.code,org.name,org.address
#end

#sql("curcollectbanklist")
  SELECT
    bnk.name,
    bnk.cnaps_code,
    bnk.address,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (pay.amount)
      ),
      0.00
    ) AS totalpay,
    isnull(
      CONVERT (
        DECIMAL (18, 2),
        SUM (recv.amount)
      ),
      0.00
    ) AS totalrecv,
    CONVERT (
      DECIMAL (18, 2),
      isnull(SUM(recv.amount), 0) - isnull(SUM(pay.amount), 0)
    ) AS totalnetrecv
  FROM
    (
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
    ) org
  LEFT JOIN acc_cur_transaction pay ON pay.acc_id = org.acc_id
  AND pay.direction = 1
  LEFT JOIN acc_cur_transaction recv ON recv.acc_id = org.acc_id
  AND recv.direction = 2
  JOIN all_bank_info bnk ON bnk.cnaps_code = org.bank_cnaps_code
  WHERE
    EXISTS (
      SELECT
        1
      FROM
        acc_cur_transaction act
      WHERE
        act.acc_id = org.acc_id
    )
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
          (
            DATEDIFF(day,#para(x.value),pay.trans_date) >= 0
            or
            DATEDIFF(day,#para(x.value),recv.trans_date) >= 0
          )
        #elseif("end_date".equals(x.key))
          (
            DATEDIFF(day,#para(x.value),pay.trans_date) <= 0
            or
            DATEDIFF(day,#para(x.value),recv.trans_date) <= 0
          )
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY
      bnk.name,
      bnk.cnaps_code,
      bnk.address
#end