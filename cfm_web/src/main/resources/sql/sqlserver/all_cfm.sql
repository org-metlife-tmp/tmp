#sql("getAdminMenu")
select menus.code as menu_code, menus.name as menu_name , menus.module_code ,modules.name as module_name
from menus , modules where menus.module_code = modules.code and modules.isAdmin = 1 order by modules.display_order, menus.displayOrder;
#end

#sql("getUseMenu")
select distinct menus.code as menu_code, menus.name as menu_name , menus.module_code , modules.name as module_name,modules.display_order,menus.displayOrder
from user_group_with_user ugwu , user_group ug ,  user_group_with_menu ugwm, menus , modules
where ugwu.group_id = ug.group_id and ug.group_id = ugwm.group_id and ugwm.menu_code = menus.code
and menus.module_code = modules.code and ugwu.uodp_id = ? order by module_code,modules.display_order , menus.displayOrder;
#end

#sql("getUserUodp")
SELECT uodp.id as uodp_id,   uodp.org_id, org.name as org_name,  uodp.dept_id, dept.name as dept_name, uodp.pos_id,pos.name as pos_name, is_default
FROM user_org_dept_pos uodp, organization org , department dept , position_info pos
where uodp.dept_id = dept.dept_id and uodp.org_id = org.org_id and uodp.pos_id = pos.pos_id and usr_id = ? ;
#end

