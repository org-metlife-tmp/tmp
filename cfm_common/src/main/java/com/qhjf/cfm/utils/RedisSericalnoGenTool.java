package com.qhjf.cfm.utils;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import redis.clients.jedis.Jedis;

import java.util.Date;

public class RedisSericalnoGenTool {

    private static RedisCacheConfigSection section = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    private final static String SERIAL_KEY_TEMPLATE = "payserial:%s";

    private final static String CHECK_VOUCHER_SEQNO = "voucherseqno:%s";

    private final static String CHECK_SERIAL_SEQNO = "checkserialseqno:%s";
    
    private final static String THREE_DISK_FILE_SEQNO = "threediskfileseqno:%s";
           
    private final static String CHECK_VOUCHER_FILE_SEQNO = "voucherfileseqno:%s";

    private final static String BILL_SEQNO = "billseqno:%s";
    
    private final static String BANK_SEQNO = "bankseqno:%s";

    private final static String SERIAL_FORMAT = "%s%s%07d";

    public static String genShortSerial() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(SERIAL_KEY_TEMPLATE, now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%06x", sequence);
    }

    public static String genVoucherSeqNo() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(CHECK_VOUCHER_SEQNO, now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%04x", sequence);
    }

    //对账流水号生成
    public static String genCheckSerialSeqNo() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(CHECK_SERIAL_SEQNO, now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return now+String.format("%06x", sequence);
    }

    public static String genVoucherFileSeqNo(String seq) {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(seq, now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%04x", sequence);
    }

    public static String genDiskFileSeqNo(String req) {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(req+"diskfileseqno:%s", now);


        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);

        jedis.close();
        return String.format("%05x", sequence);
    }
    
    public static String genThreeDiskFileSeqNo() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(THREE_DISK_FILE_SEQNO, now);


        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);

        jedis.close();
        return String.format("%03x", sequence);
    }
    
    
    public static String genCCBCDiskFileSeqNo(String req) {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMdd");
        String key = String.format(req+"ccbcdiskfileseqno:%s", now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%08x", sequence);
    }


    public static String genBillSeqNo() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMddHHmmss");
        String key = String.format(BILL_SEQNO, now);
        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        Long sequence = jedis.incr(key);
        jedis.expire(key, 300);
        jedis.close();
        return String.format("%s%06x", now,sequence);
    }

    public static String genBankSeqNo() {
        //获取当前时间，通过当前时间获取redis中的键值。
        String now = DateKit.toStr(new Date(), "yyyyMMddHHmmss");
        String key = String.format(BANK_SEQNO, now);

        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.expire(key, 300);
        jedis.close();
        return String.format("%s%06x", now,sequence);
    }



    public static String genBatchProcessno(String req) {
        String key = String.format("%s", req);
        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%s%06x", req,sequence);
    }

    public static String gmszjflownum(String req) {
        String key = String.format("%s", req);
        Jedis jedis = Redis.use(section.getCacheName()).getJedis();
        //通过redis的incr操作获取递增序列号
        Long sequence = jedis.incr(key);
        jedis.close();
        return String.format("%s%012x", req, sequence);
    }

    public static void main(String[] args) {
        Long id = 000001l;
        String ss = "01869f";
        System.out.println(String.format("%s%04x", "111",id));
        System.out.println(Long.parseLong(ss, 16));
    }

    
    

}
