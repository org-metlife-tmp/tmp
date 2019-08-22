package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;

import java.math.BigDecimal;
import java.util.List;

public class GylCommonService {

    public static void buildGylList(List<Record> list) {
        if (list != null && list.size() > 0) {
            for (Record topic : list) {
                int collect_type = TypeUtils.castToInt(topic.get("gyl_allocation_type"));
                topic.set("gyl_allocation_type_name", WebConstant.CollOrPoolType.getByKey(collect_type).getDesc());
                BigDecimal amount = TypeUtils.castToBigDecimal(topic.get("gyl_allocation_amount"));
                topic.set("gyl_allocation_amount_new", "￥" + amount.toString());
                int collect_frequency = TypeUtils.castToInt(topic.get("gyl_allocation_frequency"));
                if (collect_frequency == WebConstant.CollOrPoolFrequency.DAILY.getKey()) {
                    topic.set("gyl_allocation_frequency_name", WebConstant.CollOrPoolFrequency.getByKey(collect_frequency).getDesc());
                    topic.set("gyl_allocation_frequenc_time", topic.get("gyl_allocation_time"));
                } else {
                    String collect_time = TypeUtils.castToString(topic.get("gyl_allocation_time"));
                    String[] arr = collect_time.split("-");
                    topic.set("gyl_allocation_frequency_name", WebConstant.CollOrPoolFrequency.getByKey(collect_frequency).getDesc() + arr[0]);
                    topic.set("gyl_allocation_frequenc_time", arr[1]);
                }
            }
        }
    }
}
