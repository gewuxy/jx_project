package yaya.csp.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import yaya.csp.R;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.network.NetworkAPISetter.UserAPI;
import yaya.csp.util.Util;

/**
 * 修改密码
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */

public class ChangePwdActivity extends BaseSetActivity{

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

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd_old)
                .hint(R.string.old_pwd)
                .drawable(R.drawable.login_pwd_selector)
                .limit(KLengthMax));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd_new)
                .hint(R.string.new_pwd)
                .drawable(R.drawable.login_pwd_selector)
                .limit(KLengthMax));
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
            showToast(R.string.change_pwd_succeed);
            finish();
        }else {
            showToast(r.getMessage());
        }
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.change_pwd);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.confirm_change);
    }

    @Override
    protected void toSet() {
        String oldPwd = Util.getEtString(mEtPwdOld);
        String newPwd = Util.getEtString(mEtPwdNew);
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.changePwd(oldPwd, newPwd).build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(checkPwd(mEtPwdOld) && checkPwd(mEtPwdNew));
    }

    /**
     * 检查密码
     *
     * @param et 密码输入框
     * @return true 密码符合规则; false 密码不符合规则
     */
    private boolean checkPwd(EditText et) {
        String pwd = Util.getEtString(et);
        return TextUtil.isNotEmpty(pwd) && pwd.length() >= KLengthMin;
    }
}
