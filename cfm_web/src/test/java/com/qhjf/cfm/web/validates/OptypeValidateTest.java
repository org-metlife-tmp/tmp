package com.qhjf.cfm.web.validates;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.web.constant.WebConstant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.BatchUpdateException;

public class OptypeValidateTest {

    private JSONObject obj ;

    @Before
    public void setup(){
        String str = "{\"optype\":\"org_list\", \"params\":{\"is_default\":1, \"termail\": \"2\", \"pay_or_recv\" : \"3\"}}";
        obj = JSON.parseObject(str);
    }


    @Test
    public void testOPtype() {
        try {
            OptypeValidate val = new OptypeValidate("org_list");
            org.junit.Assert.assertTrue(val.validate(obj));
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        try{
            OptypeValidate val = new OptypeValidate("login");
            org.junit.Assert.assertTrue(val.validate(obj));
            org.junit.Assert.fail("ReqValidateException not throw");
        }catch(BusinessException e){
            org.junit.Assert.assertTrue(e instanceof ReqValidateException);
            org.junit.Assert.assertTrue(e.getMessage().indexOf("optype不正确") >= 0);
        }

    }

    @Test
    public void testRequied(){
        RequiredParamsValidate vld = new RequiredParamsValidate(new String[]{"is_default","pay_or_recv"});
        try{
            org.junit.Assert.assertTrue(vld.validate(obj));
        }catch(Exception e){
            e.printStackTrace();
        }

        vld = new RequiredParamsValidate(new String[]{"is_default","pay_or_recv", "must"});
        try{
            vld.validate(obj);
            org.junit.Assert.fail("ReqValidateException not throw");
        }catch(Exception e){
            org.junit.Assert.assertTrue(e instanceof ReqValidateException);
            org.junit.Assert.assertTrue(e.getMessage().indexOf("must") >= 0);
        }
    }


    @Test
    public void testQT(){
        OptypeValidate val = new OptypeValidate("org_list", new RequiredParamsValidate(new String[]{"is_default","pay_or_recv"}));
        try{
            org.junit.Assert.assertTrue(val.validate(obj));
        }catch(Exception e){
            e.printStackTrace();
        }
        val = new OptypeValidate("org_list", new RequiredParamsValidate(new String[]{"is_default","pay_or_recv","must1"}));
        try{
            val.validate(obj);
            org.junit.Assert.fail("ReqValidateException not throw");
        }catch(Exception e){
            org.junit.Assert.assertTrue(e instanceof ReqValidateException);
            org.junit.Assert.assertTrue(e.getMessage().indexOf("must1") >= 0);
        }

        val = new OptypeValidate("login", new RequiredParamsValidate(new String[]{"is_default","pay_or_recv","must"}));
        try{
            val.validate(obj);
            org.junit.Assert.fail("ReqValidateException not throw");
        }catch(Exception e){
            org.junit.Assert.assertTrue(e instanceof ReqValidateException);
            org.junit.Assert.assertTrue(e.getMessage().indexOf("optype") >= 0);
        }
    }


    @Test
    public void testWebConstantVal(){
        WebConstantParamsValidate val = new WebConstantParamsValidate("is_default", WebConstant.YesOrNo.class);
        try{
            org.junit.Assert.assertTrue(val.validate(obj));
        }catch(Exception e){
            e.printStackTrace();
        }
        val = new WebConstantParamsValidate("pay_or_recv", WebConstant.PayOrRecv.class);
        try{
            val.validate(obj);
            org.junit.Assert.fail("ReqValidateException not throw");
        }catch(Exception e){
            org.junit.Assert.assertTrue(e instanceof ReqValidateException);
            org.junit.Assert.assertTrue(e.getMessage().indexOf("设置有误") >= 0);
        }
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();


    @Test
    public void testQt2() throws BusinessException{
        OptypeValidate vld = new OptypeValidate("org_list",
                new RequiredParamsValidate(new String[]{"is_default"},
                        new WebConstantParamsValidate("blank",WebConstant.YesOrNo.class)));
        org.junit.Assert.assertTrue(vld.validate(obj));

    }

    @Test(expected = ReqValidateException.class)
    public void testQt3() throws BusinessException {
        OptypeValidate vld = new OptypeValidate("org_list",
                new RequiredParamsValidate(new String[]{"is_default"},
                        new WebConstantParamsValidate("pay_or_recv",WebConstant.PayOrRecv.class)));
        vld.validate(obj);
    }

    @Test
    public void testQt4() throws BusinessException {
        OptypeValidate vld = new OptypeValidate("org_list",
                new RequiredParamsValidate(new String[]{"is_default","must"},
                        new WebConstantParamsValidate("pay_or_recv",WebConstant.PayOrRecv.class)));
        thrown.expect(ReqValidateException.class);
        thrown.expectMessage("请求体params参数[must]不能为空！");
        vld.validate(obj);
    }

}