package jx.doctor.model.form.text;

import lib.ys.util.res.ResLoader;
import lib.jx.adapter.VH.FormVH;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class MeForm extends TextForm {

    private static final int KColorNormal = ResLoader.getColor(R.color.text_006ebd);

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_me;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        holder.getTvText().setTextColor(KColorNormal);
    }
}
