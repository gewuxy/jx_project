package lib.ys.util;

/**
 * 各种时间单位与毫秒之间的转换
 * <p>
 * 命名规则
 * to: 表示把毫秒转换成对应单位
 * 直接单位名称: 表示把对应单位转换成毫秒
 *
 * @author yuansui
 */
public class MilliUtil {

    private static final long KOneSecond = 1000;
    private static final long KOneMinute = KOneSecond * 60;
    private static final long KOneHour = KOneMinute * 60;
    private static final long KOneDay = KOneHour * 24;
    private static final long KOneWeek = KOneDay * 7;

    /**
     * 转换秒
     *
     * @param milliseconds
     * @return
     */
    public static long toSecond(long milliseconds) {
        return milliseconds / KOneSecond;
    }

    /**
     * 获取对应秒的毫秒数
     *
     * @param second
     * @return
     */
    public static long second(int second) {
        return second * KOneSecond;
    }

    /**
     * 转换分钟
     *
     * @param minute
     * @return
     */
    public static long minute(int minute) {
        return minute * KOneMinute;
    }

    /**
     * 获取对应小时的毫秒数
     *
     * @param hour
     * @return
     */
    public static long hour(int hour) {
        return hour * KOneHour;
    }

    public static long hour(String hour) {
        return Integer.valueOf(hour) * KOneHour;
    }

    public static long toHour(long milliseconds) {
        return milliseconds / KOneHour;
    }

    /**
     * 转换天数
     *
     * @param day
     * @return
     */
    public static long day(int day) {
        return day * KOneDay;
    }

    /**
     * 转换星期
     *
     * @param week
     * @return
     */
    public static long week(int week) {
        return week * KOneWeek;
    }
}
