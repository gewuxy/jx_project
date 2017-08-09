package yy.doctor.util.input;

import android.text.InputFilter;
import android.text.Spanned;

import static yy.doctor.util.input.InputFilterChineseImpl.isChinese;

/**
 * 密码不能输入空格，汉字
 *
 * @auther WangLan
 * @since 2017/8/8
 */

public class InputFilterUtils implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals(" ")) {
            return "";
        }
        for (int i = start; i < end; i++) {
            if (isChinese(source.charAt(i))) {
                return "";
            }
        }
        return null;
    }
}



