###
#sql("hisdetaillist")
SELECT
  ahb.acc_id,
  acc.acc_no,
  acc.acc_name,
  acc.acc_attr,
  acc.acc_attr_name,
  acc.open_date,
  acc.interactive_mode,
  ahb.bal,
  ahb.bal_date,
  ahb.import_time,
  bnk.`name` AS bank_name
FROM
  `acc_his_balance` ahb
  JOIN
    (SELECT
      acc.*,
      cv.value AS acc_attr_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.`cat_code` = 'acc_attr'
        AND cv.`key` = acc.`acc_attr`
      JOIN
        (SELECT DISTINCT
          org2.org_id
        FROM
          organization org
          JOIN organization org2
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
    where 1=1
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
            DATE_FORMAT(bal_date,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(bal_date,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    ORDER BY ahb.bal desc
#end

#sql("hissum")
SELECT
  IFNULL(TRUNCATE(SUM(bal),2),0.00) AS bal
FROM
  `acc_his_balance` ahb
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
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
    where 1=1
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
            DATE_FORMAT(bal_date,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(bal_date,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
#end

#sql("hiscollectorglist")
SELECT
  acc.org_id,
  acc.code,
  acc.name,
  acc.address,
  IFNULL(TRUNCATE(SUM(ahb.bal),2),0.00) AS bal
FROM
  `acc_his_balance` ahb
  JOIN
    (SELECT
      acc.*,
      org.code,
      org.name,
      org.address
    FROM
      account acc
      JOIN
        (SELECT DISTINCT
          org2.*
        FROM
          organization org
          JOIN organization org2
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
    where 1=1
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
            DATE_FORMAT(bal_date,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(bal_date,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY acc.org_id
    ORDER BY ahb.bal desc
#end

#sql("hiscollectbanklist")
SELECT
  bnk.name,
  bnk.address,
  bnk.cnaps_code,
  IFNULL(TRUNCATE(SUM(ahb.bal),2),0.00) AS bal
FROM
  `acc_his_balance` ahb
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
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
    where 1=1
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
            DATE_FORMAT(bal_date,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(bal_date,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY bnk.cnaps_code
    ORDER BY ahb.bal desc
#end

#sql("hiswavelist")
SELECT
  ahb.acc_id,
  acc.acc_no,
  acc.acc_name,
  acc.acc_attr,
  acc.acc_attr_name,
  acc.open_date,
  acc.interactive_mode,
  ahb.bal_date,
  ahb.import_time,
  bnk.`name` AS bank_name,
  acc.org_name,
  AVG(ahb.bal) AS bal,
  AVG(ahb.available_bal) AS available_bal,
  AVG(ahb.frz_amt) AS frz_amt
FROM
  `acc_his_balance` ahb
  JOIN
    (SELECT
      acc.*,
      cv.value AS acc_attr_name,
      org.org_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.`cat_code` = 'acc_attr'
        AND cv.`key` = acc.`acc_attr`
      JOIN
        (SELECT DISTINCT
          org2.org_id,
          org2.name as org_name
        FROM
          organization org
          JOIN organization org2
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
  where 1=1
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
            DATE_FORMAT(ahb.bal_date,'%Y-%m-%d') >=  DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #elseif("end_date".equals(x.key))
            DATE_FORMAT(ahb.bal_date,'%Y-%m-%d') <= DATE_FORMAT(#para(x.value),'%Y-%m-%d')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY ahb.acc_id,acc.acc_no,acc.acc_name
    ORDER BY ahb.bal desc
#end

#sql("hiswavetopchart")
SELECT
  acb.acc_id,
  acc.acc_no,
  acc.acc_name,
  acc.acc_attr,
  acc.open_date,
  acc.interactive_mode,
  acb.bal,
  acb.bal_date,
  acb.import_time
FROM
  `acc_his_balance` acb
  JOIN account acc
    ON acc.acc_id = acb.acc_id
  where acb.acc_id = ?
    AND DATE_FORMAT(acb.bal_date,'%Y-%m-%d') >=  DATE_FORMAT(?,'%Y-%m-%d')
    AND DATE_FORMAT(acb.bal_date,'%Y-%m-%d') <=  DATE_FORMAT(?,'%Y-%m-%d')
    ORDER BY acb.bal_date asc,acb.import_time asc
#end