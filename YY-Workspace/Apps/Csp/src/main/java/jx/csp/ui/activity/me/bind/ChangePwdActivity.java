package jx.csp.ui.activity.me.bind;

import android.text.Editable;
import android.widget.EditText;

import jx.csp.R;
import jx.csp.model.def.FormType;
import jx.csp.model.form.Form;
import jx.csp.util.Util;
import lib.ys.util.TextUtil;

/**
 * 修改密码
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */

public class ChangePwdActivity extends BaseSetActivity{

    private final int KLengthMax = 24; // 密码最大长度
    private final int KLengthMin = 6; // 密码最小长度

    private EditText mEtPwdOld;
    private EditText mEtPwdNew;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.change_old_pwd)
                .hint(R.string.setting_old_pwd)
                .drawable(R.drawable.login_selector_visible)
                .limit(KLengthMax));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.change_new_pwd)
                .hint(R.string.setting_new_pwd)
                .drawable(R.drawable.login_selector_visible)
                .limit(KLengthMax));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPwdOld = getRelatedItem(RelatedId.change_old_pwd).getHolder().getEt();
        mEtPwdNew = getRelatedItem(RelatedId.change_new_pwd).getHolder().getEt();

        mEtPwdOld.addTextChangedListener(this);
        mEtPwdNew.addTextChangedListener(this);
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.setting_change_pwd);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.setting_confirm_change);
    }

    @Override
    protected void doSet() {
        mPresenter.checkPwd(mEtPwdNew);
        mPresenter.changePwdNetworkReq(RelatedId.change_old_pwd, Util.getEtString(mEtPwdOld), Util.getEtString(mEtPwdNew));
    }

    @Override
    public void afterTextChanged(Editable s) {
        mView.setChanged(checkPwd(mEtPwdOld) && checkPwd(mEtPwdNew));
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
