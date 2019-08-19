package com.qhjf.cfm.web.util.jyyet;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AccountUtil {

    /**
     * 从list<Map>中提取银行账号列表
     *
     * @param list
     * @return
     * @throws ReqDataException
     */
    public static List<String> getAccNoList(List<Map<String, Object>> list) throws ReqDataException {
        List<String> accNoList = new ArrayList<>();
        String acc_no = null;
        for (Map<String, Object> map : list) {
            acc_no = map.get("acc_no").toString();
            if (StringUtils.isBlank(acc_no)) {
                throw new ReqDataException(String.format("导入数据银行账号为空，数据为：【%s】", map.toString()));
            }
            accNoList.add(acc_no);
        }
        return accNoList;
    }

    /**
     * 通过银行账号列表查询银行账号信息
     *
     * @param accNoList 银行账号列表
     * @return
     * @throws BusinessException
     */
    public static List<Record> getAccountInfo(List<String> accNoList) throws BusinessException {

        SqlPara sqlPara = Db.getSqlPara("curyet.accountInfo", Kv.create().set("accNoList", accNoList));
        List<Record> find = Db.find(sqlPara);
        if (find == null || accNoList.size() != find.size()) {
            throw new DbProcessException(String.format("某些银行账号在系统中不存在!账号列表=【%s】", accNoList));
        }
        return find;
    }

    /**
     * 从list<Map>中提取银行账号列表，并去重
     *
     * @param list
     * @return
     * @throws ReqDataException
     */
    public static List<String> getAccNoListDeDuplicate(List<Map<String, Object>> list) throws ReqDataException {
        Set<String> accNoSet = new HashSet<>();
        String acc_no = null;
        for (Map<String, Object> map : list) {
            acc_no = map.get("acc_no").toString();
            if (StringUtils.isBlank(acc_no)) {
                throw new ReqDataException(String.format("导入数据银行账号为空，数据为：【%s】", map.toString()));
            }
            accNoSet.add(acc_no);
        }
        return new ArrayList<>(accNoSet);
    }
}
