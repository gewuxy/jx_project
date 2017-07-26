package yy.doctor.model.form.divider;

import lib.ys.fitter.DpFitter;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/7/25
 */

public class DividerMarginForm extends DividerForm {

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        int padding = DpFitter.dimen(R.dimen.form_divider_large_height);
        paddingLeft(padding);
        paddingRight(padding);
    }
}
