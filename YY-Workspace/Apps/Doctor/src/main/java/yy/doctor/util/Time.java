package yy.doctor.util;

import java.util.concurrent.TimeUnit;

import lib.ys.YSLog;
import lib.ys.util.TimeFormatter;
import yy.doctor.Constants.DateUnit;

/**
 * @auther : GuoXuan
 * @since : 2017/7/4
 */

public class Time {

    private static long KSecondHour = TimeUnit.HOURS.toSeconds(1); // 每小时多少秒
    private static long KSecondMinute = TimeUnit.MINUTES.toSeconds(1); // 每分钟多少秒

    /**
     * 格式化时间为xx:xx:xx
     * 01:01:00
     * <p>
     * 61:00
     *
     * @param seconds 单位: 秒
     * @return
     */
    public static String secondFormat(long seconds, @DateUnit int unit) {

        StringBuffer sb = new StringBuffer();
        if (DateUnit.hour == unit) {
            long hour = seconds / KSecondHour;
            if (hour < 10) {
                sb.append(0);
            }
            sb.append(hour).append(":");
            seconds %= KSecondHour; // 不会超过1小时
            unit = DateUnit.minute;
        }

        if (DateUnit.minute == unit) {
            long min = seconds / KSecondMinute;
            if (min < 10) {
                sb.append(0);
            }
            sb.append(min).append(":");
            seconds %= KSecondMinute;
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
        if (seconds > KSecondHour) {
            sb.append(seconds / KSecondHour).append("时");
            seconds %= KSecondHour;
        }

        if (seconds > KSecondMinute) {
            sb.append(seconds / KSecondMinute).append("分");
            seconds %= KSecondMinute;
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

    public static String getTime(long millisecond) {
        int second = Math.round(millisecond / 1000.0f); // 四舍五入
        YSLog.d("media", "getTime:" + second);
        return TimeFormatter.second(second, TimeFormatter.TimeFormat.from_m);
    }
}
