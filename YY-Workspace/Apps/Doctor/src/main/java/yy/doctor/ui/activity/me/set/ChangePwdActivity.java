package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.util.TextUtil;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class ChangePwdActivity extends BaseSetActivity implements TextWatcher {

    private EditText mEtPwdOld;
    private EditText mEtPwdNew;

    @IntDef({
            RelatedId.pwd_old,
            RelatedId.pwd_new,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int pwd_old = 0;
        int pwd_new = 1;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et_register_pwd)
                .related(RelatedId.pwd_old)
                .hint("请输入旧密码"));

        addItem(Form.create(FormType.divider));

        addItem(Form.create(FormType.et_register_pwd)
                .related(RelatedId.pwd_new)
                .hint("请输入新密码"));

        addItem(Form.create(FormType.divider));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPwdOld = getRelatedItem(RelatedId.pwd_old).getHolder().getEt();
        mEtPwdNew = getRelatedItem(RelatedId.pwd_new).getHolder().getEt();
        mEtPwdOld.addTextChangedListener(this);
        mEtPwdNew.addTextChangedListener(this);
    }

    @Override
    public CharSequence getNavBarText() {
        return "修改密码";
    }

    @Override
    public CharSequence getSetText() {
        return "确认并登录";
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setChanged(TextUtil.isNotEmpty(Util.getEtString(mEtPwdOld))
                && TextUtil.isNotEmpty(Util.getEtString(mEtPwdNew)));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}