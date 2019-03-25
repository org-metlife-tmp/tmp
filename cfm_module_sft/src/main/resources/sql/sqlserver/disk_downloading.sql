#sql("findTotalConfig")
    select 
      *
    from
    document_total_config  dtc
    where 
    dtc.document_type = ? 
    and
    dtc.document_moudle = ? 
#end

#sql("findDatailConfig")
    select 
      *
    from
    document_detail_config  ddc
   where 
   ddc.document_type = ?   
   and
   ddc.document_moudle = ? 
#end


#sql("findToatlInfo")
    select 
      pay_master.id AS pay_master_id ,
      pay_master.master_batchno,
      pay_master.source_sys,
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
      pay.send_on ,
      pay.send_user_name      
    from
    pay_batch_total_master  AS pay_master ,
    pay_batch_total AS pay
    where 
    pay_master.master_batchno = pay.master_batchno
    and
    pay.id = ?  
#end

#sql("findDatailInfo")
    select 
      pbd.* 
    from
    pay_batch_detail  pbd 
   where 
   pbd.base_id = ? 
   order by pbd.package_seq asc 
#end

#sql("findEBSDatailInfo")
    select 
      pbd.* 
    from
    pay_batch_detail  pbd 
   where 
   pbd.base_id = ? 
   order by pbd.package_seq asc 
#end

#sql("findOfferDocument")
    select 
      *
    from
    pay_offerDocument_total  ot
   where 
   ot.batch_id = ? 
#end

#sql("findMasterByBatchNo")
    select
      *
    from
    pay_batch_total_master
   where
    master_batchno = ?
#end

#sql("findTotalByMainBatchNo")
    select
      *
    from
    pay_batch_total
   where
    master_batchno = ?
#end

#sql("findDiskSendingList")
   select 
      tab.* ,
      offer.file_name
      from
      (
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
      case pay.service_status when '1' then '审批中' when '2' then '已审批未发送' when '3' then '回退审批中' 
      when '4' then '已发送未回盘'  when '5' then '回盘成功' when '6' then '回盘异常'  when '7' then '审批拒绝'  when '8' then '已回退' else '其他' end status,
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
             channel.id = #para(x.value)
          #elseif("master_batchno".equals(x.key))
             pay_master.master_batchno like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
          #elseif("start_date".equals(x.key))
             DATEDIFF(day,#para(x.value),pay_master.create_on) >= 0
          #elseif("end_date".equals(x.key))
             DATEDIFF(day,#para(x.value),pay_master.create_on) <= 0
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
   )  tab  left join  pay_offerDocument_total  offer
       on   tab.pay_id = offer.batch_id     
#end


#sql("findBatchDetail")
    select 
      pay_master.id AS pay_master_id ,
      pay_master.master_batchno,
      pay_master.source_sys ,
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
      pay.service_status  status,
      channel.channel_code ,
      channel.channel_desc ,
      userinfo.name ,
      case channel.interactive_mode  when '0' then '直连' when '1' then '报盘' else '其他' end inter_mode
    from
    pay_batch_total AS  pay ,
    channel_setting  AS channel ,
    pay_batch_total_master AS pay_master ,
    user_info AS userinfo 
    where 
    pay_master.channel_id = channel.id
    AND
    pay_master.master_batchno = pay.master_batchno
    and
    userinfo.usr_id = pay_master.create_by
    and
    pay_master.id  = ? 
#end



#sql("selAccNoBalence")
    select
      *
    from
    	acc_cur_balance
    where
    	acc_no = ?
#end


#sql("selectDetailByChildno")
    select
      detail.child_batchno,
      detail.recv_acc_name ,
      detail.recv_acc_no ,
      case isnull(detail.bank_err_code,'-1') when '0000' then '交易成功' when '成功' then '交易成功'  when 'S0000' then '交易成功'
       when '-1' then ''  else '交易失败' end bank_err_code,
      detail.bank_err_msg ,
      detail.amount ,
      total.back_on ,
      channel.channel_code,
      case channel.interactive_mode  when '0' then '直连' when '1' then '报盘' else '其他' end interactive_mode ,
      channel.channel_desc     
    from
        pay_batch_detail AS detail,
        channel_setting AS channel,
        pay_batch_total AS total ,
        pay_batch_total_master AS master   	
    where
    	detail.child_batchno = total.child_batchno 
    	and
    	master.master_batchno = total.master_batchno
    	and 
    	master.channel_id = channel.id
    	and 
    	detail.child_batchno = ? 
#end


