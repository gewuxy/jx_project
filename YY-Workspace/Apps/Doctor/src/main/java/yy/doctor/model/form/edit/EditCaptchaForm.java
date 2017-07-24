package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.util.res.ResLoader;
import lib.ys.view.CaptchaView;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/7/12
 */

public class EditCaptchaForm extends EditForm {
    FormVH h;

    @NonNull
    @Override
    public int getType() {
        return FormType.et_captcha;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        h = holder;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_captcha;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        boolean enable = getBoolean(TForm.enable);
        TextView tv = holder.getTvText();
        if (enable) {
            setOnClickListener(tv);
        } else {
            removeOnClickListener(tv);
        }

        int textColor = getInt(TForm.text_color);
        if (textColor != 0) {
            tv.setSelected(enable);
            tv.setTextColor(ResLoader.getColorStateList(textColor));
        }
    }

    @Override
    protected boolean onViewClick(View v) {
        return false;
    }

    public void start() {
        CaptchaView v = (CaptchaView) getHolder().getTvText();
        v.setMaxCount(5);
        v.start();
    }

}
