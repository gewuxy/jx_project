package yaya.csp.util;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.widget.EditText;

import lib.ys.ui.other.NavBar;
import lib.yy.util.BaseUtil;
import yaya.csp.R;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    public static void addBackIcon(NavBar n, final Activity act) {
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    public static void addBackIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
    }

    /**
     * 登录页面左上角的X
     * @param n
     * @param id
     * @param act
     */
    public static void addCloseIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.drawable.login_ic_close, act);
    }

    public static void addCloseIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.drawable.login_ic_close, act);
    }

    /**
     * 获取输入框的文本
     *
     * @param et
     * @return
     */
    public static String getEtString(EditText et) {
        return et.getText().toString().trim();
    }

}
