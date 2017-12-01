package jx.doctor.util.input;

import android.text.InputFilter;
import android.text.Spanned;

import static lib.ys.util.TextUtil.KCNRangeMax;
import static lib.ys.util.TextUtil.KCNRangeMin;

/**
 * 姓名只能输入中文
 *
 * @auther WangLan
 * @since 2017/8/8
 */

public class InputFilterChineseImpl implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            int chr1 = source.charAt(i);
            if (chr1 <= KCNRangeMin || chr1 >= KCNRangeMax) {
                //不是中文，textUtil类里面
                return "";
            }
        }
        return null;
    }
}
