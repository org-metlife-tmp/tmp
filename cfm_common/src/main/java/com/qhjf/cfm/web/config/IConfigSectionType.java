package com.qhjf.cfm.web.config;

public interface IConfigSectionType {

    enum DefaultConfigSectionType implements IConfigSectionType {
        DB, Redis, Ldap, Attachment, PreApp , Quartz;

        @Override
        public String getConfigSectionType() {
            return name();
        }
    }

    enum DDHConfigSectionType implements IConfigSectionType {
        DDHOAWS,VOUCHER,DDHLA,DDHEBS,DDHLaRecv,DDHEbsRecv,DDHNCWS;

        @Override
        public String getConfigSectionType() {
            return name();
        }
    }

    enum AttachmenConfigSectionType  implements IConfigSectionType{
        Mongo,FileSave;

        @Override
        public String getConfigSectionType() {
            return null;
        }
    }


    enum BIConfigSectionType implements IConfigSectionType {
        CMBC,ICBC;

        @Override
        public String getConfigSectionType() { return name(); }
    }

    /**
     * 获取配置的类型
     *
     * @return
     */
    String getConfigSectionType();
}
