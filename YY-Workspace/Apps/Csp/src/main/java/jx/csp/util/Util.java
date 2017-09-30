package jx.csp.util;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.widget.EditText;

import lib.network.Network;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.RegexUtil;
import lib.yy.util.BaseUtil;
import jx.csp.App;
import jx.csp.R;

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

    public static boolean noNetwork() {
        boolean b = !DeviceUtil.isNetworkEnabled();
        if (b) {
            App.showToast(Network.getConfig().getDisconnectToast());
        }
        return b;
    }

    /**
     * 检验是否是电话号码
     */
    public static boolean isMobileCN(CharSequence phone) {
        return RegexUtil.isMobileCN(phone.toString().replace(" ", ""));
    }

    /**
     * 密码允许输入的特殊符
     *
     * @return
     */
    public static String symbol() {
        String character = "^([A-Za-z_0-9]|-|×|÷|＝|%|√|°|′|″|\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\||\\*|/|#|~|,|:|;|\\?|\"|‖|&|\\*|@|\\|\\^|,|\\$|–|…|'|=|\\+|!|>|<|\\.|-|—|_)+$";
        return character;
    }
}
