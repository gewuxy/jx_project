package yy.doctor.util;

import android.app.Activity;
import android.support.annotation.StringRes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.util.BaseUtil;
import yy.doctor.Constants.DateUnit;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(NavBar n, final Activity act) {
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.mipmap.nav_bar_ic_back, act);
    }

    /**
     * 获取会议科室列表
     *
     * @return
     */
    public static List<String> getSections() {
        String[] sectionNames = ResLoader.getStringArray(R.array.sections);
        return Arrays.asList(sectionNames);
    }

    /**
     * 格式化时间为xx:xx:xx
     *
     * @param seconds 秒单位
     * @return
     */
    public static String format(long seconds, @DateUnit int unit) {
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
     * @param time 秒单位
     * @return
     */
    public static String format(long time) {
        StringBuffer sb = new StringBuffer();
        long hour = TimeUnit.HOURS.toSeconds(1);
        if (time > hour) {
            sb.append(time / hour).append("时");
            time %= hour;
        }

        long minute = TimeUnit.MINUTES.toSeconds(1);
        if (time > minute) {
            sb.append(time / minute).append("分");
            time %= minute;
        }

        if (time > 0) {
            sb.append(time).append("秒");
        }
        return sb.toString();
    }


    /**
     * 格式化时间(会舍弃部分值) 时分秒
     *
     * @param milliseconds 毫秒单位
     * @return
     */
    public static String parse(long milliseconds) {

        StringBuffer parse = new StringBuffer();
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        long oneHourMills = TimeUnit.HOURS.toMillis(1);
        long oneMinuteMills = TimeUnit.MINUTES.toMillis(1);
        long oneSecondMills = TimeUnit.SECONDS.toMillis(1);
        if (milliseconds >= oneDayMillis) {
            parse.append(milliseconds / oneDayMillis)
                    .append("天");
        } else if (milliseconds < oneDayMillis && milliseconds >= oneHourMills) {
            parse.append(milliseconds / oneHourMills)
                    .append("小时");
        } else if (milliseconds < oneHourMills && milliseconds >= oneMinuteMills) {
            parse.append(milliseconds / oneMinuteMills)
                    .append("分钟");
        } else if (milliseconds < oneMinuteMills && milliseconds >= oneSecondMills) {
            parse.append(milliseconds / oneMinuteMills)
                    .append("秒");
        }

        return parse.toString();
    }

    public static String convertUrl(String url) {
        return TextUtil.toUtf8(url);
    }
}
