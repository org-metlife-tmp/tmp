package com.qhjf.cfm.utils;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;
import com.qhjf.cfm.web.quartzs.jobs.utils.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

public class test {
    private static Logger log = LoggerFactory.getLogger(SymmetricEncryptUtil.class);
    private static DDHLAConfigSection section;
    private static final String encode = "utf-8";
    private static final String ALGORITHM = "AES";
    private static final String PREFIX = "dHk=<";
    private static final String SUFFIX = ">11111000101";
    private static final String password;
    private static Cipher cipherEnc;
    private static Cipher cipherDec;
    private static final String errMsg = "[%s]:解密失败";
    private static final String DECR_ERR_MSG = "[%s]:解密失败：[%s]";
    private static final String ENCR_ERR_MSG = "[%s]:加密失败：[%s]";
    private static final String REGEX_G8 = "([A-Za-z0-9-]{4})([A-Za-z0-9-]{0,})([A-Za-z0-9-]{4})";
    private static final String REGEX_G4 = "([A-Za-z0-9-]{2})([A-Za-z0-9-]{0,})([A-Za-z0-9-]{2})";

    public static void main(String[] args) throws EncryAndDecryException, ReqValidateException {
        String now = DateKit.toStr(new Date(), "yyyyMMddHHmmss");
        System.out.println(now);

    }
    private void accNoValidate(Record r) throws ReqValidateException, EncryAndDecryException{
        String oldRecvAccNo = r.getStr("pay_acc_no");
        //数据库解密
        String recvAccNo = oldRecvAccNo;
        if (null == recvAccNo) {
            throw new ReqValidateException("TMPPJ:银行账号数据库解密失败");
        }

        //账号非法校验
        //  log.debug("数据库解密[{}]=[{}]", oldRecvAccNo, SymmetricEncryptUtil.accNoAddMask(recvAccNo));
        boolean accNoValidate = ValidateUtil.accNoValidate(recvAccNo);
        if (!accNoValidate) {
            throw new ReqValidateException("TMPPJ:银行账号非法");
        }

        //对称加密
        String newRecvAccNo = SymmetricEncryptUtil.getInstance().encrypt(recvAccNo);
        // log.debug("对称加密后的密文=[{}]", newRecvAccNo);
        r.set("pay_acc_no", newRecvAccNo);
    }
    public String decryptToStr(String content, String encode) throws EncryAndDecryException {
        String result = null;
        byte[] decrypt = this.decrypt(content);
        if (null != decrypt) {
            try {
                result = new String(decrypt, encode);
                return result;
            } catch (UnsupportedEncodingException var6) {
                throw new EncryAndDecryException(String.format("[%s]:解密失败：[%s]", content, var6.getMessage()));
            }
        } else {
            throw new EncryAndDecryException(String.format("[%s]:解密失败", content));
        }
    }
    public byte[] decrypt(String content) throws EncryAndDecryException {
        byte[] result = null;
        if (content != null && !"".equals(content)) {
            result = new byte[0];

            try {
            } catch (Exception var4) {
                throw new EncryAndDecryException(String.format("[%s]:解密失败：[%s]", content, var4.getMessage()));
            }
        }

        return result;
    }
    static {
        section = (DDHLAConfigSection) GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);
        password = "dHk=<" + section.getSecret() + ">11111000101";
        cipherEnc = null;
        cipherDec = null;
        KeyGenerator keyGenerator = null;
        SecureRandom random = null;

        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
            log.error("********************生成密钥失败，需要人工处理**********************");
        }

        random.setSeed(password.getBytes());
        keyGenerator.init(128, random);
        SecretKey key = keyGenerator.generateKey();
        byte[] encodeFormat = key.getEncoded();
        SecretKeySpec key1 = new SecretKeySpec(encodeFormat, "AES");

        try {
            cipherEnc = Cipher.getInstance("AES");
            cipherDec = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException var6) {
            var6.printStackTrace();
            log.error("********************Cipher.getInstance异常，需要人工处理**********");
        } catch (NoSuchPaddingException var7) {
            var7.printStackTrace();
            log.error("********************Cipher.getInstance异常，需要人工处理**********");
        }

        try {
            cipherEnc.init(1, key1);
            cipherDec.init(2, key1);
        } catch (InvalidKeyException var5) {
            var5.printStackTrace();
            log.error("********************cipherEnc.init异常，需要人工处理**********");
        }

    }
}
