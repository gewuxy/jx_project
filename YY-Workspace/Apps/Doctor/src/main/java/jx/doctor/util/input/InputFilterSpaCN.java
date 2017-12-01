package jx.doctor.util.input;

import android.text.InputFilter;
import android.text.Spanned;

import static lib.ys.util.TextUtil.KCNRangeMax;
import static lib.ys.util.TextUtil.KCNRangeMin;

/**
 * 密码不能输入空格，汉字
 *
 * @auther WangLan
 * @since 2017/8/8
 */

public class InputFilterSpaCN implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals(" ")) {
            return "";
        }
        for (int i = start; i < end; i++) {
            int chr1 = source.charAt(i);
            if (chr1 >= KCNRangeMin && chr1 <= KCNRangeMax) {
                //是中文
                return "";
            }
        }
        return null;
    }
}



