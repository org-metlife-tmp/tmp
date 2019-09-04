package com.qhjf.cfm.web.services;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.LdapUtil;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.IJwtAble;
import com.qhjf.cfm.web.plugins.jwt.IJwtUserService;

import java.util.List;

public class UserLoginService implements IJwtUserService {

    public static final UserLoginService me = new UserLoginService();

    public static GlobalConfigSection mgr = GlobalConfigSection.getInstance();

    private UserLoginService(){

    }

    @Override
    public IJwtAble login(String userName, String password) throws BusinessException {
        Record userInfo = validateUser(userName, password);
        UserInfo user = new UserInfo(userInfo);
        setUodp(user);
        setMenuInfo(user);
        return user;
    }

    public void setMenuInfo(UserInfo user) {
        user.refMenuInfo();
    }

    private void setUodp(UserInfo user) {
        if(user.getIs_admin() == WebConstant.YesOrNo.NO.getKey()){
            String sql = Db.getSql("getUserUodp");
            List<Record> uodps = Db.find(sql, user.getUsr_id());
            user.addUodp(uodps);
        }
    }

    private Record validateUser(String userName, String password) throws BusinessException {
        Record userInfo  = Db.findById("user_info","login_name",userName);
        if (userInfo != null){
            /**
             * 校验用户状态
             */
            int userStatus = TypeUtils.castToInt(userInfo.get("status"));
            if(userStatus != WebConstant.UserStatus.NORMAL.getKey()){
                WebConstant.UserStatus user_Status = WebConstant.UserStatus.getByKey(userStatus);
                throw new LoginException("用户["+userName+"]状态不正确，为："+user_Status!=null? user_Status.getDesc():String.valueOf(userStatus));
            }

            /**
             * 如果启用LDAP校验并且用户不是管理员，进行LDAP校验，否则校验数据库密码
             */
            if(mgr.hasConfig(IConfigSectionType.DefaultConfigSectionType.Ldap)){
            	try {
					LdapUtil.authentication(userName , password);
				} catch (BusinessException e) {
					tryTimeIncrease(userInfo);
					throw e;
				}
            }else{
                /**
                 * 校验数据库的密码
                 */
                String md5Pwd = userInfo.getStr("password");
                String cal_pwd = MD5Kit.encryptPwd(password, userInfo.getStr("salt"), userInfo.getInt("pcount"));
                if(!md5Pwd.equals(cal_pwd)){
                	tryTimeIncrease(userInfo);
                    throw new LoginException("用户["+userName+"]密码不正确！");
                }
            }



        }else{
            throw new LoginException("用户["+userName+"]不存在！");
        }
        /**
         * 用户登陆成功，暴力破解重试字段重置为0
         */
        String tryTimes = userInfo.getStr("try_times");
        if (null != tryTimes && !"0".equals(tryTimes)) {
        	userInfo.set("try_times", 0);
			Db.update("user_info", "usr_id", userInfo);
		}
        return userInfo;
    }

    /**
     * 登陆失败，则try_time字段加1
     * @param userInfo
     */
    private void tryTimeIncrease(Record userInfo){
    	
    	Integer tryTimes = userInfo.getInt("try_times");
    	if (tryTimes != null && tryTimes >= 999) {
    		userInfo.set("try_times", 1000);
    		userInfo.set("status", WebConstant.UserStatus.FROZEN.getKey());
			Db.update("user_info", "usr_id", userInfo);
		}else {
			userInfo.set("try_times", tryTimes == null ? 1 : ++tryTimes);
			Db.update("user_info", "usr_id", userInfo);
		}
    }
}
