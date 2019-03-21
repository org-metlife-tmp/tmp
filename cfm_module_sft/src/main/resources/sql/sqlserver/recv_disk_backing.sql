#sql("updateLaOriginData")
   update
   la_origin_recv_data  
   set 
   tmp_status = [status] , 
   tmp_err_message  = bank_err_msg
   from
	    recv_batch_detail  
   where
	 la_origin_recv_data.id = recv_batch_detail.origin_id  and
   recv_batch_detail.child_batchno  = ? 
#end


#sql("updateEbsOriginData")
   update
   ebs_origin_pay_data  
   set 
   tmp_status = [status] , 
   tmp_err_message  = bank_err_msg,
   paybankaccno = ?,
   paybankcode = ? ,
   paydate = ? ,
   paytime = ?
   from
	 pay_batch_detail  
   where
	 ebs_origin_pay_data.id = pay_batch_detail.origin_id  
	 and
     pay_batch_detail.child_batchno  = ? 
#end


#sql("selectLaOriginData")
   select
    *
   from
   la_origin_recv_data
   where
   id
   in
   (
   select  origin_id from recv_batch_detail where child_batchno = ?
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
      recv_master.id AS recv_master_id ,
      recv_master.master_batchno,
      case recv_master.source_sys when '0' then 'LA' when '1' then 'EBS' else '其他' end source_sys,
      recv_master.channel_id,
      recv_master.org_id,
      recv_master.dept_id ,
      recv_master.total_num AS recv_master_total_num ,
      recv_master.total_amount AS recv_master_total_amount ,
      recv_master.delete_flag ,
      recv_master.process_bank_type ,
      recv_master.is_checked ,
      recv_master.recv_acc_no ,
      recv_master.recv_acc_name ,
      recv_master.recv_bank_name ,
      recv_master.create_by ,
      recv_master.create_on ,
      recv_master.update_by ,
      recv_master.update_on ,
      recv_master.persist_version ,
      recv_master.service_status ,
      recv.id AS recv_id ,
      recv.child_batchno ,
      recv.total_num AS recv_total_num ,
      recv.total_amount AS recv_total_amount,
      recv.success_num ,
      recv.success_amount ,
      recv.fail_num ,
      recv.fail_amount ,     
      recv.send_on ,
      recv.send_user_name ,
      recv.back_on ,
      recv.back_user_name ,
      case recv.service_status when '1' then '审批中' when '2' then '已审批未发送' when '7' then '审批拒绝' 
      when '4' then '已发送未回盘'  when '5' then '回盘成功' when '6' then '回盘异常'  when '3' then '回退审批中'  when '8' then '已回退'
      when '9' then '已组批未发送' else '其他' end status,
      channel.channel_code ,
      channel.channel_desc ,
      case channel.interactive_mode  when '0' then '直连' when '1' then '报盘' else '其他' end interactive_mode
    from
    recv_batch_total AS  recv ,
    channel_setting  AS channel ,
    recv_batch_total_master AS recv_master,
    organization org
    where 
    recv_master.channel_id = channel.id
    AND
    recv_master.master_batchno = recv.master_batchno
    AND
    org.org_id = channel.org_id
    #if(map != null)
    #for(x : map)
        #if(x.value&&x.value!="")
          AND
          #if("interactive_mode".equals(x.key))
             channel.interactive_mode = #para(x.value)
          #elseif("channel_desc".equals(x.key))
             channel.id = #para(x.value)
          #elseif("master_batchno".equals(x.key))
             recv_master.master_batchno like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),recv.send_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),recv.send_on) <= 0
          #elseif("status".equals(x.key))
             recv.service_status in(
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
             recv_master.#(x.key) = #para(x.value)
          #end
        #end
    #end
  #end
#end


#sql("findDetail")
   select 
      *
    from
      recv_batch_detail AS  detail
    where 
      delete_num = 0
    #if(map != null)
    #for(x : map)
          AND
          #if("amount".equals(x.key))
             detail.amount = #para(x.value)
          #elseif("pay_code".equals(x.key))
             detail.pay_code = #para(x.value)
          #elseif("child_batchno".equals(x.key))
             detail.child_batchno = #para(x.value)
          #elseif("package_seq".equals(x.key))
             detail.package_seq = #para(x.value) 
          #else
             1 = 1
        #end
    #end
  #end
#end

