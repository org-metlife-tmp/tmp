#sql("getAccTotalAmount")
select
	sum( allocation_amount ) as sum_amount
from
	allocation_execute_instruction aei,organization org
where aei.pay_account_org_id = org.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),create_on) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),create_on) <= 0
        #elseif("pay_account_org_id".equals(x.key))
          #(x.key) in (
            #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
        #elseif("pay_bank_cnaps".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
        #elseif("level_code".equals(x.key))
          org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #else
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findAccAlloExcuteInsTopchar")
select top 5
	aei.pay_account_no,
	aei.pay_account_name,
	sum(aei.allocation_amount) as amount
from
	allocation_execute_instruction aei,organization org
where aei.pay_account_org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("pay_account_org_id".equals(x.key))
            #(x.key) in (
              #for(y : map.pay_account_org_id)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("pay_bank_cnaps".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
group by
	aei.pay_account_no,
	aei.pay_account_name
order by
	amount desc
#end

#sql("findAccAlloExcuteInsList")
select
	aei.pay_account_org_name,
	aei.pay_account_bank,
	aei.pay_account_no,
	aei.pay_account_name,
	sum( aei.allocation_amount ) as amount
from allocation_execute_instruction aei,organization org
where aei.pay_account_org_id = org.org_id
  #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("pay_account_org_id".equals(x.key))
            #(x.key) in (
              #for(y : map.pay_account_org_id)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("pay_bank_cnaps".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
group by aei.pay_account_org_name,aei.pay_account_bank,aei.pay_account_no,aei.pay_account_name
order by
	amount desc
#end

#sql("getOrgTotalAmount")
select
	sum(allocation_amount) as sum_amount
from
	allocation_execute_instruction aei,
	organization org
where
	aei.pay_account_org_id = org.org_id
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("pay_account_org_id".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_account_org_id)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
          #elseif("pay_bank_cnaps".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
          #elseif("pay_bank_cnaps".equals(x.key))
             #(x.key) in (
              #for(y : map.pay_bank_cnaps)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end

#sql("findOrgAlloExcuteInsTopchar")
select top 5
	cei.pay_account_org_id,
	cei.pay_account_org_name,
	sum( allocation_amount ) as amount
from
	allocation_execute_instruction cei,
	organization org
where
	cei.pay_account_org_id = org.org_id
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("pay_account_org_id".equals(x.key))
            #(x.key) in (
              #for(y : map.pay_account_org_id)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("pay_bank_cnaps".equals(x.key))
             #(x.key) in (
              #for(y : map.pay_bank_cnaps)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
group by cei.pay_account_org_id,cei.pay_account_org_name
order by amount desc
#end

#sql("findOrgAlloExcuteInsList")
select
	cei.pay_account_org_id,
	cei.pay_account_org_name,
	cei.pay_account_bank,
	cei.pay_account_no,
	cei.pay_account_name,
	sum( allocation_amount ) as amount
from
	allocation_execute_instruction cei,
	organization org
where
	cei.pay_account_org_id = org.org_id
#if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) >= 0
          #elseif("end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),create_on) <= 0
          #elseif("pay_account_org_id".equals(x.key))
            #(x.key) in (
              #for(y : map.pay_account_org_id)
                  #if(for.index > 0)
                    #(",")
                  #end
                  #(y)
              #end
            )
          #elseif("pay_bank_cnaps".equals(x.key))
           #(x.key) in (
            #for(y : map.pay_bank_cnaps)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
            #end
          )
          #elseif("level_code".equals(x.key))
            org.level_code like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #else
            #(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
group by cei.pay_account_org_id,cei.pay_account_org_name,cei.pay_account_bank,cei.pay_account_no,cei.pay_account_name
order by amount desc
#end

#sql("getAccListByOrgId")
  select
	cei.pay_account_no,
	cei.pay_account_name,
	cei.pay_account_bank,
	cei.allocation_amount as amount
from
	allocation_execute_instruction cei
where
	cei.pay_account_org_id = ?
order by
	allocation_amount desc
#end