package yaya.csp.util;

import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import lib.ys.util.res.ResLoader;
import yaya.csp.R;

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

    public static SpannableString setLoginProtocol (String source){
        SpannableString s = new SpannableString(source);
        s.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_167afe)), 9, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return s;
    }
}
