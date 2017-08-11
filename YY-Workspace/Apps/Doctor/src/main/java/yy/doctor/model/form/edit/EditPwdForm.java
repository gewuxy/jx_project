package yy.doctor.model.form.edit;

import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.util.input.InputFilterUtils;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class EditPwdForm extends EditForm {
    private boolean mFlag = true;//密码是否可见

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_pwd;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        // 设置输入digits
        // UISetter.setPwdRange(holder.getEt());

        holder.getIv().setSelected(true);
        setOnClickListener(holder.getIv());

        holder.getEt().setFilters(new InputFilter[]{new InputFilterUtils(), new LengthFilter(24)});

        getHolder().getIv().setSelected(mFlag);
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
        }
        return super.onViewClick(v);
    }
}


