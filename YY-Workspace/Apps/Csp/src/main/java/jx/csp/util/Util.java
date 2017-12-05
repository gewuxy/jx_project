package jx.csp.util;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import jx.csp.App;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.constant.MetaValue;
import lib.jx.util.BaseUtil;
import lib.network.Network;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;
import lib.ys.util.ReflectUtil;
import lib.ys.util.RegexUtil;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    private final static String KRegexPwd = "^([A-Za-z_0-9]|-|×|÷|＝|%|√|°|′|″|\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\||\\*|/|#|~|,|:|;|\\?|\"|‖|&|\\*|@|\\|\\^|,|\\$|–|…|'|=|\\+|!|>|<|\\.|-|—|_)+$";

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
     *
     * @param n
     * @param id
     * @param act
     */
    public static void addCloseIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.drawable.default_ic_close, act);
    }

    public static void addCloseIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.drawable.default_ic_close, act);
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
     * 验证密码规则, 全局统一
     *
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd) {
        if (!pwd.matches(KRegexPwd)) {
            App.showToast(R.string.input_special_symbol);
            return false;
        }

        if (pwd.length() < 6) {
            App.showToast(R.string.input_right_pwd_num);
            return false;
        }

        return true;
    }

    /**
     * 得到指定格式的时间字符串
     *
     * @param l
     * @param mFormat 分后面的字符
     * @param sFormat 秒后面的字符
     * @return
     */
    public static String getSpecialTimeFormat(long l, String mFormat, String sFormat) {
        int m = (int) (l / 60);
        int s = (int) (l % 60);
        StringBuffer sb = new StringBuffer()
                .append(m <= 9 ? "0" : "")
                .append(m)
                .append(mFormat)
                .append(s <= 9 ? "0" : "")
                .append(s)
                .append(sFormat);
        return sb.toString();
    }

    /**
     * 获取NavBar里需要的控件
     *
     * @param parent NavBar的控件
     * @param clz
     * @param <T>
     * @return
     */
    public static <T extends View> T getBarView(ViewGroup parent, Class<T> clz) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                getBarView((ViewGroup) childView, clz);
            } else {
                if (childView.getClass().isAssignableFrom(clz)) {
                    return (T) childView;
                }
            }
        }
        return ReflectUtil.newInst(clz);
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(String key, String data) {
        try {
            String IVPARAMETERSPEC = "qWeRdFgH"; // 初始化向量参数，AES 为16bytes. DES 为8bytes.
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  // DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
            IvParameterSpec iv = new IvParameterSpec(IVPARAMETERSPEC.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, getRawKey(key), iv);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对密钥进行处理
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key getRawKey(String key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(dks);
    }


    public static float calcVpOffset(int padding, int width) {
        float vpWidth = width - padding * 2;
        //padding部分所占百分比
        return -padding / vpWidth;
    }

    /**
     * 国内
     */
    public static boolean checkAppCn() {
        return Constants.KAppTypeCn.equals(PackageUtil.getMetaValue(MetaValue.app_type));
    }
}
