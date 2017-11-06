package jx.csp.ui.activity.login;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.login.LoginVideo;
import jx.csp.model.login.LoginVideo.TLoginVideo;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.CacheUtil;
import jx.csp.view.CustomVideoView;
import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/11/6
 */

abstract public class BaseAuthLoginActivity extends BaseActivity {

    private final int KLoginVideo = 1;
    private final int KDownLoadVideo = 2;

    private final String KFileName = "login_background_video.mp4";
    private final int KInitVersion = 0; // 首次访问此接口，version= 0

    private CustomVideoView mCustomVideoView;

    private String mUrl;
    private String mLocatePath;

    @Override
    public void initData() {
        mLocatePath = CacheUtil.getAudioCacheDir() + KFileName;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @CallSuper
    @Override
    public void findViews() {
        mCustomVideoView = findView(R.id.login_videoview);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(R.id.login_mail);

        setOnClickListener(R.id.login_protocol);

        exeNetworkReq(KLoginVideo, UserAPI.loginVideo(KInitVersion).build());

        startPlay();
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_mail: {
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.login_protocol: {
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(getString(R.string.service_agreement))
                        .route(this);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KLoginVideo) {
            return JsonParser.ev(r.getText(), LoginVideo.class);
        }
        return JsonParser.ev(r.getText(), Profile.class);
    }


    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        if (id == KLoginVideo) {
            Result<LoginVideo> r = (Result<LoginVideo>) result;
            if (r.isSucceed()) {
                LoginVideo data = r.getData();
                int newVersion = data.getInt(TLoginVideo.version);
                int oldVersion = SpApp.inst().getLoginVideoVersion(); // 保存本地
                mUrl = data.getString(TLoginVideo.videoUrl);

                //当有视频跟新的时候，url和version都有值，此时新版本肯定比旧版本大，如果没有更新，没有返回数据
                if (TextUtil.isNotEmpty(mUrl) && newVersion > oldVersion) {
                    // fixme:在服务下载
                    exeNetworkReq(KDownLoadVideo, UserAPI.downLoad(CacheUtil.getAudioCacheDir(), KFileName, mUrl).build());
//                    LoginVideoDownLoadServRouter.create(mUrl).route(this);
                    SpApp.inst().saveLoginVideoVersion(newVersion);
                }
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    //返回重新加载
    @Override
    protected void onRestart() {
        super.onRestart();

        startPlay();
    }

    //防止锁屏或者切出的时候，视频在播放
    @Override
    protected void onStop() {
        super.onStop();

        if (mCustomVideoView.isPlaying()) {
            mCustomVideoView.stopPlayback();
        }
    }

    public void startPlay() {
        if (TextUtil.isEmpty(mLocatePath)) {
            return;
        }
        File file = new File(mLocatePath);
        if (!file.exists()) {
            return;
        }

        if (mCustomVideoView == null) {
            return;
        }

        mCustomVideoView.setVideoPath(mLocatePath);
        mCustomVideoView.setOnErrorListener((mediaPlayer, i, i1) -> false);
        mCustomVideoView.setOnPreparedListener(mediaPlayer -> mCustomVideoView.start());
        mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomVideoView != null) {
            //百度的两个释放的方法，也不知道哪个
            mCustomVideoView.suspend();
            mCustomVideoView.stopPlayback();
        }
    }

    @NonNull
    @Override
    abstract public int getContentViewId();
}
