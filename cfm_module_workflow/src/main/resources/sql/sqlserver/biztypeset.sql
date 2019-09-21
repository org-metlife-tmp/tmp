#sql("findBizTypeSetInfo")
  SELECT
    biz_id,
    p_id,
    biz_name,
    is_delete,
    is_activity,
    memo
  FROM
    cfm_biz_type_setting
  WHERE is_delete = 0
  AND is_activity = 1
  AND p_id = ?
#end