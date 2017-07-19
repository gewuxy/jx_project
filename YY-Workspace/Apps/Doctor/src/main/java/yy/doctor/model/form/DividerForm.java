package yy.doctor.model.form;

import android.support.annotation.NonNull;
import android.view.ViewGroup.MarginLayoutParams;

import lib.ys.ConstantsEx;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/7
 */
public class DividerForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.divider;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_divider;
    }

    @Override
    protected void refresh(FormVH holder) {
        int h = getInt(TForm.height);
        if (h > 0) {
            MarginLayoutParams params = (MarginLayoutParams) holder.getDivider().getLayoutParams();
            params.height = h;
            holder.getDivider().setLayoutParams(params);
        }

        int background = getInt(TForm.background);
        if (background != ConstantsEx.KInvalidValue) {
            holder.getDivider().setBackgroundColor(background);
        }

    }
}
