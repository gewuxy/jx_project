package yy.doctor.ui.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.ui.activity.user.login.LoginActivity;

/**
 * 广告页
 *
 * @auther : GuoXuan
 * @since : 2017/10/23
 */
public class AdActivity extends BaseActivity {

    private NetworkImageView mIvAd;

    private TextView mTvPass;
    private CountDown mCountDown;

    public static void afterAd(Context context) {
        if (BuildConfig.TEST) {
            LaunchUtil.startActivity(context,TestActivity.class);
        } else if (Profile.inst().isLogin()) {
            // 登录有效(登录过且没有退出)
            MainActivityRouter.create().route(context);
        } else {
            // 未登录,退出登录
            LaunchUtil.startActivity(context,LoginActivity.class);
        }
    }

    @Override
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(new CountDown.OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                // fixme:文案内容
                mTvPass.setText(String.format("%d", remainCount));
                if (remainCount == 0) {
                    mTvPass.performClick();
                }
            }

            @Override
            public void onCountDownErr() {
                // do nothing
            }
        });
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_ad;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void findViews() {
        mIvAd = findView(R.id.ad_iv_ad);
        mTvPass = findView(R.id.ad_tv_pass);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvPass);

        // fixme:从配置中获取
        int count = 2;
        if (count <= 0) {
            mTvPass.performClick();
        } else {
            mCountDown.start(count);

            String url = "http://www.qingdaonews.com/images/attachement/gif/site1/20170516/0026c77e6d1a1a850db50c.gif";
            mIvAd.url(url).load();
            setOnClickListener(mIvAd);
        }
    }

    @Override
    public void onClick(View v) {
        mCountDown.stop();
        switch (v.getId()) {
            case R.id.ad_iv_ad: {
                // fixme:从配置中获取
                String htmlUrl = "http://59.111.90.245:8083/file/data/17051816413005881649/17082511344000160770/17082511344008521117.html";
                String title = "";
                AdWebViewActivityRouter.create(title, htmlUrl).route(this);
            }
            break;
            case R.id.ad_tv_pass: {
                afterAd(this);
            }
            break;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCountDown.recycle();
    }
}