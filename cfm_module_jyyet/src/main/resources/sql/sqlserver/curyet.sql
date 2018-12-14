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
  CONVERT(varchar(100), acb.import_time, 20) as import_time,
  bnk.name AS bank_name,
  cbt.name as bank_type_name,
  acc.org_name,
  (
		SELECT
			TOP 1 ahb.bal
		FROM
			acc_his_balance ahb
		WHERE
			ahb.acc_id = acb.acc_id
		AND DATEDIFF(
			DAY,
			ahb.bal_date,
			GETDATE() - 1
		) = 0
		ORDER BY
			id DESC
	) AS hisbal
FROM
  acc_cur_balance acb
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
    ON acc.acc_id = acb.acc_id
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
    ORDER BY acb.bal desc, acb.acc_id desc
#end

#sql("cursum")
SELECT
  isnull(convert(decimal(18,2),SUM(bal)),0.00) AS bal
FROM
  acc_cur_balance acb
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
    ON acc.acc_id = acb.acc_id
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

#sql("curcollectorglist")
SELECT
  acc.org_id,
  acc.code,
  acc.name,
  acc.address,
  isnull(convert(decimal(18,2),SUM(acb.bal)),0.00) AS bal
FROM
  acc_cur_balance acb
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
    ON acc.acc_id = acb.acc_id
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
    ORDER BY bal desc, acc.org_id
#end

#sql("curcollectbanklist")
SELECT
  bnk.name,
  bnk.address,
  bnk.cnaps_code,
  isnull(convert(decimal(18,2),SUM(acb.bal)),0.00) AS bal
FROM
  acc_cur_balance acb
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
    ON acc.acc_id = acb.acc_id
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
             bnk.cnaps_code
    ORDER BY bal desc, bnk.cnaps_code
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
  acc_cur_balance_wave acb
  JOIN account acc
    ON acc.acc_id = acb.acc_id
  where acb.acc_id = ?
    AND DATEDIFF(day,CONVERT(date,acb.bal_date,110),CONVERT(DATE,#[[GETDATE()]]#,110)) = 0
    ORDER BY acb.bal_date asc,acb.import_time asc

#end

#sql("accountInfo")
SELECT
	acc_id,
	acc_no,
	acc_name,
	left(bank_cnaps_code, 3) as bank_type 
from 
	account
where
	acc_no in(
    #for(x : accNoList)
      #if(for.index > 0)
        #(",")
      #end
      '#(x)'
    #end
	)
#end

#sql("deleteBalenceWave")
DELETE FROM acc_cur_balance_wave
where
	acc_no in(
	    #for(x : accNoList)
	      #if(for.index > 0)
	        #(",")
	      #end
	      '#(x)'
	    #end
	)
#end