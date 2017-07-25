package yy.doctor.model.form.text;

import android.support.annotation.NonNull;

import lib.ys.util.res.ResLoader;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class MeForm extends TextForm {

    private static final int KColorNormal = ResLoader.getColor(R.color.text_006ebd);

    @NonNull
    @Override
    public int getType() {
        return FormType.text_intent_me;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        holder.getTvText().setTextColor(KColorNormal);
    }
}
