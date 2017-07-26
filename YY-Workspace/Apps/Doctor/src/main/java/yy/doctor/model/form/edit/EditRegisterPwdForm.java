package yy.doctor.model.form.edit;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.util.UISetter;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class EditRegisterPwdForm extends EditForm {
    private boolean mFlag;//密码是否可见

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_register_pwd;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        // 设置输入digits
        UISetter.setPwdRange(holder.getEt());

        holder.getIv().setSelected(true);
        setOnClickListener(holder.getIv());
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        // TODO: dsfdf
    }

    @Override
    protected boolean onViewClick(View v) {
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
        return super.onViewClick(v);
    }
}
