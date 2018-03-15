package jx.csp.ui.activity.me.bind;

import android.text.Editable;
import android.text.InputFilter;
import android.widget.TextView;

import io.reactivex.annotations.NonNull;
import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.form.Form;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * 认证邮箱
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */

public class BindEmailActivity extends BaseSetActivity {

    private TextView mTvVerify;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et)
                .related(RelatedId.bind_email)
                .hint(R.string.setting_input_email_address)
                .input((InputFilter) (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        int chr1 = source.charAt(i);
                        if (chr1 >= TextUtil.KCNRangeMin && chr1 <= TextUtil.KCNRangeMax) {
                            //是中文
                            return "";
                        }
                    }
                    return null;
                })
                .textWatcher(this)
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd)
                .layout(R.layout.form_edit_bind_email_pwd)
                .hint(R.string.input_login_pwd)
                .textWatcher(this)
                .drawable(R.drawable.pwd_selector_visible));
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        mTvVerify = bar.addTextViewRight(R.string.verify_login, R.color.text_ace400_alpha40, v -> doSet());
        mTvVerify.setClickable(false);
        Util.setTextViewBackground(mTvVerify);
    }

    @Override
    public void setViews() {
        super.setViews();
        goneView(mTvSet);
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.setting_bind_email);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.setting_bind_email);
    }

    @Override
    protected void doSet() {
        mPresenter.checkPwd(getPwd());
        mPresenter.confirmBindAccount(RelatedId.bind_email, getEmail(), getPwd());
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (RegexUtil.isEmail(getEmail()) && checkPwd(getPwd())) {
            mTvVerify.setClickable(true);
            mTvVerify.setTextColor(ResLoader.getColor(R.color.text_ace400));
        } else {
            mTvVerify.setClickable(false);
            mTvVerify.setTextColor(ResLoader.getColor(R.color.text_ace400_alpha40));
        }
    }

    @NonNull
    private String getEmail() {
        return getRelatedItem(RelatedId.bind_email).getVal();
    }

    @NonNull
    private String getPwd() {
        return getRelatedItem(RelatedId.pwd).getVal();
    }

}
