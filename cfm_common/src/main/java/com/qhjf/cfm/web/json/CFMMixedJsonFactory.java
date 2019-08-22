package com.qhjf.cfm.web.json;

import com.jfinal.json.FastJson;
import com.jfinal.json.IJsonFactory;
import com.jfinal.json.JFinalJson;
import com.jfinal.json.Json;

/**
 * 重写MIxedJsonFactory
 */
public class CFMMixedJsonFactory implements IJsonFactory {

    private static final CFMMixedJsonFactory me = new CFMMixedJsonFactory();

    public static CFMMixedJsonFactory me() {
        return me;
    }

    private static CFMMixedJsonFactory.MixedJson mixedJson =  new CFMMixedJsonFactory.MixedJson();

    private static class MixedJson extends Json {

        private static Json jFinalJson = JFinalJson.getJson().setDatePattern("yyyy-MM-dd");
        private static FastJson fastJson = FastJson.getJson();

        public String toJson(Object object) {
            return jFinalJson.toJson(object);
        }

        public <T> T parse(String jsonString, Class<T> type) {
            return fastJson.parse(jsonString, type);
        }
    }


    @Override
    public Json getJson() {
        return mixedJson;
    }
}
