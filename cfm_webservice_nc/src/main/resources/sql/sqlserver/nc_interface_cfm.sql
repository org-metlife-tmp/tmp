#namespace("nc_interface")

#sql("getSameBill")
  select id from nc_origin_data where flow_id  =? and interface_status  != ?
#end

#sql("getSameAndSendCountBill")
  select id from nc_origin_data where flow_id  =? and send_count  = ?
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
    nc_org_mapping mapping,organization org

where
  mapping.tmp_org_code = org.code and mapping.nc_org_code = ?
#end

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

#sql("updOriginDataInterfaceStatus")
  update nc_origin_data set interface_status = ?,interface_fb_code = ?,interface_fb_msg = ?,process_status = ? where id = ?
#end
#sql("updOriginDataProcessStatus")
  update nc_origin_data set process_status = ?,process_msg = ? ,bank_fb_msg = ? where id = ?
#end


#sql("getcheck")
  SELECT
	*
FROM
	nc_check_doubtful
WHERE identification = ?

#end


#end

