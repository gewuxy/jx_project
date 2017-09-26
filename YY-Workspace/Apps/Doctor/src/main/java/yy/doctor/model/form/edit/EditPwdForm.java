package yy.doctor.model.form.edit;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.util.UISetter;
import yy.doctor.util.input.InputFilterSpaCN;

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
        // 实现的方法在 super.init()里,所以提前设置
        input(new InputFilterSpaCN());
        limit(24);

        super.init(holder);

        // 设置输入digits
        if (!getIllegality()) {
            UISetter.setPwdRange(getHolder().getEt());
        }

        NetworkImageView iv = holder.getIv();
        iv.setSelected(mFlag);
        setOnClickListener(iv);
    }

    @Override
    protected boolean onViewClick(View v) {
        switch (v.getId()) {
            case R.id.form_iv: {
                mFlag = !mFlag;
                getHolder().getIv().setSelected(mFlag);
                EditText et = getHolder().getEt();
                String content = et.getText().toString();
                if (mFlag) {
                    et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                et.setSelection(content.length());//光标移到最后
            }
            break;
        }
        return super.onViewClick(v);
    }
}


