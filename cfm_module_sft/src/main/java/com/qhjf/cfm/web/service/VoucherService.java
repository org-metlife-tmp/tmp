package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.utils.ValidateUtil;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 资金系统月末预提凭证操作
 *
 * @author GJF
 * @date 2019年4月10日
 */
public class VoucherService {

    private static Logger log = LoggerFactory.getLogger(VoucherService.class);
    /**
     * 未核对流水列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> voucherlist(int pageNum, int pageSize, final Record record) throws BusinessException {
        /**
        //根据财务月获取财务月的开始时间和结束时间
        String periodEndDate = record.getStr("period_date");
        String periodStartDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(periodEndDate));
            cal.add(Calendar.MONTH, -1);
            periodStartDate = sdf.format(cal.getTime());

        } catch (ParseException e){
            throw new ReqDataException("日期格式错误!");
        }
        //判断该财务月是否开启
        Record calStartRec = Db.findFirst(Db.getSql("voucher.getcheckoutday"), periodStartDate);
        Record calEndRec = Db.findFirst(Db.getSql("voucher.getcheckoutday"), periodEndDate);
        if(calStartRec==null || calEndRec==null){
            throw new ReqDataException("该财务月未设置结账日");
        }

        Date period_start = TypeUtils.castToDate(calStartRec.get("cdate"));
        Date period_end = TypeUtils.castToDate(calEndRec.get("cdate"));
        record.set("period_start", period_start);
        record.set("period_end", period_end);
         */

        ArrayList<String> precondition = record.get("precondition");
        ArrayList<String> preconditionAll = new ArrayList<String>() {{
            add("0");
            add("1");
            add("2");
        }};
        if(precondition != null){
            if(precondition.contains("0")){
                //包含0 要用not in  不包含0 要用in
                boolean s = preconditionAll.removeAll(precondition);
                record.set("precondition", preconditionAll);
            }else{
                record.set("preconditions", precondition);
                record.remove("precondition");
            }
        }

        SqlPara sqlPara = Db.getSqlPara("voucher.voucherlist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 根据机构获取用户
     *
     */
    public List<Record> getaccbyorg(Record record) {
        return Db.find(Db.getSqlPara("voucher.getaccbyorg", Kv.by("map", record.getColumns())));
    }

    /**
     * 交易确认
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void confirm(final Record record, final UserInfo userInfo) throws BusinessException {

        final List<Integer> idList = record.get("id");
        List<Record> trads = Db.find(Db.getSqlPara("voucher.findTradById", Kv.by("map", record.getColumns())));
        if (trads!=null && trads.size()!=0) {
            throw new ReqDataException("存在已核对的交易!");
        }
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findTradList", Kv.by("map", record.getColumns())));

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try{
                    for (Record r : tradlist) {
                        Record extRecord = new Record();
                        extRecord.set("trans_id", r.getLong("id"));
                        extRecord.set("presubmit_user_name", userInfo.getName());
                        extRecord.set("presubmit_user_id", userInfo.getUsr_id());
                        extRecord.set("period_date", CommonService.getPeriodByCurrentDay(r.getDate("trans_date")));
                        extRecord.set("precondition", 1);
                        boolean i = Db.save("acc_his_transaction_ext", extRecord);
                        if (!i) {
                            return false;
                        }
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
                try {
                    //生成凭证信息
                    CheckVoucherService.sunVoucherData(tradlist, WebConstant.MajorBizType.CWYTJ.getKey());
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("交易核对失败！");
        }

    }

}
