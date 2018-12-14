#sql("list")
  SELECT
    t.*, ti.gyl_allocation_time
  FROM
    gyl_allocation_topic t
  JOIN (
    SELECT
      MAX (id) AS m_id,
      gyl_allocation_id
    FROM
      gyl_allocation_timesetting ti
    GROUP BY
      ti.gyl_allocation_id
  ) tid ON tid.gyl_allocation_id = t.id
  JOIN gyl_allocation_timesetting ti ON ti.id = tid.m_id
  where t.delete_flag = 0
   and t.service_status = 4
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
            #if("service_status".equals(x.key) && map.service_status.size()==0)
              #continue
            #end

            #if("is_activity".equals(x.key) && map.is_activity.size()==0)
              #continue
            #end
          AND

            #if("topic".equals(x.key))
              t.topic like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            #elseif("service_status".equals(x.key))
              t.service_status in(
                #for(y : map.service_status)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("is_activity".equals(x.key))
              t.is_activity in(
                #for(y : map.is_activity)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
                #end
              )
            #elseif("pay_acc_query_key".equals(x.key))
            (
              t.pay_acc_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
              or
              t.pay_acc_name like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
            )
            #else
              t.#(x.key) = #para(x.value)
            #end
        #end
    #end
  #end
#end