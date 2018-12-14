package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/21
 * @Description: 电子回单
 */
public class EleOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        optypes.add(new Optype(Optype.Mode.NORMAL, "ele_type")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{

                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ele_template")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid"
                }))
                .registKeepParams(new String[]{
                        "uuid"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ele_list")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "channel_code", "eb_type"
                }))
                .registKeepParams(new String[]{
                        "channel_code", "eb_type",
                        "id", "amount", "channel_code", "eb_date", "eb_type",
                        "field_1", "field_2", "field_3", "field_4", "field_5",
                        "field_6", "field_7", "field_8", "field_9", "field_10",
                        "field_11", "field_12", "field_13", "field_14", "field_15",
                        "field_16", "field_17", "field_18", "field_19", "field_20",
                        "field_21", "field_22", "field_23", "field_24", "field_25",
                        "field_26", "field_27", "field_28", "field_29", "field_30",
                        "field_31", "field_32", "field_33", "field_34", "field_35",
                        "field_36", "field_37", "field_38", "field_39", "field_40",
                        "field_41", "field_42", "field_43", "field_44", "field_45",
                        "field_46", "field_47", "field_48", "field_49", "field_50",
                        "min", "max"
                }));

    }
}
