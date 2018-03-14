package jx.csp.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import inject.annotation.network.Descriptor;
import jx.csp.App;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.constant.MetaValue;
import jx.csp.network.NetworkApi;
import jx.csp.sp.SpApp;
import lib.jx.util.BaseUtil;
import lib.network.Network;
import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.PackageUtil;
import lib.ys.util.ReflectUtil;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Util extends BaseUtil {

    private final static String KRegexPwd = "^([A-Za-z_0-9]|-|×|÷|＝|%|√|°|′|″|\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\||\\*|/|#|~|,|:|;|\\?|\"|‖|&|\\*|@|\\|\\^|,|\\$|–|…|'|=|\\+|!|>|<|\\.|-|—|_)+$";

    public static void addBackIcon(NavBar n, final Activity act) {
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
        addDivider(n);
    }

    public static void addBackIcon(NavBar n, CharSequence text, final Activity act) {
        n.addTextViewMid(text);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
        addDivider(n);
    }

    public static void addBackIcon(NavBar n, @StringRes int id, final Activity act) {
        n.addTextViewMid(id);
        n.addBackIcon(R.drawable.nav_bar_ic_back, act);
        addDivider(n);
    }

    public static void addDivider(NavBar n) {
        View d = View.inflate(n.getContext(), R.layout.layout_nav_bar_divider, null);
        n.addView(d, LayoutUtil.getViewGroupParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

    public static void setTextViewBackground(TextView tv) {
        if (tv == null) {
            return;
        }
        ViewGroup.LayoutParams params = tv.getLayoutParams();
        if (params == null) {
            return;
        }
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        // 设置点击背景色
        if (NavBar.getConfig().getFocusBgColorRes() != 0) {
            StateListDrawable sd = new StateListDrawable();

            Drawable dNormal = new ColorDrawable(Color.TRANSPARENT);
            Drawable dPressed = new ColorDrawable(NavBar.getConfig().getFocusBgColorRes());

            int pressed = android.R.attr.state_pressed;

            sd.addState(new int[]{pressed}, dPressed);
            sd.addState(new int[]{-pressed}, dNormal);

            ViewUtil.setBackground(tv, sd);
        } else if (NavBar.getConfig().getFocusBgDrawableRes() != 0) {
            tv.setBackgroundResource(NavBar.getConfig().getFocusBgDrawableRes());
        }
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
     * @param l       单位 秒
     * @param mFormat 分后面的字符
     * @param sFormat 秒后面的字符
     * @return
     */
    public static String getSpecialTimeFormat(long l, String mFormat, String sFormat) {
        int m = (int) (l / 60);
        int s = (int) (l % 60);
        StringBuilder sb = new StringBuilder()
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

    /**
     * 通过图片url获取bitmap
     *
     * @param urlPath
     * @return
     */
    public static Bitmap getBitMBitmap(String urlPath) {
        final Bitmap[] map = {null};
        new Thread(() -> {
            try {
                URL url = new URL(urlPath);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream in;
                in = conn.getInputStream();
                map[0] = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        return map[0];
    }

    public static void toSetting() {
        try {
            // 应用详情页面
            Intent i = new Intent();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction(ConstantsEx.KSystemSetting);
            Uri uri = Uri.fromParts(ConstantsEx.KPackage, App.getContext().getPackageName(), null);
            i.setData(uri);
            LaunchUtil.startActivity(i);
        } catch (Exception e) {
            // 设置页面
            Intent i = new Intent(Settings.ACTION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LaunchUtil.startActivity(i);
        }
    }

    /**
     * 分享的url加密处理
     */
    public static String getShareUrl(String courseId) {
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        String baseUrl = (BuildConfig.DEBUG_NETWORK ? des.hostDebuggable() : des.host()) + "meeting/share?signature=";
        if (TextUtil.isEmpty(courseId)) {
            return baseUrl;
        }
        LangType type = SpApp.inst().getLangType(); // 系统语言
        @AppType int appType;  // 版本
        if (Util.checkAppCn()) {
            appType = AppType.inland;
        } else {
            appType = AppType.overseas;
        }
        // 拼接参数
        StringBuilder paramBuffer = new StringBuilder()
                .append("id=")
                .append(courseId)
                .append("&_local=")
                .append(type.define())
                .append("&abroad=")
                .append(appType);
        try {
            // 参数加密
            String encode = Util.encode(Constants.KDesKey, paramBuffer.toString());
            return baseUrl + URLEncoder.encode(encode, Constants.KEncoding_utf8);
        } catch (Exception e) {
            return baseUrl;
        }
    }

}
