package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * 收付费管理路由
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class SftOptypeMgr extends AbstractOptypeMgr {

    @Override
    public void registe() {
        /** ============================ 通道设置业务 begin ============================ */
        //新增通道
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_addchannel")
                .registKeepParams(new String[]{"interactive_mode", "channel_code", "channel_desc", "pay_mode", "pay_attr", "org_id",
                        "direct_channel", "document_moudle", "single_amount_limit", "single_file_limit", "card_type",
                        "charge_mode", "charge_amount", "bankcode", "op_acc_no", "op_acc_name", "op_bank_name", "is_checkout", "remark", "net_mode"})
                .registerValidate(new RequiredParamsValidate(new String[]{"interactive_mode", "channel_code", "channel_desc", "pay_mode", "pay_attr", "org_id",
                        "bankcode", "is_checkout"})));

        //修改通道
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_chgchannel")
                .registKeepParams(new String[]{"id", "interactive_mode", "channel_code", "channel_desc", "pay_mode", "pay_attr", "org_id",
                        "direct_channel", "document_moudle", "single_amount_limit", "single_file_limit", "card_type",
                        "charge_mode", "charge_amount", "bankcode", "op_acc_no", "op_acc_name", "op_bank_name", "is_checkout", "remark", "net_mode", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "interactive_mode", "channel_code", "channel_desc", "pay_mode", "pay_attr", "org_id",
                        "bankcode", "is_checkout", "persist_version"
                })));

        //通道列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_channellist")
                .registKeepParams(new String[]{"channel_code", "channel_desc", "pay_mode", "pay_attr", "interactive_mode",
                        "bankcode", "is_checkout", "page_size", "page_num"}));

        //通道详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_channeldetail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //获取直联通道
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_getdirectchannel")
               );

        //根据收付属性获取报盘模板
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_getdoucument")
                .registKeepParams(new String[]{"pay_attr", "pay_mode"})
                .registerValidate(new RequiredParamsValidate(new String[]{"pay_attr", "pay_mode"})));

        //返回所有的通道
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_getallchannel")
        );

        //返回所有的bankcode
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_getallbankcode")
        );

        //通道导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftchannel_listexport")
                .registKeepParams(new String[]{"channel_code", "channel_desc", "pay_mode", "pay_attr", "interactive_mode",
                        "bankcode", "is_checkout"}));

        /** ============================ 通道设置业务 end ============================ */

        /** ============================ bankkey设置业务 begin ============================ */
        //新增bankkey
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_addbankkey")
                .registKeepParams(new String[]{"os_source", "org_id", "bankkey", "bankkey_desc", "pay_mode", "bankkey_status",
                        "channel_id", "is_source_back", "subordinate_channel", "bank_type", "remark"})
                .registerValidate(new RequiredParamsValidate(new String[]{"os_source", "org_id", "bankkey", "bankkey_desc", "pay_mode", "bankkey_status",
                        "channel_id", "is_source_back", "subordinate_channel", "bank_type"})));

        //修改bankkey
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_chgbankkey")
                .registKeepParams(new String[]{"id", "os_source", "org_id", "bankkey", "bankkey_desc", "pay_mode", "bankkey_status",
                        "channel_id", "is_source_back", "subordinate_channel", "bank_type", "remark", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "os_source", "org_id", "bankkey", "bankkey_desc", "pay_mode", "bankkey_status",
                        "channel_id", "is_source_back", "subordinate_channel", "bank_type",  "persist_version"
                })));

        //bankkey列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_bankkeylist")
                .registKeepParams(new String[]{"os_source", "bankkey", "bankkey_desc", "channel_code", "channel_desc", "bankkey_status",
                        "page_size", "page_num"}));

        //bankkey详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_bankkeydetail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //bank获取所有机构
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_getorg"));

        //根据收付属性获取通道编码
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_getchanbypaymode")
                .registKeepParams(new String[]{"pay_mode"})
                .registerValidate(new RequiredParamsValidate(new String[]{"pay_mode"})));

        //通道导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftbankkey_listexport")
                .registKeepParams(new String[]{"os_source", "bankkey", "bankkey_desc", "channel_code", "channel_desc", "bankkey_status"}));

        /** ============================ bankkey设置业务 end ============================ */

        /** ============================ 盘片下载业务 start ============================ */
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "disksending_diskdownload")
                .registKeepParams(new String[]{"channel_code","pay_master_id","pay_id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"channel_code","pay_master_id","pay_id"})));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "disksending_list")
        		.registKeepParams(new String[]{"source_sys","page_num","page_size","master_batchno","start_date","end_date","interactive_mode","channel_id","channel_desc","status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size","page_num"})));	
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "disksending_listexport")
        		.registKeepParams(new String[]{"source_sys","master_batchno","start_date","end_date","interactive_mode","channel_id","channel_desc","status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "disksending_sendbank")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "disksending_detaillistexport")
        		.registKeepParams(new String[]{"child_batchno","type"})
                .registerValidate(new RequiredParamsValidate(new String[]{"child_batchno","type"})));
                
        

        /** ============================ 盘片下载业务 end ============================n */
        

        /** ============================ 批量付结算对账 begin ============================ */
        //查询所有批次
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_batchlist")
                .registKeepParams(new String[]{"channel_id_one", "channel_id_two", "is_checked", "start_date", "end_date",
                        "page_size", "page_num"}));

        //查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_tradingList")
                .registKeepParams(new String[]{"bankcode", "acc_no", "min", "max", "start_date", "end_date", "summary", "is_checked"})
                .registerValidate(new RequiredParamsValidate(new String[]{"is_inner"})));

        //对账确认
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_confirm")
                .registKeepParams(new String[]{"batchid", "persist_version", "trading_no"})
                .registerValidate(new RequiredParamsValidate(new String[]{"batchid", "persist_version", "trading_no"})));

        //根据子批次id查询明细
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_getdetailbybaseid")
                .registKeepParams(new String[]{"id", "source_sys"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys"})));

        //根据批次查询交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_gettradbybatchno")
                .registKeepParams(new String[]{"batchno"})
                .registerValidate(new RequiredParamsValidate(new String[]{"batchno"})));

        //查询所有银行账号
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftpaycheck_getallaccountno"));

        /** ============================ 批量付结算对账 end ============================ */

        /** ============================ 收付数据防重预警 begin ============================ */
        //防重预警列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftdoubtful_doubtfullist")
                .registKeepParams(new String[]{"os_source", "start_date", "end_date", "channel_id_one", "channel_id_two", "pay_mode", "bank_key", "bankkey_desc",
                        "tmp_org_id", "preinsure_bill_no", "insure_bill_no", "status", "page_size", "page_num"})
                .registerValidate(new RequiredParamsValidate(new String[]{"os_source"})));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftdoubtful_reject")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));

        //放行
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftdoubtful_pass")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));

        //可疑数据导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftdoubtful_listexport")
                .registKeepParams(new String[]{"os_source", "start_date", "end_date", "pay_mode", "bank_key", "bank_key_desc",
                        "tmp_org_id", "preinsure_bill_no", "insure_bill_no", "status"}));

        /** ============================ 收付数据防重预警 end ============================ */

        /** ============================ 批量付异常数据管理 begin ============================ */
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_exceptlist")
                .registKeepParams(new String[]{"source_sys", "start_date", "end_date", "master_batchno", "channel_id_one", "channel_id_two",
                        "is_revoke", "service_status", "page_size", "page_num"}));

        //详细
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        //回退
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_revoke")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));
        //批量付异常数据导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_listexport")
                .registKeepParams(new String[]{"source_sys", "start_date", "end_date", "master_batchno", "channel_id_one", "channel_id_two",
                        "is_revoke", "status"}));


        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftexcept_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        /** ============================ 批量付异常数据管理 end ============================ */

        /** ============================ 核对组批 start ============================ */
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_list")
                .registKeepParams(new String[]{"preinsure_bill_no","insure_bill_no","biz_type","recv_acc_name","bank_key","channel_id",
                		     "status","source_sys","recv_acc_no","start_date","end_date","org_id","page_size", "page_num","channel_desc"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys","page_size", "page_num"})));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_listexport")
                .registKeepParams(new String[]{"preinsure_bill_no","insure_bill_no","biz_type","recv_acc_name","bank_key","channel_id",
                		     "status","source_sys","recv_acc_no","start_date","end_date","org_id","channel_desc"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys"})));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "source_sys","channel_id"
                }))
                .registKeepParams(new String[]{
                		"remove_ids","visit_time","source_sys","channel_id",
                		"channel_desc","preinsure_bill_no","insure_bill_no","org_id","recv_acc_no"
                		,"start_date","end_date","bank_key","status"
                }));       
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));
        
        //合法数据退回LA/EBS
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_revokeToLaOrEbs")
                .registKeepParams(new String[]{"id", "persist_version", "source_sys","feed_back"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "source_sys"})));
              
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));
                                                    
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));
        
       //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));
        
        //加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));
        
        //我的审批平台 根据主批次号查询子批次详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_findSonByMasterBatch")
                .registKeepParams(new String[]{
                        "master_batchno"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "master_batchno"
                })));
        
        
        //通道编码列表  组批页面  盘片下载  回盘列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_channelCodeList"));
        
        //组批 通道编码详情,用于审批流那里展示
                                                      
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkbatch_detail")
                .registKeepParams(new String[]{
                        "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                })));

        /** ============================ 核对组批 end ============================ */
        
        
        /** ============================ 回盘列表 start ============================ */
        optypes.add(new Optype(Optype.Mode.NORMAL, "diskbacking_list")
                .registKeepParams(new String[]{"start_date","end_date","master_batchno","source_sys",
                		"channel_id","channel_desc","status","page_size","page_num"})
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size","page_num"})));
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "diskbacking_listexport")
                .registKeepParams(new String[]{"start_date","end_date","master_batchno","source_sys",
                		"channel_id","channel_desc","status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys"})));
        
        /** ============================ 回盘列表 end ============================ */

        /** ============================ 批量收结算对账 begin ============================ */
        //查询所有批次
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvcheck_batchlist")
                .registKeepParams(new String[]{"channel_id_one", "channel_id_two", "is_checked", "start_date", "end_date",
                        "page_size", "page_num"}));

        //查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvcheck_tradingList")
                .registKeepParams(new String[]{"bankcode", "acc_no", "min", "max", "start_date", "end_date", "summary", "is_checked"})
                .registerValidate(new RequiredParamsValidate(new String[]{"is_inner"})));

        //对账确认
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvcheck_confirm")
                .registKeepParams(new String[]{"batchid", "persist_version", "trading_no"})
                .registerValidate(new RequiredParamsValidate(new String[]{"batchid", "persist_version", "trading_no"})));

        //根据子批次id查询明细
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvcheck_getdetailbybaseid")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys"})));

        //根据批次查询交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvcheck_gettradbybatchno")
                .registKeepParams(new String[]{"batchno"})
                .registerValidate(new RequiredParamsValidate(new String[]{"batchno"})));

        /** ============================ 批量收结算对账 end ============================ */
        
        
        /** ============================ 批量收 start ============================ */
        //批量收批次列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvcheckbatch_list")
                .registKeepParams(new String[]{"preinsure_bill_no","insure_bill_no","biz_type","bank_key","channel_id",
                		     "status","pay_acc_no","start_date","end_date","org_id","page_size", "page_num","channel_desc"})
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num"})));
      //批量收批次列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvcheckbatch_listexport")
                .registKeepParams(new String[]{"preinsure_bill_no","insure_bill_no","biz_type","bank_key","channel_id",
                		     "status","pay_acc_no","start_date","end_date","org_id","channel_desc"}));
      //批量收组批确认按钮
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvcheckbatch_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "channel_id"
                }))
                .registKeepParams(new String[]{
                		"remove_ids","visit_time","channel_id","biz_type",
                		"channel_desc","preinsure_bill_no","insure_bill_no","org_id","pay_acc_no"
                		,"start_date","end_date","bank_key","status"
                }));
        //批量收组批撤回按钮
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvcheckbatch_revokeToLaOrEbs")
                .registerValidate(new RequiredParamsValidate(new String[]{
                		"id", "persist_version"
                }))
                .registKeepParams(new String[]{
                		"id", "persist_version","feed_back"
                }));
        // 批量收盘片发送/下载  下载按钮
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdisksending_diskdownload")
                .registKeepParams(new String[]{"channel_code","recv_master_id","recv_id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"channel_code","recv_master_id","recv_id"})));
        //批量收 盘片发送列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdisksending_list")
        		.registKeepParams(new String[]{"source_sys","page_num","page_size","master_batchno","start_date","end_date","interactive_mode","channel_id","channel_desc","status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size","page_num"})));	
      //批量收 盘片发送列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdisksending_listexport")
        		.registKeepParams(new String[]{"source_sys","master_batchno","start_date","end_date","interactive_mode","channel_id","channel_desc","status"}));  

     // 批收回盘列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdiskbacking_list")
                .registKeepParams(new String[]{"start_date","end_date","master_batchno","source_sys",
                		"channel_id","channel_desc","status","page_size","page_num"})
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size","page_num"})));
       // 批收回盘列表导出 
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdiskbacking_listexport")
                .registKeepParams(new String[]{"start_date","end_date","master_batchno","source_sys",
                		"channel_id","channel_desc","status"})); 
        
        optypes.add(new Optype(Optype.Mode.NORMAL, "recvdiskbacking_detaillistexport")
        		.registKeepParams(new String[]{"child_batchno","type"})
                .registerValidate(new RequiredParamsValidate(new String[]{"child_batchno","type"})));

        /** ============================ 批量收 end ============================ */

        /** ============================ 批量收异常数据管理 begin ============================ */
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_exceptlist")
                .registKeepParams(new String[]{"source_sys", "start_date", "end_date", "master_batchno", "channel_id_one", "channel_id_two",
                        "is_revoke", "service_status", "page_size", "page_num"}));

        //详细
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        //回退
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_revoke")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));
        //批量付异常数据导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_listexport")
                .registKeepParams(new String[]{"source_sys", "start_date", "end_date", "master_batchno", "channel_id_one", "channel_id_two",
                        "is_revoke", "status"}));


        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvexcept_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        /** ============================ 批量收异常数据管理 end ============================ */

        /** ============================ 收付数据收防重预警 begin ============================ */
        //防重预警列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvdoubtful_doubtfullist")
                .registKeepParams(new String[]{"os_source", "start_date", "end_date", "channel_id_one", "channel_id_two", "pay_mode", "bank_key", "bankkey_desc",
                        "tmp_org_id", "preinsure_bill_no", "insure_bill_no", "status", "page_size", "page_num"})
                .registerValidate(new RequiredParamsValidate(new String[]{"os_source"})));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvdoubtful_reject")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));

        //放行
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvdoubtful_pass")
                .registKeepParams(new String[]{"id", "os_source", "persist_version", "op_reason"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "os_source", "persist_version", "op_reason"})));

        //可疑数据导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "sftrecvdoubtful_listexport")
                .registKeepParams(new String[]{"os_source", "start_date", "end_date", "pay_mode", "bank_key", "bank_key_desc",
                        "tmp_org_id", "preinsure_bill_no", "insure_bill_no", "status"}));

        /** ============================ 收付数据收防重预警 end ============================ */

        
        /** ============================ 柜面付 付款工作台 start ============================ */
        
        //列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "paycounter_list")
                .registKeepParams(new String[]{"source_sys", "page_size", "page_num", "start_date", "end_date", "insure_bill_no", "preinsure_bill_no"
                		, "org_id", "recv_cert_code", "recv_acc_name", "pay_mode", "status", "service_status", "biz_code"})
                .registerValidate(new RequiredParamsValidate(new String[]{"source_sys", "page_size", "page_num"})));
       
        
        
        /** ============================ 柜面付 付款工作台 end ============================ */
    }
}
