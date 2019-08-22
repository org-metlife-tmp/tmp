#namespace("gx_ssdf")
  #sql("query_process_sql")
    SELECT
      gx.serial_no,
      gx.bill_no,
      gx.business_no,
      gx.bank_serial_no,
      gx.amount,
      gx.real_time,
      gx.customer_acc,
      gx.org_seg,
      gx.detail_seg,
      gx.trade_status,
      gx.trade_msg
    FROM
      gx_ssdf gx
      LEFT JOIN ssdata_query_log `log`
        ON gx.channel_code = log.channel_code
        AND gx.channel_interface_code = log.channel_interface_code
        AND gx.bank_serial_no = log.bank_serial_no
    WHERE gx.trade_status = 3
      AND (
        (
          log.query_count < 8
          AND log.next_time <= CURRENT_TIME
        )
        OR log.query_count IS NULL
      )
  #end
#end