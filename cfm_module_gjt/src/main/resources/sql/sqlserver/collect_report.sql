#sql("collectSum")
select sum(collect_amount) AS total_amount
    FROM collect_execute_instruction  cei
    WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_org_id".equals(x.key))
            pay_account_org_id in(
              #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
            #elseif("pay_bank_cnaps".equals(x.key))
            pay_bank_cnaps in(
              #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                '#(y)'
              #end
            )
             #elseif("collect_status".equals(x.key))
            collect_status in(
              #for(y : map.collect_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
              DATEDIFF(day,#para(x.value),create_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end


#sql("collectByOrgList")
select 
    cei.pay_account_no ,
    cei.pay_account_bank ,
    cei.pay_account_org_id ,
    cei.pay_account_org_name ,
    sum(cei.collect_amount) AS collect_amount 
    FROM collect_execute_instruction  cei
    WHERE 1=1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_org_id".equals(x.key))
            pay_account_org_id in(
              #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
            #elseif("pay_bank_cnaps".equals(x.key))
            pay_bank_cnaps in(
              #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                '#(y)'
              #end
            )
            #elseif("collect_status".equals(x.key))
            collect_status in(
              #for(y : map.collect_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
#end
  group by  cei.pay_account_no , cei.pay_account_bank ,cei.pay_account_org_name ,cei.pay_account_org_id 
  order by   cei.pay_account_org_id  desc
#end


#sql("collectByOrgChart")
select  
    TOP 5 
    cei.pay_account_org_id ,
    cei.pay_account_org_name ,
    sum(cei.collect_amount) AS collect_amount  
    FROM collect_execute_instruction  cei
    WHERE 1 = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_org_id".equals(x.key))
            pay_account_org_id in(
              #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
            #elseif("pay_bank_cnaps".equals(x.key))
            pay_bank_cnaps in(
              #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                '#(y)'
              #end
            )
            #elseif("collect_status".equals(x.key))
            collect_status in(
              #for(y : map.collect_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
#end
  group by   cei.pay_account_org_name ,cei.pay_account_org_id 
  order by   collect_amount  desc
#end


#sql("collectByAccChart")
select 
 TOP 5 
    cei.pay_account_no ,
    cei.pay_account_name ,
    sum(cei.collect_amount) AS collect_amount 
    FROM collect_execute_instruction  cei
    WHERE 1 = 1
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("pay_account_org_id".equals(x.key))
            pay_account_org_id in(
              #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
            #elseif("pay_bank_cnaps".equals(x.key))
            pay_bank_cnaps in(
              #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                '#(y)'
              #end
            )
            #elseif("collect_status".equals(x.key))
            collect_status in(
              #for(y : map.collect_status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),create_on) <= 0
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
#end
  group by   cei.pay_account_no ,cei.pay_account_name 
  order by   collect_amount  desc
#end
    
    

    
    