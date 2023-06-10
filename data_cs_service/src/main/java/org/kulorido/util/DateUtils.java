package org.kulorido.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateCstUtils {
    private static final Logger log = LoggerFactory.getLogger(DateCstUtils.class);

    // utc转北京时间
    public static Date utcToCST(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
            // calendar.getTime() 返回的是Date类型，也可以使用calendar.getTimeInMillis()获取时间戳
            return calendar.getTime();
        } catch (Exception e) {
            log.error("utcToCST error", e);
        }
        return date;
    }

    public static String utcToCSTStr(Date date, String pattern) {
        try {
            return DateFormatUtils.format(DateUtils.addHours(date, 8), pattern);
        } catch (Exception e) {
            log.error("utcToCST error", e);
        }
        return null;
    }


    // 北京时间转utc
    public static Date cstToUtcStr(String dateStr, String pattern) {
        try {
            Date date = DateUtils.parseDate(dateStr, pattern);
            return DateUtils.addHours(date, -8);
        } catch (Exception e) {
            log.error("cstToUtcStr error", e);
        }
        return null;
    }

    /**
     * 得到当前日期的年月日
     * @return
     */
    public static Date getNowUtcDate(){
        Calendar c = Calendar.getInstance();
        // 设置当前时刻的时钟为0
        c.set(Calendar.HOUR_OF_DAY, 0);
        // 设置当前时刻的分钟为0
        c.set(Calendar.MINUTE, 0);
        // 设置当前时刻的秒钟为0
        c.set(Calendar.SECOND, 0);
        // 设置当前的毫秒钟为0
        c.set(Calendar.MILLISECOND, 0);
        // 获取当前时刻的时间戳
        long currMillis = c.getTimeInMillis();
        return DateUtils.addHours(new Date(currMillis), -8);
    }

    /**
     * 年月日时间格式转换，不支持时分秒
     * @param str
     * @return
     */
    public static Date strToDate(String str){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            log.error("strToDate yyyy-MM-dd error", e);
            return new Date();
        }
    }

    /**
     * date 转为 LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime date2LocalDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * date 转为 Timestamp
     * @param date
     * @return
     */
    public static Timestamp date2Timestamp(Date date){
        if (null == date){
            date = new Date();
        }
        return Timestamp.valueOf(date2LocalDateTime(date));
    }

    public static Date initHourMinSecTime(Date date){
        if (DataEmptyUtil.isEmpty(date)){
            return new Date();
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    public static Date initHourMAXSecTime(Date date){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        cal1.set(Calendar.MILLISECOND, 999);
        return cal1.getTime();
    }
}
