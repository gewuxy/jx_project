package yy.doctor.model.form.edit;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class EditRegisterPwdForm extends EditForm {
    private boolean mFlag = true;//密码是否可见

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_register_pwd;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        // 设置输入digits
        UISetter.setPwdRange(holder.getEt());

        holder.getIv().setSelected(true);
        setOnClickListener(holder.getIv());
        setOnClickListener(holder.getIvCancel());

        getHolder().getIv().setSelected(mFlag);

        holder.getEt().addTextChangedListener(new TextWatcher() {
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

        holder.getEt().setOnFocusChangeListener((v, hasFocus) -> {
            // iv是否显示
            if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(holder.getEt()))) {
                ViewUtil.showView(holder.getIvCancel());
            } else {
                ViewUtil.goneView(holder.getIvCancel());
            }
        });
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
    }

    @Override
    protected boolean onViewClick(View v) {
        switch (v.getId()) {
            case R.id.form_iv: {
                mFlag = !mFlag;
                getHolder().getIv().setSelected(mFlag);
                EditText et = getHolder().getEt();
                String content = et.getText().toString();
                if (!mFlag) {
                    et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                et.setSelection(content.length());//光标移到最后
            }
            break;
            case R.id.form_iv_cancel: {
                getHolder().getEt().setText("");
            }
        }
        return super.onViewClick(v);
    }
}


