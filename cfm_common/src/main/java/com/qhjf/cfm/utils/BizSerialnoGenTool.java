package com.qhjf.cfm.utils;

import com.qhjf.cfm.web.constant.WebConstant;

public class BizSerialnoGenTool{

    private static final BizSerialnoGenTool instance = new BizSerialnoGenTool();

    private BizSerialnoGenConfig config;

    public static BizSerialnoGenTool getInstance(){
        return instance;
    }


    public String getSerial(WebConstant.MajorBizType type){
        this.config.setBizType(type);
        return  next();
    }

    private String next() {
    	String seqNo = RedisSericalnoGenTool.genBillSeqNo();
        StringBuffer sb = new StringBuffer(this.config.getPrefix())
                .append(this.config.getSplitString())
                .append(seqNo.substring(0,8)).append(this.config.getSplitString())
                .append(seqNo.substring(8));
        return sb.toString();
    }


    private class BizSerialnoGenConfig implements  SerialnoGenConfig{

        private WebConstant.MajorBizType bizType;

        @Override
        public String getSplitString() {
            return "";
        }

        @Override
        public int getInitial() {
            return 1;
        }

        @Override
        public String getPrefix() {
            return bizType.getPrefix();
        }

        @Override
        public int getRollingInterval() {
            return 1;
        }

        @Override
        public int getPadding() {
            return 6;
        }

        public WebConstant.MajorBizType getBizType() {
            return bizType;
        }

        public void setBizType(WebConstant.MajorBizType bizType) {
            this.bizType = bizType;
        }
    }


    private BizSerialnoGenTool() {
        this.config = new BizSerialnoGenConfig();
    }


   

    public static void main(String[] args) {
        BizSerialnoGenTool tool = BizSerialnoGenTool.getInstance();
        for(int i = 0 ; i< 200 ; i++){
            System.out.println(tool.getSerial(WebConstant.MajorBizType.ACC_OPEN_INT));
            try {
                // 括号内的参数是毫秒值,线程休眠1s
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
