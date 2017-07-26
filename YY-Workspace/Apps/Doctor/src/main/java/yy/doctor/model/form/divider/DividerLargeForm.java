package yy.doctor.model.form.divider;

import android.view.ViewGroup.MarginLayoutParams;

import lib.ys.fitter.DpFitter;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/7/25
 */

public class DividerLargeForm extends DividerForm {

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        MarginLayoutParams params = (MarginLayoutParams) holder.getDivider().getLayoutParams();
        params.height = DpFitter.dimen(R.dimen.form_divider_large_height);
        holder.getDivider().setLayoutParams(params);
    }
}
