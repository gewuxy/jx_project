package yaya.csp.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class AdvActivity extends ActivityEx implements View.OnClickListener, OnCountDownListener {
    private final int KDelayTime = 3; // 3秒跳转

    private CountDown mCountDown;

    @Override
    public void initData() {
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
        ImageView mAdvIv = findView(R.id.adv_iv);
        TextView mAdvEdit = findView(R.id.adv_skip);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.adv_iv);
        setOnClickListener(R.id.adv_skip);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start(KDelayTime);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (mCountDown != null) {
            mCountDown.stop();
            finish();
        }
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
        if (mCountDown != null) {
            mCountDown.stop();
        }
    }
}
