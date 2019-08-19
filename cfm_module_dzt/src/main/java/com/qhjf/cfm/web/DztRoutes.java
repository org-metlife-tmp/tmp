package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.DztAdjustController;
import com.qhjf.cfm.web.controller.DztCheckController;
import com.qhjf.cfm.web.controller.DztInitSettingController;
import com.qhjf.cfm.web.controller.DztMgtController;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通
 */
public class DztRoutes extends Routes {
    @Override
    public void config() {
        //对账初始化设置
        add("/normal/dztinit", DztInitSettingController.class);
        //对账管理
        add("/normal/dztmgt", DztMgtController.class);
        //余额调节表
        add("/normal/dztadjust", DztAdjustController.class);
        //已勾兑查询
        add("/normal/dztcheck", DztCheckController.class);
    }
}
