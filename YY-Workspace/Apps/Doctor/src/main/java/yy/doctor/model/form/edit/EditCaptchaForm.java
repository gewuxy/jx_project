package yy.doctor.model.form.edit;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

        EditText editText = holder.getEt();
        setOnClickListener(holder.getIvCancel());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isNotEmpty(s)) {
                    ViewUtil.showView(holder.getIvCancel());
                } else {
                    ViewUtil.goneView(holder.getIvCancel());
                }
            }
        });

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

    @Override
    protected boolean onViewClick(View v) {
        switch (v.getId()){
            case R.id.form_iv_cancel:
                getHolder().getEt().setText("");
        }
        return false;
    }

    public void start() {
        CaptchaView v = (CaptchaView) getHolder().getTvText();
        v.setMaxCount(5);
        v.start();
    }

}
