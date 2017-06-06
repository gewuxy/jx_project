package yy.doctor.util;

import android.app.Activity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.ys.ConstantsEx.Milli;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
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
        n.addBackIcon(R.mipmap.nav_bar_ic_back, text, act);
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
     * 把时间格式化为xx:xx:xx
     *
     * @param seconds
     * @return
     */
    public static String formatTime(long seconds, @DateUnit int unit) {
        long millis = seconds * Milli.KSecond;
        String format = null;
        switch (unit) {
            case DateUnit.hour: {
                format = TimeFormat.from_h_24;
            }
            break;
            case DateUnit.minute: {
                format = TimeFormat.from_m;
            }
            break;
            case DateUnit.second: {
                format = TimeFormat.only_ss;
            }
            break;
        }

        return TimeUtil.formatMilli(millis, format);

//        StringBuffer sb = new StringBuffer();
//        if (DateUnit.hour == unit) {
//            long hour = useTime / MilliUtil.KOneHour;
//            if (hour < 10) {
//                sb.append(0);
//            }
//            sb.append(hour).append(":");
//            unit = DateUnit.minute;
//        }
//
//        if (DateUnit.minute == unit) {
//            int min = useTime / 60 % 60;
//            if (min < 10) {
//                sb.append(0);
//            }
//            sb.append(min).append(":");
//            unit = DateUnit.kSecond;
//        }
//
//        if (DateUnit.kSecond == unit) {
//            int sec = useTime % 60;
//            if (sec < 10) {
//                sb.append(0);
//            }
//            sb.append(sec);
//        }
//        return sb.toString();
    }

    /**
     * 按X.X小时的格式格式化毫秒值
     *
     * @param milliseconds 秒单位
     * @return
     */
    public static String timeParse(long milliseconds) {
        // FIXME: 会议时间转换??????
        StringBuffer parse = new StringBuffer();

        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
        if (milliseconds >= oneDayMillis) {
            parse.append(milliseconds / oneDayMillis)
                    .append("天");
            milliseconds %= oneDayMillis;
        }

        BigDecimal b = new BigDecimal(milliseconds);
        //保留1位小数,四舍五入
        float result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return parse
                .append(result)
                .append("小时")
                .toString();
    }

}
