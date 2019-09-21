package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class BbOptypeMgr extends AbstractOptypeMgr{
    @Override
    public void registe() {
        /** ============================ OA报表业务 start ============================ */

        //OA报表查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "oaReport_OaReportList")
                .registKeepParams(new String[]{"start_date","end_date","flow_id","recv_account_name",
                        "payment_amount","pay_org_type","min","max","page_size", "page_num"})
                );

        //报表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "oaReport_listexport")
                .registKeepParams(new String[]{"start_date","end_date","flow_id","recv_account_name",
                        "payment_amount","pay_org_type"}));
    /** ============================ OA报表业务 end ============================ */
    /** ============================ 归集通报表业务 start ============================ */
    //报表查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "gjtReport_GjtReportList")
                .registKeepParams(new String[]{"start_date","end_date","recv_account_no","pay_account_no",
            "payment_amount","min","max","page_size", "page_num"
    }));

    //报表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "gjtReport_listexport")
                .registKeepParams(new String[]{"start_date","end_date","recv_account_no","pay_account_no",
                        "payment_amount","min","max"}));
    /** ============================ 归集通报表业务 end ============================ */

    /** ============================ 对账单报表业务 start ============================ */
        //报表查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dzdReport_DzdReportList")
                .registKeepParams(new String[]{"start_date","end_date","acc_name","page_size", "page_num"
                }));

        //报表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "dzdReport_listexport")
                .registKeepParams(new String[]{"start_date","end_date","acc_name","page_size", "page_num"}));
     /** ============================ 对账单报表业务 end ============================ */

    }


}