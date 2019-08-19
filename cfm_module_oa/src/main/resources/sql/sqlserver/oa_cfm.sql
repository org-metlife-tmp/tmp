###总公司oa
#namespace("head_org_oa")
  #include("head_org_oa.sql")
#end

###分公司oa
#namespace("branch_org_oa")
  #include("branch_org_oa.sql")
#end

###原始数据列表
#namespace("origin_data_oa")
  #include("origin_data_oa.sql")
#end

###OA分公司交易核对
#namespace("branch_org_oa_check")
  #include("branch_org_oa_check.sql")
#end

###OA总公司交易核对
#namespace("head_org_oa_check")
  #include("head_org_oa_check.sql")
#end

###可疑数据管理
#namespace("check_doubtful_oa")
  #include("check_doubtful_oa.sql")
#end
