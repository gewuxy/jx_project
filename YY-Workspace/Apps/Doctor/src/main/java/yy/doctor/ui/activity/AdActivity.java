package yy.doctor.ui.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import yy.doctor.R;
import yy.doctor.model.Ad;
import yy.doctor.model.Ad.TAd;
import yy.doctor.model.Profile;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.sp.SpApp;
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
    private String mHtmlUrl;

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
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(new CountDown.OnCountDownListener() {

            @Override
            public void onCountDown(long remainCount) {
                mTvPass.setText(remainCount + "跳过");
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
            mCountDown.start(count);

            mIvAd.url(url).load();
            setOnClickListener(mIvAd);
        }

    }

    @Override
    public void onClick(View v) {
        mCountDown.stop();
        switch (v.getId()) {
            case R.id.ad_iv_ad: {
                String title = "";
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

        mCountDown.recycle();
    }
}
