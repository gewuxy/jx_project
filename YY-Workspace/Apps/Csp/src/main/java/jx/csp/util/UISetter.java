package jx.csp.util;

import android.content.Context;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * @auther yuansui
 * @since 2017/6/8
 */

public class UISetter {

    /**
     * 设置密码的输入范围格式
     *
     * @param et
     */
    public static void setPwdRange(EditText et) {

        et.setKeyListener(new NumberKeyListener() {

            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM-×÷＝%√°′″{}()[].|*/#~,:;?\"‖&*@\\^,$–…'=+!><.-—_";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
    }

    /**
     * 设置观看密码页面的输入范围格式
     *
     * @param et
     */
    public static void setWatchPwdRange(EditText et) {

        et.setKeyListener(new NumberKeyListener() {

            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
    }

    /**
     * NavBar中间文字设置 文字太长时的设置方法
     */
    public static void setNavBarMidText(NavBar bar, String fileName, Context context) {
        if (TextUtil.isEmpty(fileName)) {
            return;
        }
        View v = View.inflate(context, R.layout.layout_nav_bar_mid_text, null);
        TextView tv = v.findViewById(R.id.nav_bar_mid_tv);
        tv.setText(fileName);
        bar.addViewMid(v);
    }
}
