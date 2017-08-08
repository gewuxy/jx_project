package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class ChangePwdActivity extends BaseSetActivity {

    private final int KLengthMax = 24; // 密码最大长度
    private final int KLengthMin = 6; // 密码最少长度

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
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
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
    protected void toSet() {
        String oldPwd = Util.getEtString(mEtPwdOld);
        String newPwd = Util.getEtString(mEtPwdNew);
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.changePwd(oldPwd, newPwd));
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(checkPwd(mEtPwdOld) && checkPwd(mEtPwdOld));
    }

    /**
     * 检查密码
     *
     * @param et 密码输入框
     * @return true 密码符合规则; false 密码不符合规则
     */
    private boolean checkPwd(EditText et) {
        String pwd = Util.getEtString(et);
        return TextUtil.isNotEmpty(pwd) && pwd.length() >= KLengthMin && pwd.length() <= KLengthMax;
    }
}