package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.kit.Kv;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtKit {

    private static final Logger logger = LoggerFactory.getLogger(JwtKit.class);

    public enum Mode {
        FILE, REDIS;
    }

    public static String header = "Authorization";  // 默认请求头标识符
    public static String tokenPrefix = "Bearer ";    // 默认token前缀
    public static String secret = "default";         // 默认私钥
    public static Long expiration = 86400L;          // 默认失效时间(秒) 1天

    public static Long tokenTimeOut = 1800L;            // token 超时时间(30分钟)1800s
    public static IJwtUserService userService = null;//  需要注入的服务参数

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";

    private static IJWTSerializer serializer = new FileJWTSerializer();
    private static Mode mode = Mode.FILE;

    public static void setMode(Mode changeMode) {
        logger.debug("Enter into setMode ");
        if (!changeMode.equals(mode)) {
            logger.debug(mode.name() + " change to " + changeMode.name());
            mode = changeMode;
            switch (mode) {
                case FILE:
                    serializer = new FileJWTSerializer();
                    break;
                case REDIS:
                    serializer = new RedisJWTSerializer();
                    break;
            }
        }
        mode = mode;
        logger.debug("leave setMode ");
    }

    public static Kv getKv() {
        return serializer.deSerialize();
    }

    private static void write(Kv stroe) {
        serializer.serialize(stroe);
    }


    /**
     * 重构缓存
     */
    public static void storeReset() {
        serializer.reStroe();
    }


    /**
     * 切换uodp
     *
     * @param cur_uodp_id
     * @return
     */
    public static UserInfo switchUodp(long cur_uodp_id, String loginName) {
        Kv jwtStore = getKv();
        UserInfo userInfo = (UserInfo) jwtStore.get(loginName);
        userInfo.setCur_uodp_Id(cur_uodp_id);
        userInfo.refMenuInfo();

        jwtStore.set(loginName, userInfo);// 在服务器端储存jwtBean
        logger.debug("start save in memeory....");
        write(jwtStore);
        logger.debug("leave save in memeory");

        return userInfo;

    }

    /**
     * 通过 用户名密码 获取 token 如果为null，那么用户非法
     *
     * @param userName
     * @param password
     * @return
     */
    public static String getToken(String userName, String password) throws BusinessException {
        logger.debug("enter into getToken(String userName, String password)");
        if (userService == null) {
            logger.error("userService 为 空/null");
            throw new RuntimeException("userService 为 空/null");
        }

        IJwtAble user = userService.login(userName, password);
        if (user == null) {
            logger.debug("user is null , token return null");
            return null;
        }
        // 构建服务器端储存对象
        String token = generateToken(userName);
        user.refLastToken(token);   // 关联token

        logger.debug("jwtStore save user in server :" + userName);
        Kv jwtStore = getKv();
        jwtStore.set(userName, user);// 在服务器端储存jwtBean
        logger.debug("start save in memeory....");
        write(jwtStore);
        logger.debug("leave save in memeory");
        // 用userName创建token
        logger.debug("用userName创建token");

        logger.debug("记录token有效时间");
        serializer.recordTokenEffectiveTime(token,tokenTimeOut);


        return tokenPrefix + token;


    }

    /**
     * 通过旧的token来交换新的token
     *
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        if (userService == null)
            throw new RuntimeException("userService 为 空/null");
        if (token == null || token.length() < tokenPrefix.length())
            throw new RuntimeException("token被解析");
        String trueToken = token.substring(tokenPrefix.length(), token.length());
        if (isTokenExpired(trueToken)) { // 如果已经过期
            // 解析出用户名
            String userName = getJwtUser(trueToken);
            Kv jwtStore = getKv();
            IJwtAble jwtBean = (IJwtAble) jwtStore.get(userName);
            if (jwtBean == null) return token;
            return generateToken(userName); // 在此匹配生成token
        }
        return token;
    }

    /**
     * 从用户Token中获取用户名信息
     *
     * @param authToken
     * @return
     */
    public static String getJwtUser(String authToken) {
        String jwtUser = null;
        if(serializer.isTokenTimeOut(authToken)){
            logger.debug("authToken is time out");
            return jwtUser;
        }
        try {
            final Claims claims = getClaimsFromToken(authToken);
            jwtUser = claims != null ? claims.getSubject() : null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return jwtUser;
        }
    }

    /**
     * 获取 getJwtBean 对象
     *
     * @param jwtUser
     * @return
     */
    public static IJwtAble getJwtBean(String jwtUser, Date created) {
        logger.debug("Enter into getJwtBean(String jwtUser, Date created)");
        IJwtAble jwtBean = null;
        try {
            jwtBean = (IJwtAble) getKv().get(jwtUser);
            logger.debug("jwtBean is:" + jwtBean);
            //TODO 更新 IJWTAble 最后操作时间！
            if (created == null || jwtBean == null || created.before(jwtBean.getLastModifyPasswordTime()))/* 如果创建时间在修改密码之前 **/ {
                logger.debug("jwtBean is null");
                jwtBean = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwtBean;
    }

    /**
     * 获取Token的过期日期
     *
     * @param token
     * @return
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 获取用户Token的创建日期
     *
     * @param authToken
     * @return
     */
    public static Date getCreatedDateFormToken(String authToken) {
        logger.debug("enter into getCreatedDateFormToken(String authToken)");
        Date creatd;
        try {
            final Claims claims = getClaimsFromToken(authToken);
            creatd = new Date((Long) claims.get(CLAIM_KEY_CREATED)); // 把时间戳转化为日期类型
        } catch (Exception e) {
            logger.error(e.getMessage());
            creatd = null;
        }
        logger.debug("creatd  is:" + creatd);
        return creatd;
    }

    /**
     * 判断Token是否已经过期
     *
     * @param token
     * @return
     */
    protected static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        if(expiration != null){
            return expiration.before(new Date());
        }else{
            //若expiration为空，则token 已过期
            return true;
        }

    }

    /**
     * 将Token信息解析成Claims
     *
     * @param token
     * @return
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            serializer.recordTokenEffectiveTime(token,tokenTimeOut);
        } catch (Exception e) {
            //若是token过期，通过ExpiredJwtException 获取之前的claims信息
            if(e instanceof  ExpiredJwtException){
                if(e instanceof ExpiredJwtException){
                    claims =  ((ExpiredJwtException) e).getClaims();
                }
            }

        }
        return claims;
    }

    /**
     * 根据用户信息生成Token
     *
     * @param userName
     * @return
     */
    private static String generateToken(String userName) {
        logger.debug("Enter into generateToken(String userName) ");
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(CLAIM_KEY_USERNAME, userName);
        claims.put(CLAIM_KEY_CREATED, new Date());
        logger.debug("leave  generateToken(String userName) ");
        return generateToken(claims);
    }

    /**
     * 根据Claims信息来创建Token
     *
     * @param claims
     * @returns
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成令牌的过期日期
     *
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }


    public static void main(String[] args) {
        /*String token ="Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjY5ODE5MTEsInN1YiI6ImFkbWluIiwiY3JlYXRlZCI6MTUyNjM3NzExMTE3NX0.WQtqK7xb7_5G2gw3fjfXt3AmcsHp7QrAYV3ZVTPnkFzuxVChXtUi_6-25Yz9Khy0fmDibv-hnYlLJOwU0b6uYg";
        String trueToken = token.substring(tokenPrefix.length(), token.length());
        Claims claims = getClaimsFromToken(trueToken);
        String str = claims.getSubject();
        System.out.println(str);*/

        /*byte[] bytes = DatatypeConverter.parseBase64Binary("eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjY5ODE5MTEsInN1YiI6ImFkbWluIiwiY3JlYXRlZCI6MTUyNjM3NzExMTE3NX0.WQtqK7xb7_5G2gw3fjfXt3AmcsHp7QrAYV3ZVTPnkFzuxVChXtUi_6-25Yz9Khy0fmDibv-hnYlLJOwU0b6uYg");
        System.out.println(new String(bytes));*/

        String base64UrlEncodedHeader = null;
        String base64UrlEncodedPayload = null;
        String base64UrlEncodedDigest = null;

        String jwt = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjY5ODE5MTEsInN1YiI6ImFkbWluIiwiY3JlYXRlZCI6MTUyNjM3NzExMTE3NX0.WQtqK7xb7_5G2gw3fjfXt3AmcsHp7QrAYV3ZVTPnkFzuxVChXtUi_6-25Yz9Khy0fmDibv-hnYlLJOwU0b6uYg";
        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt j = parser.parse(jwt);

        System.out.println(j.getHeader().toString());
        System.out.println(j.getBody().toString());

    }

}
