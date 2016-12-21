package com.sogou.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 时间相关的工具类
 */
public final class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    private TimeUtil() {

    }

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYY_M_D_H_M_S = "yyyy-M-d H:m:s";
    public static final String M_D = "M.d";
    public static final String M = "M";

    private static final Map<String, FastDateFormat> FAST_DATE_FORMAT_MAP = initFormatMap();

    /* 指定日期 */
    public enum DATEOffSET {
        MON_START, MON_END, QUA_START, QUA_END, WEE_START, WEE_END
    }

    private static Map<String, FastDateFormat> initFormatMap() {
        Map<String, FastDateFormat> formatMap = Maps.newHashMapWithExpectedSize(10);
        formatMap.put(YYYYMM, FastDateFormat.getInstance(YYYYMM));
        formatMap.put(YYYY_MM, FastDateFormat.getInstance(YYYY_MM));
        formatMap.put(YYYYMMDD, FastDateFormat.getInstance(YYYYMMDD));
        formatMap.put(YYYY_MM_DD, FastDateFormat.getInstance(YYYY_MM_DD));
        formatMap.put(YYYY_MM_DD_HH_MM, FastDateFormat.getInstance(YYYY_MM_DD_HH_MM));
        formatMap.put(YYYYMMDDHHMMSS, FastDateFormat.getInstance(YYYYMMDDHHMMSS));
        formatMap.put(YYYY_MM_DD_HH_MM_SS, FastDateFormat.getInstance(YYYY_MM_DD_HH_MM_SS));
        return formatMap;
    }

    /**
     * 根据指定模式获得一个 FastDateFormat
     *
     * @param pattern
     * @return
     */
    public static FastDateFormat getFastDateFormat(String pattern) {
        FastDateFormat fastDateFormat = FAST_DATE_FORMAT_MAP.get(pattern);
        if (fastDateFormat == null) {
            fastDateFormat = FastDateFormat.getInstance(pattern);
        }
        return fastDateFormat;
    }

    /**
     * 取得当前时间
     *
     * @return
     */
    public static Timestamp nowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据指定模式解析日期
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static final Date parseDate(String dateStr, String pattern) {
        try {
            return DateUtils.parseDate(dateStr, new String[]{pattern});
        } catch (ParseException e) {
            LOGGER.error(String.format("parse date error. %s ， %s", dateStr, pattern), e);
            return null;
        }
    }

    /**
     * 获取格式化后当天
     *
     * @param pattern
     * @return
     */
    public static final String formatToday(String pattern) {
        return formatDate(new Date(), pattern);
    }

    /**
     * 获取格式化后的昨天
     *
     * @param pattern
     * @return
     */
    public static final String formatYesterday(String pattern) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return formatDate(calendar.getTime(), pattern);
    }

    /**
     * 将Date format成指定的模式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String formatDate(Date date, String pattern) {
        FastDateFormat fastDateFormat = getFastDateFormat(pattern);
        if (fastDateFormat != null) {
            return fastDateFormat.format(date);
        }
        return null;
    }

    /**
     * 格式化成完整时间 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static final String formatYYYYMMDDHHMMSS(Date date) {
        return formatDate(date, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 指定时刻是否在当前时刻之前 ：通用方法，处理以下场景
     * <p/>
     * 1.是否在当前时刻之前  pattern = yyyyMMdd HHmmSS
     * 2.是否在当天之前     pattern = yyyyMMdd
     * 3.是否在当月之前     pattern = yyyyMM
     * 4.是否在当年之前     pattern = yyyy
     *
     * @param date
     * @param pattern
     * @return
     */
    public static boolean isBeforeNow(Date date, String pattern) {
        FastDateFormat dateFormat = getFastDateFormat(pattern);
        Date now = parseDate(dateFormat.format(new Date()), pattern);
        return date.before(now);
    }

    /**
     * 解析日期字符串，转换成指定模式字符串
     *
     * @param date
     * @param fromPattern
     * @param toPattern
     * @return
     */
    public static String parseAndTransDate(String date, String fromPattern, String toPattern) {
        return formatDate(parseDate(date, fromPattern), toPattern);
    }

    /**
     * 将util.Date转换为sql.Date
     *
     * @param date
     * @return
     */
    public static java.sql.Date convertToSqlDate(Date date) {
        Preconditions.checkArgument(date != null);
        return new java.sql.Date(date.getTime());
    }

    /**
     * 输入时间，得到时间所属季度名
     *
     * @param date 时间
     * @return{@link String}季度名
     */
    public static String getQuarter(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        String qua = "";
        if (month >= 0 && month <= 2) {
            qua = "Q1";
        } else if (month >= 3 && month <= 5) {
            qua = "Q2";
        } else if (month >= 6 && month <= 8) {
            qua = "Q3";
        } else {
            qua = "Q4";
        }
        return String.valueOf(c.get(Calendar.YEAR)) + "-" + qua;
    }

    /**
     * 输入时间，得到时间所属该年第几周周度名
     *
     * @param date 时间
     * @return{@link String}第几周周度名
     */
    public static String getWeekOfYear(Date date) {
        Calendar sta = Calendar.getInstance();
        sta.setTime(date);
        sta.setFirstDayOfWeek(Calendar.SUNDAY);
        sta.setMinimalDaysInFirstWeek(7);
        int weekOfYear = sta.getWeekYear();
        int weeks = sta.get(Calendar.WEEK_OF_YEAR);
        String strWeek;
        if (weeks < 10) {
            strWeek = "0" + String.valueOf(weeks);
        } else {
            strWeek = String.valueOf(weeks);
        }

        return String.valueOf(weekOfYear) + "-" + strWeek;
    }

    /**
     * 输入时间，得到时间所属月度名
     *
     * @param date 时间
     * @return {@link String}月度名
     */
    public static String getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        int trueMonth = month + 1;
        String strMonth = "";
        if (trueMonth < 10) {
            strMonth = "0" + String.valueOf(trueMonth);
        } else {
            strMonth = String.valueOf(trueMonth);
        }
        return String.valueOf(c.get(Calendar.YEAR)) + "-" + strMonth;
    }

    /**
     * 输入时间获取星期名
     *
     * @param date 时间
     * @return 星期名
     */
    public static String getWeekDate(java.sql.Date date, boolean isEnter) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        if (isEnter) {
            return date.toString() + "<br>" + weekDays[w];
        } else {
            return date.toString() + weekDays[w];
        }
    }


    /**
     * 输入时间，及偏移标识，输出对应偏移时间
     *
     * @param date   时间
     * @param offset 偏移标识
     * @return 偏移时间
     */
    public static java.sql.Date getOffsetDateByFlag(Date date, DATEOffSET offset) {
        Calendar c = Calendar.getInstance();
        int month;
        c.setTime(date);

        switch (offset) {
            case MON_START:
                c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                return convertToSqlDate(c.getTime());
            case MON_END:
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                return convertToSqlDate(c.getTime());
            case QUA_START:
                month = getQuarterInMonth(c.get(Calendar.MONTH), true);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, 1);
                return convertToSqlDate(c.getTime());
            case QUA_END:
                month = getQuarterInMonth(c.get(Calendar.MONTH), false);
                c.set(Calendar.MONTH, month + 1);
                c.set(Calendar.DAY_OF_MONTH, 0);
                return convertToSqlDate(c.getTime());
            case WEE_START:
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//设置周日
                c.setFirstDayOfWeek(Calendar.SUNDAY);
                return convertToSqlDate(c.getTime());
            case WEE_END:
                c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);//设置周六
                c.setFirstDayOfWeek(Calendar.SUNDAY);
                return convertToSqlDate(c.getTime());
            default:
                return null;
        }
    }

    /**
     * 获取输入时间所在季度所有周
     * 第一周为当前季度第一个周日
     * 最后一周为当前季度最后一天所在周的周六
     *
     * @param date 时间
     * @return {@link Date}周起始时间
     * {@link Date}周结束时间
     * {@link String}周名
     */
    public static List<List<String>> getWeeksOfQuarter(Date date) {
        Date quarterStart = getOffsetDateByFlag(date, DATEOffSET.QUA_START);

        Date quarterEND = getOffsetDateByFlag(date, DATEOffSET.QUA_END);
        List<List<String>> weeks = Lists.newArrayList();
        if (quarterStart == null || quarterEND == null)
            return weeks;
        Date weekStart, weekEnd;
        Calendar sta = Calendar.getInstance();
        sta.setTime(quarterStart);
        int i = 1;
        while (sta.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            sta.add(Calendar.DATE, 1);
        }
        if (sta.getTime().after(date)) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTime(date);
            newDate.add(Calendar.DATE, -7);
            quarterStart = getOffsetDateByFlag(newDate.getTime(), DATEOffSET.QUA_START);
            quarterEND = getOffsetDateByFlag(newDate.getTime(), DATEOffSET.QUA_END);
            sta.setTime(quarterStart);
            while (sta.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                sta.add(Calendar.DATE, 1);
            }
        }

        while (sta.getTime().getTime() <= quarterEND.getTime()) {
            weekStart = sta.getTime();
            sta.add(Calendar.DATE, 6);
            weekEnd = sta.getTime();
            List<String> tmp=Lists.newArrayList();
            tmp.add(formatDate(weekStart,YYYYMMDD));
            tmp.add(formatDate(weekEnd, YYYYMMDD));
            tmp.add("第" + String.valueOf(i) + "周");

            weeks.add(tmp);
            i++;
            sta.add(Calendar.DATE, 1);
        }
        return weeks;
    }

    /**
     * 获取输入时间往前N天
     *
     * @param sDate   开始时间
     * @param isEnter 是否包含回车符
     * @return {@link Date}当前时间
     * {@link String}星期名
     */
    public static List<List<String>> getLastDays(Date sDate, int num, boolean isEnter) {
        List<List<String>> days = Lists.newArrayList();
        if (num <= 0) {
            return days;
        }
        java.sql.Date resDate;
        String resStr;
        for (int i = 0; i < num; i++) {
            resDate = getOffsetDateByNum(sDate, 0 - i);
            resStr = getWeekDate(resDate, isEnter);
            List<String> tmp=Lists.newArrayList();
            tmp.add(formatDate(resDate,YYYYMMDD));
            tmp.add(resStr);
            days.add(tmp);
        }
        return days;
    }

    /**
     * 获取从开始到结束所有月份数据
     *
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return {@link Date}月起始时间
     * {@link Date}月结束时间
     * {@link String}月名
     */
    public static List<List<String>> getMonthsOfAll(Date sDate, Date eDate) {
        List<List<String>> months = Lists.newArrayList();
        Calendar sta = Calendar.getInstance();
        sta.setTime(sDate);
        sta.set(Calendar.DAY_OF_MONTH, 1);
        java.sql.Date monStart, monEnd;
        while (sta.getTime().getTime() <= eDate.getTime()) {
            monStart = getOffsetDateByFlag(sta.getTime(), DATEOffSET.MON_START);
            monEnd = getOffsetDateByFlag(sta.getTime(), DATEOffSET.MON_END);
            List<String> tmp=Lists.newArrayList();
            tmp.add(formatDate(monStart,YYYYMMDD));
            tmp.add(formatDate(monEnd,YYYYMMDD));
            tmp.add(formatDate(sta.getTime(), YYYY_MM));
            months.add(tmp);
            sta.add(Calendar.MONTH, 1);
        }
        return months;
    }

     private static int getQuarterInMonth(int month, boolean isQuarterStart) {
        int months[] = {0, 3, 6, 9};
        if (!isQuarterStart) {
            months = new int[]{2, 5, 8, 11};
        }
        if (month >= 0 && month <= 2) {
            return months[0];
        } else if (month >= 3 && month <= 5) {
            return months[1];
        } else if (month >= 6 && month <= 8) {
            return months[2];
        } else {
            return months[3];
        }
    }

    /**
     * 获取日期偏移值
     *
     * @param date   输入时间
     * @param offset 偏移
     * @return 偏移时间
     */
    public static java.sql.Date getOffsetDateByNum(Date date, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, offset);
        return new java.sql.Date(c.getTime().getTime());
    }

    /**
     * 根据Timestamp，获取偏移之后的Timestamp
     *
     * @return
     */
    public static Timestamp getTimestampBefore(Timestamp stamp, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(stamp);
        c.add(Calendar.MINUTE, -minute);
        return new Timestamp(c.getTime().getTime());
    }
}
