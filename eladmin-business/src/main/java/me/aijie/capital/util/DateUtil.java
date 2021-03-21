package me.aijie.capital.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static Calendar fromCal = Calendar.getInstance();

    /**
     * 按日期加天数得出全新日期
     * @param date	需要加天数的日期
     * @param day	需要增加的天数
     * @return 新的日期
     */
    public static Date addDate(Date date, int day) {
        try {
            fromCal.setTime(date);
            fromCal.add(Calendar.DATE, day);

            return fromCal.getTime();
        } catch (Exception e) {
            return null;
        }
    }
}
