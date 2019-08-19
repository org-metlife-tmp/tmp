###附件sql
#sql("files")
SELECT
  atta.*,
  atta_ref.bill_id,
  atta_ref.biz_type
FROM
  common_attachment_info atta
  JOIN common_attachment_info_ref atta_ref
    ON atta_ref.attachment_id = atta.id
WHERE atta_ref.biz_type = ?
  AND atta_ref.bill_id = ?
#end