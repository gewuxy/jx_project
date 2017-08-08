package yy.doctor.model.form.edit;

import android.text.Editable;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.CaptchaView;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/12
 */

public class EditCaptchaForm extends EditForm {

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
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
        v.setMaxCount(5);
        v.start();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (TextUtil.isNotEmpty(editable)) {
            ViewUtil.showView(getHolder().getIvClean());
        } else {
            ViewUtil.goneView(getHolder().getIvClean());
        }
    }
}
