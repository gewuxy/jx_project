package jx.csp.ui.activity.me.bind;

import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import jx.csp.Constants.CaptchaType;
import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.dialog.CommonDialog;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.presenter.SetBindPresenterImpl;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 修改与绑定的基类
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */
abstract public class BaseSetActivity extends BaseFormActivity implements TextWatcher {

    private TextView mTvSet;

    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟

    private int mCount; // 计算点击多少次
    private long mStartTime; // 开始计算10分钟间隔的时间

    public SetBindContract.P mPresenter;
    public SetBindContract.V mView;

    @IntDef({
            RelatedId.bind_email,
            RelatedId.pwd,

            RelatedId.bind_phone_number,
            RelatedId.bind_captcha,

            RelatedId.change_old_pwd,
            RelatedId.change_new_pwd,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelatedId {
        int bind_email = 0; //绑定邮箱id
        int pwd = 2;

        int bind_phone_number = 1; //绑定手机号id
        int bind_captcha = 3;

        int change_old_pwd = 4; //更改密码id
        int change_new_pwd = 5;
    }

    @Override
    public void initData() {
        super.initData();
        mCount = 0;

        mView = new baseSetBindViewImpl();
        mPresenter = new SetBindPresenterImpl(mView);
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();
        mTvSet = findView(R.id.base_set_tv_set);
    }

    @Override
    public final void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getNavBarText(), this);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();
        mView.initButtonStatus();
        setOnClickListener(R.id.base_set_tv_set);
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_set_tv_set: {
                refresh(RefreshWay.dialog);
                doSet();
            }
            break;
        }
    }

    @Deprecated
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Deprecated
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_set_footer);
    }

    /**
     * 获取标题文本
     *
     * @return 标题的文本
     */
    abstract protected CharSequence getNavBarText();

    /**
     * 获取按钮文本
     *
     * @return 按钮的文本
     */
    abstract protected CharSequence getSetText();

    /**
     * 点击按钮的操作
     */
    abstract protected void doSet();

    private class baseSetBindViewImpl implements SetBindContract.V {

        private View mView;

        @Override
        public void setChanged(boolean enabled) {
            mTvSet.setEnabled(enabled);
        }

        @Override
        public void initButtonStatus() {
            mTvSet.setEnabled(false);
            mTvSet.setText(getSetText());
        }

        @Override
        public void setBindEmailSuccessJump() {
            startActivity(ReceiveEmailTipsActivity.class);
        }

        @Override
        public void addItemCaptchaView() {
            mView = inflate(R.layout.dialog_captcha);
            TextView tv = mView.findViewById(R.id.captcha_tv_phone_number);
            String phone = getItemText(RelatedId.bind_phone_number);
            tv.setText(phone);
        }

        @Override
        public void showCaptchaDialog() {
            CommonDialog dialog = new CommonDialog(BaseSetActivity.this);
            dialog.addHintView(mView);
            dialog.addGrayButton(R.string.cancel);
            dialog.addBlueButton(getString(R.string.well), v1 -> {
                mCount++;
                YSLog.d("mCount:", mCount + "");
                if (mCount == 1) {
                    mStartTime = System.currentTimeMillis();
                }
                if (mCount > KMaxCount) {
                    long duration = System.currentTimeMillis() - mStartTime;
                    if (duration <= KCaptchaDuration) {
                        showToast(R.string.get_captcha_frequently);
                        return;
                    } else {
                        mCount = 1;
                    }
                }
                mPresenter.getCaptcha(RelatedId.bind_captcha, BindPhoneActivity.getPhone(), CaptchaType.re_fetch);
            });
            dialog.show();
        }

        @Override
        public String getItemText(int relatedId) {
            return getRelatedItem(relatedId).getVal();
        }

        @Override
        public void getCaptcha() {
            EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.bind_captcha);
            item.start();
        }

        @Override
        public void onFinish() {
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
        }
    }
}
