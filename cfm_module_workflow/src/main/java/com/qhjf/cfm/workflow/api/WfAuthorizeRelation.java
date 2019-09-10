package com.qhjf.cfm.workflow.api;


import com.jfinal.ext.kit.DateKit;

import java.util.Calendar;
import java.util.Date;

/**
 * 工作流授权关系对象，作为Cache中的value存在，它的key值为被授权人的usr_id
 */
public class WfAuthorizeRelation {


    private Long authorize_usr ; //授权人usr_id

    private Date startDate ;   //授权开始时间 yyyy-MM-dd

    private Date endDate;      //授权结束时间 yyyy-MM-dd

    public WfAuthorizeRelation(Long authorize_usr, Date startDate, Date endDate) {
        this.authorize_usr = authorize_usr;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public WfAuthorizeRelation(Long authorize_usr, String startDate, String endDate) {
        this.authorize_usr = authorize_usr;
        this.startDate = DateKit.toDate(startDate);
        this.endDate =  DateKit.toDate(endDate);
    }


    public Long getAuthorize_usr() {
        return authorize_usr;
    }

    public void setAuthorize_usr(Long authorize_usr) {
        this.authorize_usr = authorize_usr;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 根据startDate和endDate 通系统当前时间进行比较，判断授权关系是否有效
     * @return
     */
    public boolean isValidate(){
        Date now = new Date(System.currentTimeMillis()); //获取系统当前时间;
        return now.getTime() >= startDate.getTime() && now.getTime() <= endDate.getTime();


    }


    private Date getSDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND,0);
        return cal.getTime();
    }


    private Date getEDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND,59);
        return cal.getTime();
    }
}
