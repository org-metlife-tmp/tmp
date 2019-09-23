#sql("recvcounterPoslist")
SELECT
    recv.id ,
    recv.liquidation_date ,
    recv.trade_date ,
    recv.trade_time ,
    recv.terminal_no ,
    recv.trade_amount ,
    recv.procedures_amount ,
    recv.entry_account_amount ,
    recv.system_track_number ,
    recv.retrieval_reference_number ,
    recv.serial_number ,
    recv.trade_type ,
    recv.card_no,
    recv.card_type ,
    recv.card_issue_bank ,
    recv.no_identity_mark ,
    recv.import_date ,
    case recv.bill_checked when '0' then '未核对'  when '1' then '已核对'  end bill_checked,
    recv.bill_statement_code ,
    recv.bill_check_user_name ,
    recv.bill_check_service_number,
    recv.trade_checked ,
    recv.trade_statement_code ,
    recv.trade_check_user_name ,
    recv.trade_check_service_number,
    recv.create_on ,
    recv.update_on ,
    recv.update_by ,
    recv.delete_flag ,
    recv.persist_version
FROM
	recv_counter_pos AS recv
WHERE
	recv.delete_flag = 0
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!=""&&!"[]".equals(x.value.toString()))
         AND
        #if("trade_type".equals(x.key))
        	recv.trade_type = #para(x.value)
        #elseif("liquidation_start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.liquidation_date) >= 0
        #elseif("liquidation_end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.liquidation_date) <= 0
        #elseif("trade_start_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.trade_date) >= 0
        #elseif("trade_end_date".equals(x.key))
            DATEDIFF(day,#para(x.value),recv.trade_date) <= 0
        #elseif("terminal_no".equals(x.key))
            recv.terminal_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("serial_number".equals(x.key))
            recv.serial_number like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("card_no".equals(x.key))
            recv.card_no like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("card_issue_bank".equals(x.key))
            recv.card_issue_bank like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
         #elseif("min".equals(x.key))
              recv.trade_amount >= #para(x.value)
         #elseif("max".equals(x.key))
              recv.trade_amount <= #para(x.value)
         #elseif("bill_checked".equals(x.key))
            recv.bill_checked in(
              #for(y : map.bill_checked)
                #if(for.index > 0)
                  #(",")
                #end
                #(y)
              #end
            )
         #else
           1 = 1    
        #end
      #end
    #end
  #end
  order by recv.id asc
#end