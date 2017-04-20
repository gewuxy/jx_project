package lib.yy.util;

import android.support.annotation.DrawableRes;

import lib.ys.activity.ActivityEx;
import lib.ys.ex.TitleBarEx;
import lib.ys.util.UtilEx;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class BaseUtil extends UtilEx {

    public static void addBackIcon(TitleBarEx t, @DrawableRes int drawableId, ActivityEx act) {
        t.addBackIcon(drawableId, act);
    }
}
