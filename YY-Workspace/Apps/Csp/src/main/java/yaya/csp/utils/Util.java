package yaya.csp.utils;

import android.widget.EditText;

import lib.ys.util.RegexUtil;
import lib.yy.util.BaseUtil;

/**
 * @auther WangLan
 * @since 2017/9/22
 */

public class Util extends BaseUtil {
    /**
     * 检验是否是电话号码
     */
    public static boolean isMobileCN(CharSequence phone) {
        return RegexUtil.isMobileCN(phone.toString().replace(" ", ""));
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
