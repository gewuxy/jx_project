package jx.csp.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Arg;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.contact.MyMessageContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.MyMessagePresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * 个人中心基类
 *
 * @auther HuoXuYu
 * @since 2017/9/21
 */

abstract public class BaseMyMessageActivity extends BaseActivity {

    @Arg
    TProfile mAttr;
    @Arg
    boolean mFlag;

    protected TextView mTv;

    public MyMessageContract.P mPresenter;
    public MyMessageContract.V mView;

    @Override
    public void initData() {
        mView = new MyMessageViewImpl();
        mPresenter = new MyMessagePresenterImpl(mView);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return 0;
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_message, this);
        mTv = bar.addTextViewRight(R.string.my_message_save, v -> {
            if (mFlag == false) {
                if (TextUtil.isEmpty(getEt().getText())) {
                    showToast(R.string.my_message_save_as_blank);
                } else {
                    refresh(RefreshWay.dialog);
                    doSet();
                }
            } else {
                refresh(RefreshWay.dialog);
                doSet();

            }
        });
        mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
    }

    @Override
    public void setViews() {
        mView.getText();
        mTv.setEnabled(true);
    }

    /**
     * 获取内容
     *
     * @return
     */
    abstract protected EditText getEt();

    /**
     * 点击按钮的操作
     */
    abstract protected void doSet();

    /**
     * 获取Profile里面对应enum的值
     *
     * @return
     */
    @NonNull
    protected String getVal() {
        return Profile.inst().getString(mAttr);
    }

    protected class MyMessageViewImpl implements MyMessageContract.V {

        @Override
        public void setData(String text) {
            Intent i = new Intent().putExtra(Extra.KData, text);
            setResult(RESULT_OK, i);
            finish();
        }

        @Override
        public void getText() {
            getEt().setText(getVal());
            getEt().setSelection(getEt().getText().length());
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
