package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;

/**
 * 忘记密码跳转和注册跳转的基类
 *
 * @auther WangLan
 * @since 2017/10/17
 */
@Route
public class SkipActivity extends BaseActivity implements OnCountDownListener {

    private final int KCountDownTime = 5; // 单位是秒

    private TextView mTvSkipText;
    private TextView mTvBack;
    private CountDown mCountDown;
    @Arg
    public String mTitleText;

    @Arg
    public String mSkipText;

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
        bar.addViewLeft(R.drawable.default_ic_close, v -> finish());
        bar.addTextViewMid(mTitleText);
        bar.setBackgroundResource(R.color.transparent);
        Util.addDivider(bar);
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void findViews() {
        mTvSkipText = findView(R.id.tv_skip);
        mTvBack = findView(R.id.tv_back_login);
    }

    @Override
    public void setViews() {
        mTvSkipText.setText(mSkipText);
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
}
