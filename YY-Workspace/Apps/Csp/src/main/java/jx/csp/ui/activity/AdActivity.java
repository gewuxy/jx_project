package jx.csp.ui.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.login.Advert;
import jx.csp.model.login.Advert.TAdvert;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.ui.activity.login.auth.AuthLoginActivity;
import jx.csp.ui.activity.login.auth.AuthLoginOverseaActivity;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;

/**
 * 广告页
 *
 * @auther WangLan
 * @since 2017/9/20
 */
public class AdActivity extends BaseActivity {

    private NetworkImageView mIv;
    private String mPageUrl;

    private Handler mHandler;
    private View mTv;

    @Override
    public void initData() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                mTv.performClick();
            }

        };
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_adv;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void findViews() {
        mIv = findView(R.id.adv_iv);
        mTv = findView(R.id.adv_skip);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.adv_skip);

        CommonServRouter.create(ReqType.advert).route(this);
        int count = 0;
        String url = ConstantsEx.KEmpty;
        Advert advert = SpApp.inst().getAdvert();
        if (advert != null) {
            url = advert.getString(TAdvert.imgUrl);
            count = advert.getInt(TAdvert.countDown);
            mPageUrl = advert.getString(TAdvert.pageUrl);
        }
        if (count <= 0) {
            mTv.performClick();
        } else {
            mHandler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(count));
            mIv.url(url).load();
            setOnClickListener(mIv);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adv_iv: {
                //点击广告跳到h5页面
                AdWebViewActivityRouter.create(mPageUrl).route(this);
            }
            break;
            case R.id.adv_skip: {
                afterAd(AdActivity.this);
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

    public static void afterAd(Context context) {
        // 先判断是否需要显示引导页 如果不显示则判断跳转到登陆还是首页
        if (SpApp.inst().getGuideState()) {
            LaunchUtil.startActivity(context, GuideActivity.class);
        } else {
            if (Profile.inst().isLogin()) {
                // 登录有效(登录过且没有退出)
                LaunchUtil.startActivity(context, MainActivity.class);
            } else {
                // 未登录,退出登录
                if (Util.checkAppCn()) {
                    LaunchUtil.startActivity(context, AuthLoginActivity.class);
                } else {
                    LaunchUtil.startActivity(context, AuthLoginOverseaActivity.class);
                }
            }
        }
    }
}
