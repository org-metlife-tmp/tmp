###销户事项start
#namespace("cai")
  #include("cai.sql")
#end
###销户事项end

###销户事项补录start
#namespace("caf")
  #include("caf.sql")
#end
###销户事项补录end

###开户事项start
#namespace("aoi")
  #include("aoi.sql")
#end
###开户事项end

###开户事项补录start
#namespace("aoc")
  #include("aoc.sql");
#end
###开户事项补录end

###账户信息维护start
#namespace("acc")
  #include("acc.sql");
#end
###账户信息维护end

###开户事项变更start
#namespace("chg")
  #include("chg.sql");
#end
###开户事项变更end

###账户冻结/解冻start
#namespace("afd")
  #include("afd.sql")
#end
###账户冻结/解冻end

###查询业务所对应得流程列表
#namespace("common")
  #sql("displayPossibleWf")
    SELECT
      def.id AS define_id,
      base.`workflow_name`,
      base.`laster_version`,
      def.`reject_strategy`
    FROM
      `cfm_workflow_define` def
      JOIN `cfm_workflow_base_info` base
        ON base.id = def.base_id
        AND base.`is_activity` = 1
        AND base.delete_flag = 0
        AND def.`def_version` = base.`laster_version`
      JOIN cfm_workflow_relation rela
        ON rela.`base_id` = base.id
    WHERE LOCATE(?, rela.org_exp) > 0
      AND LOCATE(?, rela.biz_exp) > 0
      AND rela.is_activity = 1
      #end
#end