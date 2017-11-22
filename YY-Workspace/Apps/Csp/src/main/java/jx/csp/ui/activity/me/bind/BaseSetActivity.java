package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.contact.SetBindContract;
import jx.csp.presenter.SetBindPresenterImpl;
import jx.csp.util.Util;
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
    public void initData(Bundle state) {
        super.initData(state);

        mView = new BaseSetBindViewImpl();
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
        mTvSet.setEnabled(false);
        mTvSet.setText(getSetText());
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

    protected void setChanged(boolean enabled){
        mTvSet.setEnabled(enabled);
    }

    protected class BaseSetBindViewImpl implements SetBindContract.V {

        @Override
        public void closePage() {
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
