package jx.doctor.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.model.Ad;
import jx.doctor.model.Ad.TAd;
import jx.doctor.model.Profile;
import jx.doctor.serv.CommonServ.ReqType;
import jx.doctor.serv.CommonServRouter;
import jx.doctor.sp.SpApp;
import jx.doctor.ui.activity.user.login.LoginActivity;

/**
 * 广告页
 *
 * @auther : GuoXuan
 * @since : 2017/10/23
 */
public class AdActivity extends BaseActivity {

    private NetworkImageView mIvAd;
    private TextView mTvPass;

    private String mHtmlUrl;
    private Handler mHandler;

    public static void afterAd(Context context) {
        if (Profile.inst().isLogin()) {
            // 登录有效(登录过且没有退出)
            LaunchUtil.startActivity(context, MainActivity.class);
        } else {
            // 未登录,退出登录
            LaunchUtil.startActivity(context, LoginActivity.class);
        }
    }

    @Override
    public void initData(Bundle state) {
        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                mTvPass.performClick();
            }

        };
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

        CommonServRouter.create().type(ReqType.advert).route(this);
        int count = 0;
        String url = ConstantsEx.KEmpty;
        Ad advert = SpApp.inst().getAdvert();
        if (advert != null) {
            url = advert.getString(TAd.imageUrl);
            count = advert.getInt(TAd.skipTime);
            mHtmlUrl = advert.getString(TAd.pageUrl);
        }
        if (count <= 0) {
            mTvPass.performClick();
        } else {
            mHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(count));
            mIvAd.url(url).load();
            setOnClickListener(mIvAd);
        }

    }

    @Override
    public void onClick(View v) {
        mHandler.removeCallbacksAndMessages(null);
        switch (v.getId()) {
            case R.id.ad_iv_ad: {
                String title = ConstantsEx.KEmpty;
                if (TextUtil.isEmpty(mHtmlUrl)) {
                    return;
                }
                AdWebViewActivityRouter.create(title, mHtmlUrl).route(this);
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
    protected void startInAnim() {
        // 取消转场动画
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);
    }
}
