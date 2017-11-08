package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @auther WangLan
 * @since 2017/10/17
 */

public abstract class BaseSkipActivity extends BaseActivity implements OnCountDownListener {

    private final int KCountDownTime = 5; // 单位是秒

    private TextView mTvSkipText;
    private TextView mTvBack;
    private CountDown mCountDown;

    @Override
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start(KCountDownTime);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_skip;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, getTitleText(), this);
    }

    @Override
    public void findViews() {
        mTvSkipText = findView(R.id.tv_skip);
        mTvBack = findView(R.id.tv_back_login);
    }

    @Override
    public void setViews() {
        mTvSkipText.setText(getSkipText());
        setOnClickListener(R.id.tv_back_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_login: {
                finish();
            }
            break;
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        String s = String.format(getString(R.string.back_to_login), remainCount);
        mTvBack.setText(s);
        if (remainCount == 0) {
            finish();
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

    abstract protected CharSequence getTitleText();

    abstract protected CharSequence getSkipText();
}
