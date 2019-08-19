#namespace("oa_interface")
#sql("getMainOrg")
SELECT
	org_id,
	code,
	name,
	province,
	city,
	address,
	parent_id,
	level_code,
	level_num,
	status,
	is_have_extra 
FROM
	organization
where level_num = 1
#end

#sql("getOrgByCode")
SELECT
	org.org_id,
	org.code,
	org.name,
	org.province,
	org.city,
	org.address,
	org.parent_id,
	org.level_code,
	org.level_num,
	org.status,
	org.is_have_extra 
FROM
    oa_org_mapping mapping,organization org
	
where 
  mapping.tmp_org_code = org.code and mapping.oa_org_code = ?
#end

#sql("getApplyUser")
SELECT
	usr_id,
	name,
	phone,
	email,
	login_name,
	register_date,
	status,
	is_admin,
	is_boss 
FROM
	user_info
where login_name = ?
#end

#sql("getBankJQ")
SELECT
	cnaps_code,
	name,
	swift_code,
	bank_type,
	province,
	city,
	country,
	address,
	area_code  
FROM
	all_bank_info
where name = ?
#end

#sql("getBank")
SELECT
	cnaps_code,
	name,
	swift_code,
	bank_type,
	province,
	city,
	country,
	address,
	area_code  
FROM
	all_bank_info
where name like ?
#end

#sql("getOiriginInterfaceStatus")
   select id,flow_id,send_count,interface_status,interface_fb_msg 
   from oa_origin_data
   where flow_id = ? and send_count = ?
#end

#sql("usrUdopList")
SELECT
  uodp.id,
  uodp.usr_id,
  uodp.org_id,
  uodp.dept_id,
  uodp.pos_id,
  uodp.is_default,
  org.name as org_name,
  dept.name as dept_name,
  pos.name as pos_name 
FROM
  user_org_dept_pos uodp
  JOIN user_info usr
    ON usr.usr_id = uodp.usr_id
  JOIN organization org
    ON org.org_id = uodp.org_id
       AND org.status != 3
  JOIN department dept
    ON dept.dept_id = uodp.dept_id
       AND dept.status != 3
  JOIN position_info pos
    ON pos.pos_id = uodp.pos_id
       AND pos.status != 3
WHERE uodp.usr_id = ?
#end

#sql("updOriginDataInterfaceStatus")
  update oa_origin_data set interface_status = ?,interface_fb_code = ?,interface_fb_msg = ?,process_status = ? where id = ?
#end

#sql("updOriginDataProcessStatus")
  update oa_origin_data set process_status = ?,process_msg = ? ,bank_fb_msg = ? where id = ?
#end

#sql("getSameBill")
  select id from oa_origin_data where flow_id  =? and interface_status  != ? 
#end

#sql("getSameAndSendCountBill")
  select id from oa_origin_data where flow_id  =? and send_count  = ? 
#end
#end

