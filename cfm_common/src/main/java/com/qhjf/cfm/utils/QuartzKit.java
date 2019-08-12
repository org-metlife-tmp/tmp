package com.qhjf.cfm.utils;


import com.qhjf.cfm.exceptions.QuartzCronFormatException;
import com.qhjf.cfm.web.constant.WebConstant;

public class QuartzKit {

    public static String cronFormat(Integer dateType, String dateDetail, String time) throws QuartzCronFormatException {
        StringBuffer cron = new StringBuffer("0");
        ;
        if (dateType == null || time == null || time.length() <= 0) {
            throw new QuartzCronFormatException("时间格式有误");
        }
        String[] timeArr = time.split(":");
        if (timeArr.length != 2) {
            throw new QuartzCronFormatException("时间格式有误");
        }
        Integer timeHour = Integer.parseInt(timeArr[0]);
        if (timeHour < 0 || timeHour > 23) {
            throw new QuartzCronFormatException("时间格式有误");
        }
        Integer timeMinute = Integer.parseInt(timeArr[1]);
        if (timeMinute < 0 || timeMinute > 59) {
            throw new QuartzCronFormatException("时间格式有误");
        }
        cron = cron.append(" ").append(timeMinute).append(" ").append(timeHour);
        if (dateType == WebConstant.CollOrPoolFrequency.DAILY.getKey()) {
            cron = cron.append(" * * ?");
        } else if (dateType == WebConstant.CollOrPoolFrequency.WEEKLY.getKey()) {
            validate(7, dateDetail);

            cron = cron.append(" ? * ").append(dateDetail);
        } else if (dateType == WebConstant.CollOrPoolFrequency.MONTHLY.getKey()) {
            validate(31, dateDetail);

            cron = cron.append(" ").append(dateDetail).append(" * ?");
        } else {
            throw new QuartzCronFormatException("时间格式有误");
        }
        return cron.toString();
    }


    private static void validate(Integer dateEnd, String dateDetail) throws QuartzCronFormatException {
        String[] dateDetailArr;
        if (dateDetail == null || dateDetail.length() <= 0) {
            throw new QuartzCronFormatException("时间格式有误");
        }
        if (dateDetail.indexOf(",") >= 0) {
            dateDetailArr = dateDetail.split(",");
            for (String dateDetailPer : dateDetailArr) {
                if (dateDetailPer == null || dateDetailPer.length() <= 0) {
                    throw new QuartzCronFormatException("时间格式有误");
                }
                Integer dateDetailInt = Integer.parseInt(dateDetailPer);
                if (dateDetailInt < 1 || dateDetailInt > dateEnd) {
                    throw new QuartzCronFormatException("时间格式有误");
                }
            }
        } else if (dateDetail.indexOf("-") >= 0) {
            dateDetailArr = dateDetail.split(",");
            if (dateDetailArr.length != 2) {
                throw new QuartzCronFormatException("时间格式有误");
            }
            String dateDetailStart = dateDetailArr[0];
            String dateDetailEnd = dateDetailArr[1];

            if (dateDetailStart == null || dateDetailStart.length() <= 0 || dateDetailEnd == null || dateDetailEnd.length() <= 0) {
                throw new QuartzCronFormatException("时间格式有误");
            }

            Integer dateDetailStartInt = Integer.parseInt(dateDetailStart);
            Integer dateDetailEndInt = Integer.parseInt(dateDetailEnd);

            if (dateDetailStartInt < 1 || dateDetailStartInt > dateEnd) {
                throw new QuartzCronFormatException("时间格式有误");
            }
            if (dateDetailEndInt < 1 || dateDetailEndInt > dateEnd) {
                throw new QuartzCronFormatException("时间格式有误");
            }
            if (dateDetailStartInt >= dateDetailEndInt) {
                throw new QuartzCronFormatException("时间格式有误");
            }

        } else {
            Integer dateDetailInt = Integer.parseInt(dateDetail);
            if (dateDetailInt < 1 || dateDetailInt > dateEnd) {
                throw new QuartzCronFormatException("时间格式有误");
            }
        }
    }

    public static void main(String[] agrs) throws QuartzCronFormatException {
        System.out.println(cronFormat(2, "7", "11:09"));
    }

}