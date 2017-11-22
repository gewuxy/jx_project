package yy.doctor.ui.activity.me.set;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.Editable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.NetworkApiDescriptor.UserAPI;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class ChangePwdActivity extends BaseSetActivity {

    private final int KLengthMax = 24; // 密码最大长度
    private final int KLengthMin = 6; // 密码最少长度

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
    public void initData(Bundle state) {
        super.initData(state);

        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd_old)
                .hint("旧密码")
                .limit(KLengthMax)
                .textWatcher(this)
                .drawable(R.drawable.register_pwd_selector));

        addItem(Form.create(FormType.divider));

        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd_new)
                .hint(R.string.pwd)
                .limit(KLengthMax)
                .textWatcher(this)
                .drawable(R.drawable.register_pwd_selector));

        addItem(Form.create(FormType.divider));
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            showToast(R.string.pwd_change_success);
            finish();
        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public CharSequence getNavBarText() {
        return getString(R.string.change_pwd);
    }

    @Override
    public CharSequence getSetText() {
        return getString(R.string.confirm_change);
    }

    @Override
    protected void toSet() {
        String oldPwd = getRelatedItem(RelatedId.pwd_old).getVal();
        String newPwd = getRelatedItem(RelatedId.pwd_new).getVal();
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.changePwd(oldPwd, newPwd).build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        String oldPwd = getRelatedItem(RelatedId.pwd_old).getVal();
        String newPwd = getRelatedItem(RelatedId.pwd_new).getVal();
        setChanged(checkPwd(oldPwd) && checkPwd(newPwd));
    }

    /**
     * 检查密码
     *
     * @param pwd 密码
     * @return true 密码符合规则; false 密码不符合规则
     */
    private boolean checkPwd(String pwd) {
        return TextUtil.isNotEmpty(pwd) && pwd.length() >= KLengthMin;
    }
}