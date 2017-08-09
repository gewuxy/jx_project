package yy.doctor.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 不能输入空格
 * @auther WangLan
 * @since 2017/8/8
 */

public class InputFilterUtils implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals(" ")) {
            return "";
        }
        return null;
    }
}



