#sql("updateLaOriginData")
   update
   la_origin_pay_data
   set 
   tmp_status = 1
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


#sql("updateEbsOriginData")
   update
   ebs_origin_pay_data
   set 
   tmp_status = 1,
   paybankaccno = ?,
   paybankcode = ? ,
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


#sql("selectLaOriginData")
   select
    *
   from
   la_origin_pay_data
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end

#sql("selectEbsOriginData")
   select
    *
   from
   ebs_origin_pay_data
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


#sql("findDiskBackingList")
 select 
      pay_master.id AS pay_master_id ,
      pay_master.master_batchno,
      case pay_master.source_sys when '0' then 'LA' when '1' then 'EBS' else '其他' end source_sys,
      pay_master.channel_id,
      pay_master.org_id,
      pay_master.dept_id ,
      pay_master.total_num AS pay_master_total_num ,
      pay_master.total_amount AS pay_master_total_amount ,
      pay_master.delete_flag ,
      pay_master.process_bank_type ,
      pay_master.is_checked ,
      pay_master.pay_acc_no ,
      pay_master.pay_acc_name ,
      pay_master.pay_bank_name ,
      pay_master.create_by ,
      pay_master.create_on ,
      pay_master.update_by ,
      pay_master.update_on ,
      pay_master.persist_version ,
      pay_master.service_status ,
      pay.id AS pay_id ,
      pay.child_batchno ,
      pay.total_num AS pay_total_num ,
      pay.total_amount AS pay_total_amount,
      pay.success_num ,
      pay.success_amount ,
      pay.fail_num ,
      pay.fail_amount ,     
      pay.send_on ,
      pay.send_user_name ,
      pay.back_on ,
      pay.back_user_name ,
      case pay.service_status when '1' then '审批中' when '2' then '已审批未发送' when '3' then '审批拒绝' 
      when '4' then '已发送未回盘'  when '5' then '回盘成功' when '6' then '回盘异常'  when '7' then '回退审批中'  when '8' then '已回退' else '其他' end status,
      channel.channel_code ,
      channel.channel_desc ,
      case channel.interactive_mode  when '0' then '直连' when '1' then '报盘' else '其他' end interactive_mode
    from
    pay_batch_total AS  pay ,
    channel_setting  AS channel ,
    pay_batch_total_master AS pay_master,
    organization org
    where 
    pay_master.channel_id = channel.id
    AND
    pay_master.master_batchno = pay.master_batchno
    AND
    org.org_id = channel.org_id
    #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("interactive_mode".equals(x.key))
             channel.interactive_mode = #para(x.value)
          #elseif("channel_desc".equals(x.key))
             channel.channel_desc = #para(x.value)
          #elseif("master_batchno".equals(x.key))
             pay_master.master_batchno like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),pay_master.back_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),pay_master.back_on) <= 0
          #elseif("status".equals(x.key))
             pay.service_status in(
              #for(y : map.status)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
           #elseif("codes".equals(x.key))
             org.code in(
              #for(z : map.codes)
                #if(for.index > 0)
                  #(",")
                #end
                #(z)
              #end
            )
          #else
             pay_master.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end
