package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.util.res.ResLoader;
import lib.ys.view.CaptchaView;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

import static lib.ys.util.view.ViewUtil.hideView;
import static lib.ys.util.view.ViewUtil.showView;

/**
 * @auther WangLan
 * @since 2017/7/12
 */

public class EditCaptchaForm extends EditForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_captcha;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        EditText editText = holder.getEt();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() != 0) {
                    showView(holder.getIv());
                }else {
                    hideView(holder.getIv());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setOnClickListener(holder.getIv());
    }

    @Override
    public int getContentViewResId() {
        int layout = getInt(TForm.layout);
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
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
        if (textColor != ConstantsEx.KInvalidValue) {
            tv.setSelected(enable);
            tv.setTextColor(ResLoader.getColorStateList(textColor));
        }
    }

    @Override
    protected boolean onViewClick(View v) {
        getHolder().getEt().setText("");
        return false;
    }

    public void start() {
        CaptchaView v = (CaptchaView) getHolder().getTvText();
        v.setMaxCount(5);
        v.start();
    }

}
