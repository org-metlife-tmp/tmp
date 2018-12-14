package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.constant.WebConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/27
 */

public class AccCommonService {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static void setTodoListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.REJECT.getKey(),
                    WebConstant.BillStatus.WAITPROCESS.getKey()
            });
        }
    }

    public static void setDoneListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey()
            });
        }
    }

    public static void setInnerMoreListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.REJECT.getKey(),
                    WebConstant.BillStatus.PROCESSING.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey()
            });
        }
    }

    public static void setInnerDetailListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.PROCESSING.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey()
            });
        }
    }

    public static void setSktMoreListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey()
            });
        }
    }

    public static void setSktListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUCCESS.getKey()
            });
        }
    }

    public static void setInnerPayListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
            });
        }
    }

    public static void setInnerTradStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUCCESS.getKey()
            });
        }
    }

    public static void setInnerBatchTradStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.PayStatus.SUCCESS.getKey()
            });
        }
    }

    public static void setPoolTradStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.CollOrPoolRunStatus.SUCCESS.getKey()
            });
        }
    }

    public static void setAccountListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.AccountStatus.NORAMAL.getKey(),
                    WebConstant.AccountStatus.FREEZE.getKey(),
                    WebConstant.AccountStatus.CLOSED.getKey()
            });
        }
    }

    public static void setInnerBatchListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.REJECT.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey()
            });
        }
    }

    public static void setInnerBatchPayListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey()
            });
        }
    }

    public static void setInnerBatchViewListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey()
            });
        }
    }

    public static void checkAccProcessLock(long acc_id) throws BusinessException {
        Record record = Db.findById("acc_process_lock", "acc_id", acc_id);
        //1变更  2冻结  3销户

        if (record != null) {
            throw new ReqDataException("此账户正在进行[" +
                    WebConstant.AccProcessLockType.getAccProcessLockType(TypeUtils.castToInt(record.get("type"))).getDesc() + "]操作！");
        }
    }

    public static String formatDate(Object o) throws BusinessException {
        if (null == o) return "";
        if (o instanceof Date) {
            o = (Date) o;
            return format.format(o);
        } else if (o instanceof String) {
            return (String) o;
        } else {
            throw new ReqDataException("未知的数据类型错误！");
        }
    }

    public static void setAllocationMoreListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.REJECT.getKey()
            });
        }
    }

    public static void setAllocationViewListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
            });
        }
    }

    public static void setAllocationMgtListStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.PASS.getKey(),
            });
        }
    }

    public static void setOaTodoStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.REJECT.getKey()
            });
        }
    }

    public static void setOaDoneStatus(final Record record, String statusName) {
        List status = record.get(statusName);

        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.PROCESSING.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey(),
                    WebConstant.BillStatus.WAITPROCESS.getKey()
            });
        }
    }
}
