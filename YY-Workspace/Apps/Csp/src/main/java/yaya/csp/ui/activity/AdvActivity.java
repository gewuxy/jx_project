package yaya.csp.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.other.NavBar;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yaya.csp.R;

/**
 * 广告页
 *
 * @auther WangLan
 * @since 2017/9/20
 */
public class AdvActivity extends ActivityEx implements OnClickListener, OnCountDownListener {

    private final int KDelayTime = 3; // 3秒跳转

    private CountDown mCountDown;

    @Override
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_adv;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.adv_iv);
        setOnClickListener(R.id.adv_skip);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCountDown.start(KDelayTime);
                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
        switch (v.getId()) {
            case R.id.adv_iv: {
                //点击广告跳到h5页面
                //Fixme:现在只是假设跳到TestActivity。
                startActivity(TestActivity.class);
            }
            break;
            case R.id.adv_skip: {
                startActivity(TestActivity.class);
            }
            break;
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            startActivity(TestActivity.class);
            finish();
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCountDown.stop();
        mCountDown.recycle();
    }
}
