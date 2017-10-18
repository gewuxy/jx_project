package jx.csp.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import jx.csp.R;
import jx.csp.model.login.Advert;
import jx.csp.model.login.Advert.TAdvert;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkAPIDescriptor.AdvertAPI;
import lib.network.model.NetworkResp;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.activity.ActivityEx;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
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
                CommonWebViewActivityRouter.create("...",mPageUrl).route(this);
            }
            break;
            case R.id.adv_skip: {
                startActivity(TestActivity.class);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Advert.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Advert> r = (Result<Advert>) result;
        if (r.isSucceed()) {
            Advert adv = r.getData();
            mIv.url(adv.getString(TAdvert.imgUrl)).load();
            mPageUrl = adv.getString(TAdvert.pageUrl);
        }else {
            onNetworkError(id, r.getError());
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
