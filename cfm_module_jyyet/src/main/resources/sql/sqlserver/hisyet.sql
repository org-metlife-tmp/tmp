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
  CONVERT(varchar(100), ahb.import_time, 20) as import_time,
  bnk.name AS bank_name ,
  bnk.province,
  bnk.city,
  cbt.name as bank_type_name,
  acc.org_name
FROM
  acc_his_balance ahb
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
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
  JOIN const_bank_type cbt
    ON cbt.[code] = bnk.bank_type
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
            DATEDIFF(day,#para(x.value),bal_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bal_date) <= 0
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    ORDER BY ahb.bal desc, ahb.acc_id
#end

#sql("hissum")
SELECT
  isnull(convert(decimal(18,2),SUM(bal)),0.00) AS bal
FROM
  acc_his_balance ahb
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
    ON acc.acc_id = ahb.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
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
            DATEDIFF(day,#para(x.value),bal_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bal_date) <= 0
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
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
  isnull(convert(decimal(18,2),SUM(ahb.bal)),0.00) AS bal
FROM
  acc_his_balance ahb
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
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
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
            DATEDIFF(day,#para(x.value),bal_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bal_date) <= 0
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY acc.org_id,
             acc.code,
             acc.name,
             acc.address
    order by bal desc, acc.org_id
#end

#sql("hiscollectbanklist")
SELECT
  bnk.name,
  bnk.address,
  bnk.cnaps_code,
  bnk.province,
  bnk.city,
  isnull(convert(decimal(18,2),SUM(ahb.bal)),0.00) AS bal
FROM
  acc_his_balance ahb
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
    ON acc.acc_id = ahb.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
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
            DATEDIFF(day,#para(x.value),bal_date) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),bal_date) <= 0
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY bnk.name,
             bnk.address,
             bnk.cnaps_code,
             bnk.province,
             bnk.city
    order by bal desc, bnk.cnaps_code
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
  bnk.name AS bank_name,
  bnk.province,
  bnk.city,
  acc.org_name,
  cbt.name as bank_type_name,
  AVG(ahb.bal) AS bal,
  CONVERT (DECIMAL (18, 2),AVG(ahb.available_bal)) AS available_bal,
  AVG(ahb.frz_amt) AS frz_amt
FROM
  acc_his_balance ahb
  JOIN
    (SELECT
      acc.*,
      cv.[value] AS acc_attr_name,
      org.org_name
    FROM
      account acc
      LEFT JOIN category_value cv
        ON cv.cat_code = 'acc_attr'
        AND cv.[key] = acc.acc_attr
      JOIN
        (SELECT DISTINCT
          org2.org_id,
          org2.name as org_name
        FROM
          organization org
          JOIN organization org2
            ON charindex(
              org.level_code,
              org2.level_code
            ) = 1
        WHERE org.org_id = #(map.org_id)) org
        ON acc.org_id = org.org_id) acc
    ON acc.acc_id = ahb.acc_id
  JOIN all_bank_info bnk
    ON bnk.cnaps_code = acc.bank_cnaps_code
  JOIN const_bank_type cbt
    ON cbt.[code] = bnk.bank_type
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
            DATEDIFF(day,#para(x.value),CONVERT(DATE,ahb.bal_date,110)) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),CONVERT(DATE,ahb.bal_date,110)) <= 0
          #elseif("bank_type".equals(x.key))
            bnk.#(x.key) = #para(x.value)
          #elseif("acc_no".equals(x.key))
            acc.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
      #end
    #end
    GROUP BY ahb.acc_id,
             acc.acc_no,
             acc.acc_name,
             acc.acc_attr,
             acc.acc_attr_name,
             acc.open_date,
             acc.interactive_mode,
             bnk.province,
	           bnk.city,
             bnk.name,
             acc.org_name,
             cbt.name
    ORDER BY bal desc, ahb.acc_id
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
  acc_his_balance acb
  JOIN account acc
    ON acc.acc_id = acb.acc_id
  where acb.acc_id = ?
    AND DATEDIFF(day,?,CONVERT(DATE,acb.bal_date,110)) >= 0
    AND DATEDIFF(day,?,CONVERT(DATE,acb.bal_date,110)) <= 0
    ORDER BY acb.bal_date asc,acb.import_time asc
#end

#sql("initialBalance")
SELECT  
   acb.acc_id,
   acb.acc_no, 
   acb.acc_name,
   acc.acc_attr,
   acc.open_date,
   acc.interactive_mode,
   acb.bal    ,
   acb.bal_date,
   acb.import_time
   FROM  acc_his_balance acb 
   JOIN account acc
   ON acc.acc_id = acb.acc_id
   where  acb.acc_id = ?
   AND   DATEDIFF(day,CONVERT(DATE,dateadd(day, -1, GETDATE()),110),CONVERT(DATE,acb.bal_date,110)) = 0
   order by  acb.bal_date desc 
#end

