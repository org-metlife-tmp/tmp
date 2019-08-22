package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileJWTSerializer implements IJWTSerializer {

    private static final Logger log = LoggerFactory.getLogger(FileJWTSerializer.class);

    private final static String FILENAME = "jwt_token.store";

    private final static Map<String,Long> TOKEN_TIME = new ConcurrentHashMap<>();


    @Override
    public Kv deSerialize() {
        log.debug("enter into deSerialize ...");
        FileInputStream fis = null;
        ObjectInput store = null;
        Kv result = null;
        try {
            fis = new FileInputStream(new File(PathKit.getRootClassPath() + "/"+FILENAME));
            store = new ObjectInputStream(fis);
            result = (Kv) store.readObject();
        } catch (Exception e) {
            result = Kv.create();
        } finally {
            try {
                if (store != null)
                    store.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(" deSerialize close failed");
            }
        }
        return result;
    }



    @Override
    public void serialize(Kv jwtStore) {
        log.debug("enter into serialize ...");
        FileOutputStream fos = null;
        ObjectOutputStream store = null;
        try {
            File file = new File(PathKit.getRootClassPath() + "/"+FILENAME);
            file.createNewFile();
            fos = new FileOutputStream(file);
            store = new ObjectOutputStream(fos);
            store.writeObject(jwtStore);
        } catch (Exception e) {
            e.printStackTrace();
            jwtStore = Kv.create();
        } finally {
            try {
                if (store != null)
                    store.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(" serialize close failed");
            }
        }
        log.debug("leave  serialize ");

    }

    @Override
    public void reStroe() {
        log.debug("enter into reStroe...");
        FileOutputStream fos = null;
        ObjectOutputStream store = null;
        try {
            File file = new File(PathKit.getRootClassPath() + "/"+FILENAME);
            file.createNewFile();
            fos = new FileOutputStream(file);
            store = new ObjectOutputStream(fos);
            store.writeObject(Kv.create());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (store != null)
                    store.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(" serialize close failed");
            }
        }
        log.debug("leave  reStroe ");
    }

    @Override
    public void recordTokenEffectiveTime(String token, long interval) {
        log.debug("enter into recordTokenEffectiveTime(token,interval) ...");
        if(token != null && !"".equals(token)){
            TOKEN_TIME.put(token,(new Date()).getTime()+interval*1000);
        }
        log.debug("leave   recordTokenEffectiveTime(token, interval)  !");
    }

    @Override
    public boolean isTokenTimeOut(String token) {
        log.debug("enter into isTokenTimeOut(token) ...");
        if(TOKEN_TIME.containsKey(token)){
            long cur = new Date().getTime();
            return cur > TOKEN_TIME.get(token);
        }else{
            //不含有此token，则认为超时
            return true;
        }
    }
}
