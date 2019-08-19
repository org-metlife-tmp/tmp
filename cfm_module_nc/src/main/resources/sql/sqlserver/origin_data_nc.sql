
#sql("getPage")
  SELECT
    nc.*,
    oam.org_name
  FROM
    nc_origin_data nc  left join (
      SELECT oam.nc_org_code, org.name as org_name
      FROM nc_org_mapping oam , organization org
      where oam.tmp_org_code = org.code
    ) oam
    ON nc.apply_org = oam.nc_org_code
  WHERE 1=1
  #if(map != null)
      #for(x : map)
          #if(x.value&&x.value!="")

            #if("status".equals(x.key) && map.status.size()==0)
              #continue
            #end

            AND
            #if("recv_acc_query_key".equals(x.key))
              (recv_acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
                or
              recv_acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              )
            #elseif("status".equals(x.key))
              interface_status in(
                #for(y : map.status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("min_amount".equals(x.key))
              amount >= convert(decimal(18,2),#para(x.value))
            #elseif("max_amount".equals(x.key))
              amount <= convert(decimal(18,2),#para(x.value))
            #elseif("org_name".equals(x.key))
              oam.org_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("apply_start_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_date,110)) >= 0
            #elseif("apply_end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),CONVERT(DATE,apply_date,110)) <= 0
            #else
              #(x.key) = #para(x.value)
            #end
          #end
      #end
  #end
  order by id desc
#end

#sql("updProcessStatus")
 update nc_origin_data set process_status = ? ,process_msg = ? where id = ?
#end

#sql("findOriginDataByOrgCode")
  SELECT
    nc_org_code,
    tmp_org_code
  FROM
	  nc_org_mapping
  WHERE nc_org_code = ?
#end