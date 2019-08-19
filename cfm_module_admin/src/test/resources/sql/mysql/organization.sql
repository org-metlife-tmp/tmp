#sql("getOrgList")
SELECT
  `org_id`,
  `code`,
  `name`,
  `province`,
  `city`,
  `address`,
  `parent_id`,
  `level_code`,
  `level_num`,
  `status`,
  `is_have_extra`
 FROM organization
 WHERE status != 3
 ORDER BY level_code,level_num ASC
#end

#sql("findOrgInfoById")
SELECT
  `org_id`,
  `code`,
  `name`,
  `province`,
  `city`,
  `address`,
  `parent_id`,
  `level_code`,
  `level_num`,
  `status`,
  `is_have_extra`
FROM organization
WHERE status != 3 AND org_id = #para(0)
 LIMIT 1
#end

#sql("getMAXCodeSql")
SELECT max(level_code) AS level_code
 FROM organization a
 WHERE locate(?, a.level_code) = 1 AND level_num = ?
 LIMIT 1
#end

#sql("delOrgExt")
DELETE FROM `org_extra_info`
WHERE org_id = ?
#end

#sql("childOrgNum")
SELECT COUNT(org_id) AS counts
FROM `organization`
WHERE parent_id = ? and `status` != 3
#end

#sql("orgUserNum")
SELECT COUNT(uodp.usr_id) AS counts
FROM user_org_dept_pos uodp
WHERE uodp.org_id = ?
#end

#sql("getOrgNumByCode")
SELECT
  count(org_id) as counts
FROM organization
WHERE `code` = ?
  limit 1
#end

#sql("getOrgNumByCodeExcludeId")
SELECT
  count(org_id) as counts
FROM organization
WHERE `code` = ?
  and org_id != ?
  limit 1
#end

#sql("getCurrentUserOrgs")
  SELECT
  org2.*
FROM
  organization org
  JOIN organization org2
    ON LOCATE(
      org.`level_code`,
      org2.`level_code`
    ) = 1
WHERE org.org_id = ? and org.status != 3
 order by org2.`level_code`
#end