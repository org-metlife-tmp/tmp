###
#sql("curdetaillist")
SELECT
  acb.acc_id,
  acc.acc_no,
  acc.acc_name,
  acc.acc_attr,
  acc.acc_attr_name,
  acc.open_date,
  acc.interactive_mode,
  acb.bal,
  acb.bal_date,
  acb.import_time,
  bnk.`name` AS bank_name,
  cbt.name as bank_type_name,
  acc.org_name
FROM
  `acc_cur_balance` acb
  JOIN
    (SELECT
      acc.*,
      cv.value AS acc_attr_name,
      org.name AS org_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.`cat_code` = 'acc_attr'
        AND cv.`key` = acc.`acc_attr`
      JOIN
        (SELECT DISTINCT
          org2.org_id,
          org2.name
        FROM
          organization org
          JOIN organization org2
            ON LOCATE(
              org.`level_code`,
              org2.`level_code`
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = acb.acc_id
  JOIN `all_bank_info` bnk
    ON bnk.`cnaps_code` = acc.`bank_cnaps_code`
  JOIN `const_bank_type` cbt
    ON cbt.`code` = bnk.`bank_type`
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
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like concat('%', #para(x.value), '%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    ORDER BY acb.bal desc
#end

#sql("cursum")
SELECT
  IFNULL(TRUNCATE(SUM(bal),2),0.00) AS bal
FROM
  `acc_cur_balance` acb
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
    ON acc.acc_id = acb.acc_id
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
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like concat('%', #para(x.value), '%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
#end

#sql("curcollectorglist")
SELECT
  acc.org_id,
  acc.code,
  acc.name,
  acc.address,
  IFNULL(TRUNCATE(SUM(acb.bal),2),0.00) AS bal
FROM
  `acc_cur_balance` acb
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
    ON acc.acc_id = acb.acc_id
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
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like concat('%', #para(x.value), '%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY acc.org_id
    ORDER BY bal desc
#end

#sql("curcollectbanklist")
SELECT
  bnk.name,
  bnk.address,
  bnk.cnaps_code,
  bnk.province,
  bnk.city,
  IFNULL(TRUNCATE(SUM(acb.bal),2),0.00) AS bal
FROM
  `acc_cur_balance` acb
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
    ON acc.acc_id = acb.acc_id
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
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like concat('%', #para(x.value), '%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY bnk.cnaps_code
    ORDER BY acb.bal desc
#end

#sql("curwavetopchart")
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
  `acc_cur_balance_wave` acb
  JOIN account acc
    ON acc.acc_id = acb.acc_id
  where acb.acc_id = ?
    AND DATE_FORMAT(acb.bal_date,'%Y-%m-%d') =  DATE_FORMAT(now(),'%Y-%m-%d')
    ORDER BY acb.bal_date asc,acb.import_time asc

#end