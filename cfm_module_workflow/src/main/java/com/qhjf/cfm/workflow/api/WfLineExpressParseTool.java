package com.qhjf.cfm.workflow.api;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工作流线点表达式工具
 */
public class WfLineExpressParseTool {

    private static final Pattern EXPRESSPATTERN = Pattern.compile("\\Q${\\E(?<key>[A-Z]+)\\}(?<oper>[^\\$\\{]+)\\Q${\\E(?<value>[0-9,.]+)\\}");


    private static final WfLineExpressParseTool INSTANCE = new WfLineExpressParseTool();

    public static WfLineExpressParseTool getInstance() {
        return INSTANCE;
    }

    private WfLineExpressParseTool(){

    }


    public boolean expressAllParse(WfRequestObj requestObj, String conditionExpress) throws WorkflowException {
        if (conditionExpress == null || conditionExpress.length() <= 0) {
            return true;
        }
        String[] expressArr = getAllExpressArr(conditionExpress);
        if (expressArr == null) {
            return true;
        }
        boolean expressPerParseResult = true;
        for (String expressPer : expressArr) {
            expressPerParseResult = expressPerParse(requestObj, expressPer);
            if (!expressPerParseResult) {
                break;
            }
        }
        return expressPerParseResult;

    }


    public boolean expressPerParse(WfRequestObj requestObj, String express) throws WorkflowException {
        if (express == null || express.length() <= 0 || express.trim().length() <= 0) {
            return true;
        }

        Matcher matcher = EXPRESSPATTERN.matcher(express);
        if (matcher.matches()) {
            String expressKey = matcher.group("key").trim();
            String expressOperatorType = matcher.group("oper").trim();
            String expressValue = matcher.group("value");

            if (expressKey == null || expressOperatorType == null || expressValue == null || expressKey.length() <= 0 || expressOperatorType.length() <= 0 || expressValue.length() <= 0) {
                throw new WorkflowException("非法的表达式：" + express);
            }
            WebConstant.WfExpressType expressType = WebConstant.WfExpressType.getTypeByName(expressKey);
            if (expressType == null) {
                throw new WorkflowException("表达式的KEY值未定义：" + express);
            }

            Object modelValue = getFieldValueByExpressName(requestObj, expressType);

            if (expressOperatorType.equals(WorkflowConstant.OperatorType.Greater.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isGreaterThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.GreaterOrEquals.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isGreaterOrEqualsThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.Equals.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isEqualsThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.LessOrEquals.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isLessOrEqualsThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.Less.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isLessThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.isNoEqualsThan.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                BigDecimal expressBigDecimalValue = new BigDecimal(expressValue);
                return isNoEqualsThan(modelValueDec, expressBigDecimalValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.isBetween.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                String[] expressValueArr = expressValue.split(",");
                if (expressValueArr.length != 2) {
                    throw new WorkflowException("非法的表达式：" + express);
                }
                return isBetween(modelValueDec, new BigDecimal(expressValueArr[0]), new BigDecimal(expressValueArr[1]));
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.isNoBetween.getOp())) {
                BigDecimal modelValueDec = (BigDecimal) modelValue;
                String[] expressValueArr = expressValue.split(",");
                if (expressValueArr.length != 2) {
                    throw new WorkflowException("非法的表达式：" + express);
                }
                return isNoBetween(modelValueDec, new BigDecimal(expressValueArr[0]), new BigDecimal(expressValueArr[1]));
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.isIn.getOp())) {
                return isIn(modelValue.toString(), expressValue);
            } else if (expressOperatorType.equals(WorkflowConstant.OperatorType.isNoIn.getOp())) {
                return isNoIn(modelValue.toString(), expressValue);
            } else {
                throw new WorkflowException("无法解析表达式：" + express);
            }

        } else {
            throw new WorkflowException("非法的表达式：" + express);
        }
    }






    /**
     * 将一组表达式拆分成单个表达式
     *
     * @param conditionExpress
     * @return
     */
    private String[] getAllExpressArr(String conditionExpress) {
        if (conditionExpress != null && conditionExpress.length() > 0) {
            return conditionExpress.split(";");
        }
        return null;
    }


    /**
     * 大于运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isGreaterThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult == 1) {
            return true;
        }
        return false;
    }


    /**
     * 大于等于运算
     *
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isGreaterOrEqualsThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult == 1 || compareResult == 0) {
            return true;
        }
        return false;
    }


    /**
     * 等于运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isEqualsThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult == 0) {
            return true;
        }
        return false;
    }


    /**
     * 小于等于运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isLessOrEqualsThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult == 0 || compareResult == -1) {
            return true;
        }
        return false;
    }


    /**
     * 小于运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isLessThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult == -1) {
            return true;
        }
        return false;
    }

    /**
     * 不等于运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isNoEqualsThan(BigDecimal modelValue, BigDecimal expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        int compareResult = modelValue.compareTo(expressValue);
        if (compareResult != 0) {
            return true;
        }
        return false;
    }


    /**
     * 区间运算
     * @param modelValue
     * @param expressMinValue
     * @param expressMaxValue
     * @return
     */
    public boolean isBetween(BigDecimal modelValue, BigDecimal expressMinValue, BigDecimal expressMaxValue) {
        if (modelValue == null || expressMinValue == null || expressMaxValue == null) {
            return false;
        }
        int compareResultMin = modelValue.compareTo(expressMinValue);
        int compareResultMax = modelValue.compareTo(expressMaxValue);
        //大于等于  &&  小于
        if ((compareResultMin == 1 || compareResultMin == 0) && (compareResultMax == -1)) {
            return true;
        }
        return false;
    }


    /**
     * 不在区间运算
     * @param modelValue
     * @param expressMinValue
     * @param expressMaxValue
     * @return
     */
    public boolean isNoBetween(BigDecimal modelValue, BigDecimal expressMinValue, BigDecimal expressMaxValue) {
        if (modelValue == null || expressMinValue == null || expressMaxValue == null) {
            return false;
        }
        int compareResultMin = modelValue.compareTo(expressMinValue);
        int compareResultMax = modelValue.compareTo(expressMaxValue);
        if (compareResultMin == -1 && compareResultMax == 1) {
            return true;
        }
        return false;
    }


    /**
     * 包含运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isIn(String modelValue, String expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        expressValue = "," + expressValue + ",";
        int compareResult = expressValue.indexOf("," + modelValue + ",");
        if (compareResult != -1) {
            return true;
        }
        return false;
    }


    /**
     * 不包含运算
     * @param modelValue
     * @param expressValue
     * @return
     */
    public boolean isNoIn(String modelValue, String expressValue) {
        if (modelValue == null || expressValue == null) {
            return false;
        }
        expressValue = "," + expressValue + ",";
        int compareResult = expressValue.indexOf("," + modelValue + ",");
        if (compareResult == -1) {
            return true;
        }
        return false;
    }


    private <T> T getFieldValueByExpressName(WfRequestObj requestObj, WebConstant.WfExpressType type) throws WorkflowException{
        return requestObj.getFieldValue(type);
    }

    public static void main(String[] args) throws WorkflowException {
        WfLineExpressParseTool tool = WfLineExpressParseTool.getInstance();
        final Record record = new Record().set("amount",new BigDecimal(12345.67));
        boolean bool = tool.expressPerParse(new WfRequestObj(WebConstant.MajorBizType.INNERDB, "dbt", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                return record.get("amount");
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return null;
            }
        },"${AMOUNT}~${20.123,100000.12}");
        System.out.println(bool);
    }


}
