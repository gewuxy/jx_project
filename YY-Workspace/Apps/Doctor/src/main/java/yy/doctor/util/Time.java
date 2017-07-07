package yy.doctor.util;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import yy.doctor.Constants.DateUnit;

/**
 * @auther : GuoXuan
 * @since : 2017/7/4
 */

public class Time {

    private static long secondHour = TimeUnit.HOURS.toSeconds(1); // 每小时多少秒
    private static long secondMinute = TimeUnit.MINUTES.toSeconds(1); // 每分钟多少秒

    /**
     * 格式化时间为xx:xx:xx
     * 01:01:00
     *
     * 61:00
     *
     * @param seconds 单位: 秒
     * @return
     */
    public static String secondFormat(long seconds,@NonNull @DateUnit Integer unit) {

        StringBuffer sb = new StringBuffer();
        if (DateUnit.hour == unit) {
            long hour = seconds / secondHour;
            if (hour < 10) {
                sb.append(0);
            }
            sb.append(hour).append(":");
            seconds %= secondHour; // 不会超过1小时
            unit = DateUnit.minute;
        }

        if (DateUnit.minute == unit) {
            long min = seconds / secondMinute;
            if (min < 10) {
                sb.append(0);
            }
            sb.append(min).append(":");
            seconds %= secondMinute;
            unit = DateUnit.second;
        }

        if (DateUnit.second == unit) {
            if (seconds < 10) {
                sb.append(0);
            }
            sb.append(seconds);
        }
        return sb.toString();
    }

    /**
     * 格式化时间 时分秒
     *
     * @param seconds 单位: 秒
     * @return
     */
    public static String secondFormat(long seconds) {
        StringBuffer sb = new StringBuffer();
        if (seconds > secondHour) {
            sb.append(seconds / secondHour).append("时");
            seconds %= secondHour;
        }

        if (seconds > secondMinute) {
            sb.append(seconds / secondMinute).append("分");
            seconds %= secondMinute;
        }

        if (seconds > 0) {
            sb.append(seconds).append("秒");
        }
        return sb.toString();
    }

    /**
     * 格式化时间(会舍弃部分值) 时分秒
     *
     * @param milliseconds 单位: 毫秒
     * @return
     */
    public static String milliFormat(long milliseconds) {

        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        long oneHourMills = TimeUnit.HOURS.toMillis(1);
        long oneMinuteMills = TimeUnit.MINUTES.toMillis(1);
        long oneSecondMills = TimeUnit.SECONDS.toMillis(1);

        StringBuffer sb = new StringBuffer();
        if (milliseconds >= oneDayMillis) {
            sb.append(milliseconds / oneDayMillis)
                    .append("天");
        } else if (milliseconds < oneDayMillis && milliseconds >= oneHourMills) {
            sb.append(milliseconds / oneHourMills)
                    .append("小时");
        } else if (milliseconds < oneHourMills && milliseconds >= oneMinuteMills) {
            sb.append(milliseconds / oneMinuteMills)
                    .append("分钟");
        } else if (milliseconds < oneMinuteMills && milliseconds >= oneSecondMills) {
            sb.append(milliseconds / oneMinuteMills)
                    .append("秒");
        }

        return sb.toString();
    }
}
