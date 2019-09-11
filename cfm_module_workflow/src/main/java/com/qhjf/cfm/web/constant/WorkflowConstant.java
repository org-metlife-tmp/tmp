package com.qhjf.cfm.web.constant;

public interface WorkflowConstant extends  WebConstant {

    @ConstantAnnotation("受理类型")
    enum AssigneeType implements   WorkflowConstant{
        AGREED(1,"同意"), REFUSED(2,"拒绝"),APPEND(3,"加签");

        int key ;
        String desc;
        AssigneeType(int key , String desc){
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("回退策略")
    enum RejectStrategy implements WorkflowConstant{
        BACKCOMMIT(1,"回退到提交人"), BACKVEFORE(2,"回退到上一级"), CUSTOM(3,"自定义");
        int key ;
        String desc;

        RejectStrategy(int key , String desc){
            this.key = key;
            this.desc = desc;
        }
        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }


    @ConstantAnnotation("工作流类型")
    enum WorkFlowType implements  WorkflowConstant{
        BUILDIN(1,"内置"),NORMAL(2,"普通"),CUSTOM(3,"自定义");

        int key ;
        String desc;

        WorkFlowType(int key , String desc){
            this.key = key;
            this.desc = desc;
        }
        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }


    @ConstantAnnotation("工作流特殊节点")
    enum Endpoint implements WorkflowConstant{
        STARTPOINT(-1,"开始节点"), ENDPOINT(-2,"结束节点");

        int key ;
        String desc;

        Endpoint(int key , String desc){
            this.key = key;
            this.desc = desc;
        }
        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }


    enum OperatorType implements  WorkflowConstant{
        Greater(1,"大于",">"), GreaterOrEquals(2,"大于等于",">="), Equals(3,"等于","="), LessOrEquals(4,"小于等于","<="),
        Less(5,"小于","<"), isNoEqualsThan(6,"不等于","!="), isBetween(7,"在..区间内","~"), isNoBetween(8,"不在..区间内","!~"),
        isIn(9,"在其中","isIn"), isNoIn(10,"不在其中","isNoIn");;

        int key ;
        String desc;
        String op;

        OperatorType(int key , String desc, String op){
            this.key = key;
            this.desc = desc;
            this.op = op;
        }
        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public String getOp() {
            return op;
        }
    }


}
