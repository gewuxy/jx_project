package yy.doctor.util;

import android.app.Activity;

import lib.ys.ex.TitleBarEx;
import lib.yy.util.BaseUtil;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(TitleBarEx titleBar, final Activity act) {
        titleBar.addBackIcon(R.mipmap.title_ic_back, act);
    }

    public static void addBackIcon(TitleBarEx titleBar, CharSequence text, final Activity act) {
        titleBar.addBackIcon(R.mipmap.title_ic_back, text, act);
    }
}
