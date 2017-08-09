package yy.doctor.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @auther WangLan
 * @since 2017/8/9
 */

public class InputSpaceFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals(" ")) {
            return "";
        }
        return null;
    }
}
