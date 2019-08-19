package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;

import java.math.BigDecimal;
import java.util.List;

public class GjtCommonService {

    public static void buildTopicList(List<Record> list) {
        if (list != null && list.size() > 0) {
            for (Record topic : list) {
                int collect_type = TypeUtils.castToInt(topic.get("collect_type"));
                topic.set("collect_type_name", WebConstant.CollOrPoolType.getByKey(collect_type).getDesc());
                if(collect_type != WebConstant.CollOrPoolType.ALL.getKey()) {
                    BigDecimal amount = TypeUtils.castToBigDecimal(topic.get("collect_amount"));
                    topic.set("collect_amount_new", "￥" + amount.toString());
                }
                int collect_frequency = TypeUtils.castToInt(topic.get("collect_frequency"));
                if (collect_frequency == WebConstant.CollOrPoolFrequency.DAILY.getKey()) {
                    topic.set("collect_frequency_name", WebConstant.CollOrPoolFrequency.getByKey(collect_frequency).getDesc());
                    topic.set("collect_frequenc_time", topic.get("collect_time"));
                } else {
                    String collect_time = TypeUtils.castToString(topic.get("collect_time"));
                    String[] arr = collect_time.split("-");
                    topic.set("collect_frequency_name", WebConstant.CollOrPoolFrequency.getByKey(collect_frequency).getDesc() + arr[0]);
                    topic.set("collect_frequenc_time", arr[1]);
                }
            }
        }
    }
}
