package yy.doctor.util;

import android.app.Activity;

import java.util.Arrays;
import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.util.BaseUtil;
import yy.doctor.Constants.Date;
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
     * @param useTime
     * @return
     */
    public static String timeParse(int useTime,@Date int time) {
        StringBuffer sb = new StringBuffer();
        if (Date.hour == time) {
            int hour = useTime / 3600;
            if (hour < 10) {
                sb.append(0);
            }
            sb.append(hour).append(":");
            time = Date.minute;
        }

        if (Date.minute == time) {
            int min = useTime / 60 % 60;
            if (min < 10) {
                sb.append(0);
            }
            sb.append(min).append(":");
            time = Date.second;
        }

        if (Date.second == time) {
            int sec = useTime % 60;
            if (sec < 10) {
                sb.append(0);
            }
            sb.append(sec);
        }
        return sb.toString();
    }

}
