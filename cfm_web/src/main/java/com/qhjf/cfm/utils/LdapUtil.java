package com.qhjf.cfm.utils;

import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.LdapConfigSection;
import com.qhjf.cfm.web.services.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * 光大永明LDAP 用户验证
 */
public class LdapUtil {

    private static LdapConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Ldap);

    private static DirContext ctx;

    // LDAP服务器端口默认为389
    private static final String LDAP_URL ;

    private static final String DOMAIN;


    private static final String DOMAIN_FORMAT;



    //LDAP驱动
    private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private static Logger logger = LoggerFactory.getLogger(LdapUtil.class);

    static{
        DOMAIN = config.getDomain();
        DOMAIN_FORMAT = config.getDomainFormat();
        LDAP_URL = "ldap://"+config.getHost()+":"+config.getPort();
    }


    /**
     * LDAP 用户验证
     *
     * @param account
     * @param password
     * @throws BusinessException
     */
    public static void authentication(String account, String password) throws BusinessException {
        logger.debug("Enter into authentication ...");
        Hashtable env = new Hashtable();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, password);
        // cn=属于哪个组织结构名称，ou=某个组织结构名称下等级位置编号
        //env.put(Context.SECURITY_PRINCIPAL, "cn=" + account + ", ou=Level0" + i + "00," + LDAP_URL);
        env.put(Context.SECURITY_PRINCIPAL, getLdapUserFullName(account));
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
        env.put(Context.PROVIDER_URL, LDAP_URL);

        try {
            // 连接LDAP进行认证
            ctx = new InitialDirContext(env);
            logger.info("【" + account + "】用户于【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "】登陆系统成功");
        } catch (Exception e) {
            processExcepiton(e);
        }
    }

    /**
     * 对异常的封装
     *
     * @param e
     * @throws BusinessException
     */
    private static void processExcepiton(Exception e) throws BusinessException {
        logger.debug("Enter into processExcepiton(Exception e)");
        logger.info(e.getMessage());
        if (e.getMessage().indexOf(" 525") >= 0) {
            throw new LoginException("您输入的账号不存在");
        } else if (e.getMessage().indexOf(" 775") >= 0) {
            throw new LoginException("您输入的的账号被锁定了");
        } else if (e.getMessage().indexOf(" 52e") >= 0) {
            throw new LoginException("您输入你的密码不正确。");
        } else if (e.getMessage().indexOf(" 530") >= 0) {
            throw new LoginException("由于账户策略限制，您此时不允许登录。");
        } else if (e.getMessage().indexOf(" 531") >= 0) {
            throw new LoginException("由于账户策略限制，您不允许登录");
        } else if (e.getMessage().indexOf(" 532") >= 0) {
            throw new LoginException("您的域帐户密码已过期");
        } else if (e.getMessage().indexOf(" 533") >= 0) {
            throw new LoginException("您的域帐户已停用");
        } else if (e.getMessage().indexOf("701") >= 0) {
            throw new LoginException("您的域帐户已停用");
        } else if (e.getMessage().indexOf("773") >= 0) {
            throw new LoginException("您的域帐户必须重设密码");
        } else {
            throw new LoginException("非法用户");
        }
    }


    // 关闭LDAP服务器连接
    public static void closeCtx() {

        try {
            if (null != ctx) {
                ctx.close();
            }
        } catch (NamingException ex) {
            logger.info("--------->> 关闭LDAP连接失败");
        }
    }

    /**
     * 获取用户全名
     * @param userName
     * @return
     */
    private static String getLdapUserFullName(String userName){
        if(DOMAIN_FORMAT.equals("prefix")){
            return getPrefixUserFullName(userName);
        }else if(DOMAIN_FORMAT.equals("suffix")){
            return getSufixUserFullName(userName);
        }
        return userName;
    }


    /**
     * 获取前缀用户全名    domain\\user
     * @param userName
     * @return
     */
    private static String getPrefixUserFullName(String userName){
        String prefix = DOMAIN+"\\";
        userName = userName.trim();
        if (userName.startsWith(prefix) == false)
            userName = prefix+userName;
        return userName;
    }


    /**
     * 获取前缀用户全名    user@domain
     * @param userName
     * @return
     */
    private  static String getSufixUserFullName(String userName){
        String dns = "@" + DOMAIN;

        userName = userName.trim();
        if (userName.endsWith(dns) == false)
            userName += dns;
        return userName;
    }


    /**** 测试 ****/
    public static void main(String[] args) {
        try {
            LdapUtil.authentication("hq01233", "Windows2006");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        LdapUtil.closeCtx();
    }
}
