package yy.doctor.util;

import java.util.concurrent.TimeUnit;

import yy.doctor.Constants.DateUnit;

/**
 * @auther : GuoXuan
 * @since : 2017/7/4
 */

public class Time {

    /**
     * 格式化时间为xx:xx:xx
     *
     * @param seconds 单位: 秒
     * @return
     */
    public static String secondFormat(long seconds, @DateUnit int unit) {
        StringBuffer sb = new StringBuffer();
        if (DateUnit.hour == unit) {
            long hour = seconds / TimeUnit.HOURS.toSeconds(1);
            if (hour < 10) {
                sb.append(0);
            }
            sb.append(hour).append(":");
            unit = DateUnit.minute;
        }

        if (DateUnit.minute == unit) {
            long min = seconds / TimeUnit.HOURS.toMinutes(1) % TimeUnit.MINUTES.toSeconds(1);
            if (min < 10) {
                sb.append(0);
            }
            sb.append(min).append(":");
            unit = DateUnit.second;
        }

        if (DateUnit.second == unit) {
            long sec = seconds % TimeUnit.MINUTES.toSeconds(1);
            if (sec < 10) {
                sb.append(0);
            }
            sb.append(sec);
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
        long hour = TimeUnit.HOURS.toSeconds(1);
        if (seconds > hour) {
            sb.append(seconds / hour).append("时");
            seconds %= hour;
        }

        long minute = TimeUnit.MINUTES.toSeconds(1);
        if (seconds > minute) {
            sb.append(seconds / minute).append("分");
            seconds %= minute;
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
