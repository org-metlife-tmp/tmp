package com.qhjf.cfm.utils;

import org.junit.Test;

public class RegexUtilsTest {

    @Test
    public void testPinppi(){
        org.junit.Assert.assertTrue(RegexUtils.isPinYin("shang"));

        org.junit.Assert.assertTrue(RegexUtils.isPinYin("shanghai"));
    }

}