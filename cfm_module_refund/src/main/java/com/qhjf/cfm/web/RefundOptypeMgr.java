package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class RefundOptypeMgr extends AbstractOptypeMgr {

	@Override
	public void registe() {
		
		 //===========================主动退票=============================
		
        optypes.add(new Optype(Optype.Mode.NORMAL, "refund_tradeList")
                .registKeepParams(new String[]{
                        "page_size", "page_num","acc_no","min","max","start_date","end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num"
                })));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "refund_billList")
                .registKeepParams(new String[]{
                        "page_size", "page_num","biz_type","trade_id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num","biz_type","trade_id"
                })));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "refund_confirm")
                .registKeepParams(new String[]{
                        "biz_type", "trade_id","bill_id","bill_persist_version","detail_id","detail_persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "biz_type", "trade_id","bill_id","bill_persist_version"
                })));
        //============================怀疑退票=============================
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "doubtfulrefund_tradeList")
                .registKeepParams(new String[]{
                        "page_size", "page_num","acc_no","min","max","start_date","end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num"
                })));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "doubtfulrefund_billList")
                .registKeepParams(new String[]{
                        "page_size", "page_num", "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num", "id"
                })));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "doubtfulrefund_confirm")
                .registKeepParams(new String[]{
                        "biz_type", "id", "bill_id", "bill_persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "biz_type", "id", "bill_id", "bill_persist_version"
                })));
        optypes.add(new Optype(Optype.Mode.NORMAL, "doubtfulrefund_normalTrade")
                .registKeepParams(new String[]{
                        "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                		"id"
                })));
        
        /** ============================ 退票查询 begin ============================ */

        // 退票查询交易列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "refundview_alreadyRefundTradeList")
                .registKeepParams(new String[]{"acc_no" ,
                        "min", "max", "page_size", "page_num", "start_date", "end_date"})
                .registerValidate(new RequiredParamsValidate(new String[]{
                		"page_size", "page_num"
                })));
        
     // 退票查询单据列表,根据交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "refundview_billList")
                .registKeepParams(new String[]{"trade_id"})
                .registerValidate(new RequiredParamsValidate(new String[]{
                		"trade_id"
                })));
        
        
        /** ============================ 退票查询 end ============================ */   
	}
}
