#namespace("auto_recv_counter")

  #sql("confirm_list")
    select  TOP 10 *
    from recv_counter_bill
    where pay_status = 1
    and confirm_status = 0
    and bill_type = 0
    and confrim_try_times < 5
    order by create_on asc 
  #end

  #sql("confirm_group_list")
    select *
    from recv_counter_bill
    where pay_status = 1
    and confirm_status = 0
    and bill_type = 1
    and confrim_try_times < 5
    order by create_on asc
  #end

  #sql("confirm_novoucher_list")
    select *
    from recv_counter_bill
    where confirm_status = 1
    and back_on is not null
    and is_sunvouder = 0
  #end
  
   
   
#end
