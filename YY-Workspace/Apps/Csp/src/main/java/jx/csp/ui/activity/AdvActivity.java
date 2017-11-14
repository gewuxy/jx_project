package jx.csp.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.constant.MetaValue;
import jx.csp.model.Profile;
import jx.csp.model.login.Advert;
import jx.csp.model.login.Advert.TAdvert;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.AdvertAPI;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.AuthLoginOverseaActivity;
import jx.csp.ui.activity.main.MainActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.PackageUtil;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 广告页
 *
 * @auther WangLan
 * @since 2017/9/20
 */
public class AdvActivity extends ActivityEx implements OnClickListener, OnCountDownListener {

    private final int KDelayTime = 3; // 3秒跳转

    private CountDown mCountDown;

    private NetworkImageView mIv;
    private String mPageUrl;


    @Override
    public void initData(Bundle state) {
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
        mIv = findView(R.id.adv_iv);
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

        exeNetworkReq(AdvertAPI.advert().build());
    }

    @Override
    public void onClick(View v) {
        finish();
        switch (v.getId()) {
            case R.id.adv_iv: {
                //点击广告跳到h5页面
                AdWebViewActivityRouter.create(mPageUrl).route(this);
            }
            break;
            case R.id.adv_skip: {
                afterAd(this);
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Advert.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            Advert adv = (Advert) r.getData();
            mIv.url(adv.getString(TAdvert.imgUrl)).load();
            mPageUrl = adv.getString(TAdvert.pageUrl);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            afterAd(this);
            finish();
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCountDown.recycle();
    }

    public static void afterAd(Context context) {
        if (Profile.inst().isLogin()) {
            // 登录有效(登录过且没有退出)
            LaunchUtil.startActivity(context, MainActivity.class);
        } else {
            // 未登录,退出登录
            if (Constants.KAppTypeCn.equals(PackageUtil.getMetaValue(MetaValue.app_type))) {
                LaunchUtil.startActivity(context, AuthLoginActivity.class);
            }else {
                LaunchUtil.startActivity(context, AuthLoginOverseaActivity.class);
            }
        }
    }
}
