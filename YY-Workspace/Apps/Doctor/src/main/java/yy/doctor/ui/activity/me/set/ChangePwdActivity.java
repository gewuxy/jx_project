package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkResp;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
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
                .hint("请输入旧密码")
                .drawable(R.drawable.register_pwd_selector));

        addItem(Form.create(FormType.divider));

        addItem(Form.create(FormType.et_register_pwd)
                .related(RelatedId.pwd_new)
                .hint("请输入新密码")
                .drawable(R.drawable.register_pwd_selector));

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
    protected void toSet() {
        String oldPwd = mEtPwdOld.getText().toString();
        String newPwd = mEtPwdNew.getText().toString();
        exeNetworkReq(NetFactory.changePwd(oldPwd, newPwd));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast(R.string.pwd_change_success);
            finish();
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public CharSequence getNavBarText() {
        return "修改密码";
    }

    @Override
    public CharSequence getSetText() {
        return "确认修改";
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setChanged(checkPwd(Util.getEtString(mEtPwdOld)) && checkPwd(Util.getEtString(mEtPwdNew)));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private boolean checkPwd(String pwd) {
        return TextUtil.isNotEmpty(pwd) && pwd.length() >= 6 && pwd.length() <= 24;
    }
}