package com.mredrock.cyxbs.common.utils;

import com.mredrock.cyxbs.common.BaseApp;
import com.mredrock.cyxbs.common.utils.extensions.SharedPreferencesKt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 学校日历工具类
 * Created by cc on 15/8/23.
 */
public class SchoolCalendar {
    public static final String FIRST_DAY = "first_day";
    Calendar firstDay = new GregorianCalendar(2015, Calendar.SEPTEMBER, 7);
    Calendar currentTime;

    public SchoolCalendar() {
        // 鄙人认为，在这个时候，我们有必要去更新一下firstDay
        long first = SharedPreferencesKt.getDefaultSharedPreferences(BaseApp.Companion.getContext()).getLong(FIRST_DAY, firstDay.getTimeInMillis());
        firstDay.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        firstDay.setTimeInMillis(first);
        currentTime = new GregorianCalendar();
        currentTime.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    public SchoolCalendar(Date date) {
        this();
        currentTime.setTime(date);
    }

    public SchoolCalendar(long timestamp) {
        this(new Date(timestamp * 1000));
    }

    public SchoolCalendar(int year, int month, int day) {
        currentTime = new GregorianCalendar(year, month, day);
    }

    public SchoolCalendar(int week, int weekDay) {
        this();
        currentTime = firstDay;
        currentTime.add(Calendar.DATE, (week - 1) * 7);
        weekDay = weekDay == 0 ? 7 : weekDay;
        currentTime.add(Calendar.DATE, weekDay - getDayOfWeek());
    }

    /**
     * 取这学期过去了多少天
     *
     * @return days 过去的天数
     */
    public int getDayOfTerm() {
        int days = getDeltaT(currentTime, firstDay);
        if (days >= 0) {
            days++;
        }
        return days;
    }

    /**
     * 取当前第几周
     *
     * @return weeks 当前的周数
     */
    public int getWeekOfTerm() {
        Calendar currentTime2 = (Calendar) currentTime.clone();
        int beWeekDay = firstDay.get(Calendar.DAY_OF_WEEK);
        beWeekDay = beWeekDay == 1 ? 8 : beWeekDay;
        int enWeekDay = currentTime2.get(Calendar.DAY_OF_WEEK);
        enWeekDay = enWeekDay == 1 ? 8 : enWeekDay;
        currentTime2.add(Calendar.DATE, beWeekDay - enWeekDay);
        int weeks = getDeltaT(currentTime2, firstDay) / 7;
        if (weeks >= 0) {
            weeks++;
        }
        return weeks;
    }

    private int getDeltaT(Calendar end, Calendar begin) {
        Calendar mBegin = (Calendar) begin.clone();
        mBegin.set(Calendar.HOUR_OF_DAY, 0);
        mBegin.set(Calendar.MINUTE, 0);
        mBegin.set(Calendar.SECOND, 0);
        mBegin.set(Calendar.MILLISECOND, 0);
        Calendar mEnd = (Calendar) end.clone();
        mEnd.set(Calendar.HOUR_OF_DAY, 0);
        mEnd.set(Calendar.MINUTE, 0);
        mEnd.set(Calendar.SECOND, 0);
        mEnd.set(Calendar.MILLISECOND, 0);
        return (int) ((mEnd.getTimeInMillis() - mBegin.getTimeInMillis()) / (1000 * 86400));
    }


    /**
     * 日期加减
     *
     * @param day 增加的日子
     */
    public SchoolCalendar addDay(int day) {
        currentTime.add(Calendar.DATE, day);
        return this;
    }


    /**
     * 格式化输出日期
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString 待格式化文本
     */
    public String getString(String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.CHINA);
        return format.format(currentTime.getTime());
    }


    /**
     * 格式化解析日期文本
     * 年:y		月:M		日:d		时:h(12制)/H(24值)	分:m		秒:s		毫秒:S
     *
     * @param formatString 格式化的文本
     */
    public SchoolCalendar parse(String formatString, String content) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.CHINA);
        try {
            currentTime.setTime(format.parse(content));
            return this;
        } catch (ParseException e) {
            return null;
        }
    }

    public SchoolCalendar setDate(int year, int month, int day) {
        currentTime.set(year, month, day);
        return this;
    }

    public Calendar getCalendar() {
        return currentTime;
    }

    public Date getDate() {
        return currentTime.getTime();
    }

    public int getDayOfWeek() {
        int weekDay = currentTime.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1) {
            return 7;
        } else {
            return weekDay - 1;
        }
    }

    public int getDay() {
        return currentTime.get(Calendar.DATE);
    }

    public int getMonth() {
        return currentTime.get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return currentTime.get(Calendar.YEAR);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SchoolCalendar) {
            return getDayOfTerm() == ((SchoolCalendar) o).getDayOfTerm();
        }
        return false;
    }
}
