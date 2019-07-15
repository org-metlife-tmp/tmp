package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.BeanKit;
import com.qhjf.cfm.utils.RSAUtils;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.commconstant.ConstantCollectTool;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.jwt.JwtKit;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.render.CaptchaRender;
import com.qhjf.cfm.web.validates.Optype;
import org.apache.commons.codec.binary.Hex;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class GlobalController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(GlobalController.class);

    /**
     * 获取验证图片
     */
    @Clear()
    public void img() {
        logger.debug("Enter into img()");
        CaptchaRender img = new CaptchaRender(4);
        render(img);
    }


    public void index() {
        logger.debug("Enter into index()");
        if (this.getAttr("_obj") != null) {
            JSONObject jb = (JSONObject) this.getAttr("_obj");
            logger.debug("index _obj is:" + jb.toJSONString());
        }
        RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
        final String modulus = Hex.encodeHexString(publicKey.getModulus().toByteArray());
        final String exponent = Hex.encodeHexString(publicKey.getPublicExponent().toByteArray());
        renderJson(new HashMap<String, String>() {
            {
                put("modulus", modulus);
                put("exponent", exponent);
            }
        });
    }


    public void login() {
        logger.debug("Enter into login()");
        JSONObject jobj = this.getAttr("_obj");
        try {
            if (validateLoginReq(jobj)) {
                JSONObject params = jobj.getJSONObject("params");
                String login_name = params.getString("login_name");
                String secret_pwd = params.getString("password");
                logger.debug("passwd加密后为 :" + secret_pwd);
                String decode_pwd = RSAUtils.decryptStringByJs(secret_pwd);
                logger.debug("解密成功：" + decode_pwd);
                String token = JwtKit.getToken(login_name, decode_pwd);
                UserInfo info = (UserInfo) JwtKit.getJwtBean(login_name, new Date());

                renderJson(Ret.by("token", token).set("optype", "login").set("user", BeanKit.beanToMap(info))
                        .set("constants", ConstantCollectTool.ALL_WEB_CONSTANT)
                        .set("dbconstatns", ConstantCollectTool.DB_CONSTANT)
                        .set("uuid", UUID.randomUUID().toString().replace("-", "")));
            }
        } catch (BusinessException e) {
            e.printStackTrace();
            renderJson(Ret.fail().set("error_code", e.getError_code()).set("error_msg", e.getMessage()));
        }
    }


    @Auth(withRoles = {"admin"})
    public void adminProcess() {
        logger.debug("Enter into adminProcess");
        doProcess(Optype.Mode.ADMIN);
    }


    @Auth(hasRoles = {"admin", "normal"})
    public void commProcess() {
        logger.debug("Enter into commProcess");
        doProcess(Optype.Mode.COMM);
    }


    @Auth(hasRoles = {"normal"})
    public void normalProcess() {
        logger.debug("Enter into normalProcess");
        doProcess(Optype.Mode.NORMAL);
    }


    private void doProcess(Optype.Mode mode) {
        JSONObject jobj = this.getAttr("_obj");
        try {
            if (validateReq(mode, jobj)) {
                keepParams(mode);
                String url = getRoute(mode, jobj.getString("optype"));
                if (url != null && !"".equals(url)) {
                    forwardAction(url);
                    optrace();
                } else {
                    renderError(404);
                }
            }
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
