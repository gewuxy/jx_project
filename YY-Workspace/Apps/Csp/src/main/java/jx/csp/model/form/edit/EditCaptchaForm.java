package jx.csp.model.form.edit;

import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.view.CaptchaView;
import lib.yy.adapter.VH.FormVH;
import jx.csp.R;

/**
 * @auther WangLan
 * @since 2017/7/12
 */

public class EditCaptchaForm extends EditForm {
    private final int KCountDown = 60;

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        holder.getIv().setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
    }

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_captcha;
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        boolean enable = isEnabled();
        TextView tv = holder.getTvText();
        if (enable) {
            setOnClickListener(tv);
        } else {
            removeOnClickListener(tv);
        }

        if (getTextColor() != null) {
            tv.setSelected(enable);
            tv.setTextColor(getTextColor());
        }
    }

    public void start() {
        CaptchaView v = (CaptchaView) getHolder().getTvText();
        v.setMaxCount(KCountDown);
        v.start();
    }
}
