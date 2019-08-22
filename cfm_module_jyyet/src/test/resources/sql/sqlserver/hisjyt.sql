###当日交易明细列表
#sql("hisdetaillist")
SELECT
  act.id,
  act.acc_id,
  act.amount,
  act.direction,
  act.trans_time,
  act.summary,
  bnk.name AS bank_name,
  acc.acc_no,
  acc.acc_name,
  acc.interactive_mode,
  acc.acc_attr,
  acc.acc_attr_name,
  act.opp_acc_no,
  act.opp_acc_name,
  act.opp_acc_bank
FROM
  acc_his_transaction act
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
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
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
#sql("hisdetailsum")
SELECT DISTINCT
  (SELECT
    isnull(convert(decimal(18,2),SUM(act.amount)), 0.00)
  FROM
    acc_his_transaction act
  WHERE act.direction = 1) AS totalpay,
  (SELECT
    isnull(convert(decimal(18,2),SUM(act.amount)), 0.00)
  FROM
    acc_his_transaction act
  WHERE act.direction = 2) totalrecv,
  (
    (SELECT
      isnull(convert(decimal(18,2),SUM(act.amount)), 0.00)
    FROM
      acc_his_transaction act
    WHERE act.direction = 1) -
    (SELECT
      isnull(convert(decimal(18,2),SUM(act.amount)), 0.00)
    FROM
      acc_his_transaction act
    WHERE act.direction = 2)
  ) AS totalnetrecv
FROM
  acc_his_transaction act
  JOIN
    (SELECT
      acc.*
    FROM
      account acc
      JOIN
        (SELECT DISTINCT
          org2.org_id
        FROM
          organization org
          JOIN organization org2
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
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
#end

###按账户分组查询交易
#sql("hiscollectacclist")
SELECT
  acc.acc_id,
  acc.acc_no,
  acc.acc_name,
  convert(decimal(18,2),isnull(sum(pay.totalpay), 0)) AS totalpay,
  convert(decimal(18,2),isnull(sum(recv.totalrecv), 0)) AS totalrecv,
  convert(decimal(18,2),isnull(sum(recv.totalrecv), 0) - isnull(sum(pay.totalpay), 0)) AS totalnetrecv
FROM
  acc_his_transaction act
  LEFT JOIN
    (SELECT
      aht.acc_id,
      isnull(convert(decimal(18,2),SUM(aht.amount)), 0.00) AS totalpay
    FROM
      acc_his_transaction aht
    WHERE aht.direction = 1
    GROUP BY aht.acc_id) pay
    ON act.acc_id = pay.acc_id
  LEFT JOIN
    (SELECT
      aht.acc_id,
      isnull(convert(decimal(18,2),SUM(aht.amount)), 0.00) AS totalrecv
    FROM
      acc_his_transaction aht
    WHERE aht.direction = 2
    GROUP BY aht.acc_id) recv
    ON act.acc_id = recv.acc_id
    JOIN
    (SELECT
      acc.*
    FROM
      account acc
      JOIN
        (SELECT DISTINCT
          org2.org_id
        FROM
          organization org
          JOIN organization org2
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
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
  GROUP BY
    acc.acc_id,
    acc.acc_no,
    acc.acc_name
  ORDER BY
    acc.acc_id,
    acc.acc_no,
    acc.acc_name
#end

#sql("hiscollectorglist")
SELECT
  acc.org_id,
  acc.code,
  acc.name,
  acc.address,
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
      ON acc.org_id = org.org_id) acc
  LEFT JOIN acc_his_transaction pay
    ON pay.acc_id = acc.acc_id
    AND pay.direction = 1
  LEFT JOIN acc_his_transaction recv
    ON recv.acc_id = acc.acc_id
    AND recv.direction = 2
    JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
WHERE EXISTS
  (SELECT
    1
  FROM
    acc_his_transaction act
  WHERE act.acc_id = acc.acc_id)
  #if(map.size() > 1)
    #for(x : map)
      #if(x.value && x.value != "")
        #if("org_id".equals(x.key))
          #continue
        #end
        AND

        #if("org_ids".equals(x.key)
          || "cnaps_codes".equals(x.key)
          || "acc_attrs".equals(x.key)
          || "interactive_modes".equals(x.key))
          #if("null".equals(x.value.toString()) || "[]".equals(x.value.toString()))
            #continue
          #end
        #end

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
    acc.org_id,
    acc.code,
    acc.name,
    acc.address
  ORDER BY
    acc.org_id,
    acc.code,
    acc.name,
    acc.address
#end

#sql("hiscollectbanklist")
SELECT
  bnk.name,
  bnk.cnaps_code,
  bnk.address,
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
      ON acc.org_id = org.org_id) acc
  LEFT JOIN acc_his_transaction pay
    ON pay.acc_id = acc.acc_id
    AND pay.direction = 1
  LEFT JOIN acc_his_transaction recv
    ON recv.acc_id = acc.acc_id
    AND recv.direction = 2
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
WHERE EXISTS
  (SELECT
    1
  FROM
    acc_his_transaction act
  WHERE act.acc_id = acc.acc_id)
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
  order by
    bnk.name,
    bnk.cnaps_code,
    bnk.address
#end

#sql("hiswavelist")
  SELECT
    act.acc_id,
    acc.acc_no,
    acc.acc_name,
    acc.org_name,
    CONVERT (
      DECIMAL (18, 2),
      SUM (isnull(act.pay_amount, 0))
    ) pay,
    CONVERT (
      DECIMAL (18, 2),
      SUM (isnull(act.recv_amount, 0))
    ) recv,
    CONVERT (
      DECIMAL (18, 2),
      SUM (isnull(act.recv_amount, 0)) - SUM (isnull(act.pay_amount, 0))
    ) AS netrecv
  FROM
    acc_data_report act
  JOIN (
    SELECT
      acc.*, org.org_name
    FROM
      account acc
    JOIN (
      SELECT DISTINCT
        org2.org_id,
        org2.name AS org_name
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
              "#(y)"
            #end
          )
        #elseif("acc_attrs".equals(x.key))
          acc.acc_attr in(
            #for(y : map.acc_attrs)
              #if(for.index > 0)
                #(",")
              #end
              "#(y)"
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
          DATE_FORMAT(act.import_time,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #elseif("end_date".equals(x.key))
          DATE_FORMAT(act.import_time,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  GROUP BY
    act.acc_id,
    acc.acc_no,
    acc.acc_name,
    acc.org_name
#end

#sql("hiswavetopchart")
SELECT
  acb.acc_id,
  acc.acc_no,
  acc.acc_name,
  acc.acc_attr,
  acc.open_date,
  acc.interactive_mode,
  acb.pay_amount,
  acb.recv_amount,
  acb.statistics_date,
  acb.import_time
FROM
  acc_data_report acb
  JOIN account acc
    ON acc.acc_id = acb.acc_id
  WHERE acb.acc_id = ?
    AND DATEDIFF(day,CONVERT(DATE,?,110),CONVERT(DATE,acb.statistics_date,110)) >= 0
    AND DATEDIFF(day,CONVERT(DATE,?,110),CONVERT(DATE,acb.statistics_date,110)) <= 0
    ORDER BY acb.statistics_date ASC,acb.import_time ASC
#end