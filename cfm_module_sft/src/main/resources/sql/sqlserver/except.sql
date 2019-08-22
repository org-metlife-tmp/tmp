#sql("exceptlist")
SELECT
	child.id,
	master.source_sys,
	child.master_batchno,
	child.child_batchno,
	chan.interactive_mode,
	chan.channel_code,
	chan.channel_desc,
	child.send_on,
	child.total_amount,
	child.total_num,
	child.service_status,
	child.error_msg,
	child.revoke_user_name,
	child.revoke_date,
	child.is_revoke,
	child.exam_position_name,
	child.exam_time,
	child.persist_version
FROM
	pay_batch_total child,
	pay_batch_total_master master,
	channel_setting chan,
	organization org
WHERE
	child.master_batchno = master.master_batchno
	AND chan.id = master.channel_id
	and chan.org_id = org.org_id
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&(!"[]".equals(x.value.toString())))
        AND
        #if("source_sys".equals(x.key))
          master.#(x.key) = #para(x.value)
        #elseif("master_batchno".equals(x.key))
          child.master_batchno like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("channel_id_one".equals(x.key))
          master.channel_id = #para(x.value)
        #elseif("channel_id_two".equals(x.key))
          master.channel_id = #para(x.value)
        #elseif("start_date".equals(x.key))
          DATEDIFF(day,#para(x.value),send_on) >= 0
        #elseif("end_date".equals(x.key))
          DATEDIFF(day,#para(x.value),send_on) <= 0
        #elseif("service_status".equals(x.key))
          child.service_status in(
            #for(y : map.service_status)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #elseif("codes".equals(x.key))
          org.code in(
            #for(y : map.codes)
              #if(for.index > 0)
                #(",")
              #end
              #(y)
            #end
          )
        #else
          child.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by child.id desc
#end


#sql("findExceptlistPendingList")
SELECT
	total.*,
	case channel.interactive_mode when 0 then '直连' when 1 then '报盘' end inter_mode,
	channel.channel_code,
	channel.channel_desc,
	userinfo.name ,
	cwrei.id as inst_id,
	cwrei.base_id,
	cwrei.workflow_name,
	cwrei.define_id,
	cwrei.workflow_type,
	cwrei.reject_strategy,
	cwrei.def_version,
	cwrei.workflow_node_id,
	cwrei.step_number,
	cwrei.shadow_execute,
	cwrei.shadow_user_id,
	cwrei.shadow_user_name,
	cwrei.biz_type,
	cwrei.bill_id,
	cwrei.bill_code,
	cwrei.submitter,
	cwrei.submitter_name,
	cwrei.submitter_pos_id,
	cwrei.submitter_pos_name,
	cwrei.init_user_id,
	cwrei.init_user_name,
	cwrei.init_org_id,
	cwrei.init_org_name,
	cwrei.init_dept_id,
	cwrei.init_dept_name,
	cwrei.start_time
FROM
	pay_batch_total total,
	pay_batch_total_master master,
	channel_setting channel,
	cfm_workflow_run_execute_inst cwrei,
	user_info  userinfo
WHERE total.id = cwrei.bill_id
and master.channel_id = channel.id
and total.master_batchno = master.master_batchno
and userinfo.usr_id = total.create_by
  #for(x : map)
    #if("in".equals(x.key))
      #if(map.in != null)
        AND cwrei.id IN (
          #for(y : map.in)
            #for(z : y.instIds)
              #if(for.index > 0)
                #(",")
              #end
              #(z)
            #end
          #end
        )
      #end
    #elseif("notin".equals(x.key))
      #if(map.notin != null)
        AND cwrei.id NOT IN (
          #for(y : map.notin)
            #for(z : y.excludeInstIds)
              #if(for.index > 0)
                #(",")
              #end
              #(z)
            #end
          #end
        )
      #end
    #elseif("biz_type".equals(x.key))
     AND  #(x.key) = #(x.value)
    #end
  #end
  order by total.id
#end


#sql("findbatchtotalbyid")
SELECT
	total.*,
	case channel.interactive_mode when 0 then '直连' when 1 then '报盘' end inter_mode,
	channel.channel_code,
	channel.channel_desc,
	master.org_id,
	userinfo.name
FROM
	pay_batch_total total,
	pay_batch_total_master master,
	channel_setting channel,
	user_info userinfo
WHERE
	total.master_batchno = master.master_batchno
	and master.channel_id = channel.id
	and userinfo.usr_id = total.create_by
	and total.id = ?
#end

#sql("findFirstApprovePosition")
SELECT
	inst.assignee_id,inst.assignee,pos.pos_id,pos.name
FROM
	cfm_workflow_his_execute_inst inst,user_org_dept_pos uodp,position_info pos
WHERE
    inst.assignee_id = uodp.usr_id
    and uodp.pos_id = pos.pos_id
	and inst.biz_type = ?
	and inst.bill_id = ?
	and inst.step_number = 1
#end

#sql("updIntrDetailForException")
	update
		detail
	set
		status = 4
	from
		batch_pay_instr_queue_detail detail,
		batch_pay_instr_queue_total total
	where detail.base_id=total.id
		and total.bill_id = ?
#end

#sql("recvUpdIntrDetailForException")
	update
		detail
	set
		status = 4
	from
		batch_recv_instr_queue_detail detail,
		batch_recv_instr_queue_total total
	where detail.base_id=total.id
		and total.bill_id = ?
#end

