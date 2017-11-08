package jx.csp.ui.activity.login;

import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

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
import lib.ys.YSLog;
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

    private final int KInitVersion = 0; // 首次访问此接口，version = 0

    private CustomVideoView mCustomVideoView;

    private String mUrl;
    private String mFilePath;
    private String mFileName;

    @Override
    public void initData() {
        mFileName = CacheUtil.getVideoLoginFileName(SpApp.inst().getLoginVideoVersion());
        mFilePath = CacheUtil.getVideoCacheDir() + mFileName;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @CallSuper
    @Override
    public void findViews() {
        mCustomVideoView = findView(getVideoViewId());
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(R.id.login_mail);

        setOnClickListener(R.id.login_protocol);

        exeNetworkReq(KLoginVideo, UserAPI.loginVideo(SpApp.inst().getLoginVideoVersion()).build());
        int loginVideoVersion = SpApp.inst().getLoginVideoVersion();
        YSLog.d(TAG, "loginVideoVersion" + loginVideoVersion);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                startPlay();
                removeOnGlobalLayoutListener(this);
            }
        });
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
        if (id == KLoginVideo) {
            stopRefresh();
            Result<LoginVideo> r = (Result<LoginVideo>) result;
            if (r.isSucceed()) {
                LoginVideo data = r.getData();
                int newVersion = data.getInt(TLoginVideo.version);
                int oldVersion = SpApp.inst().getLoginVideoVersion(); // 保存本地
                mUrl = data.getString(TLoginVideo.videoUrl);

                if (TextUtil.isNotEmpty(mUrl) && newVersion > oldVersion) {
                    // fixme:在服务下载
                    exeNetworkReq(KDownLoadVideo, UserAPI.downLoad(CacheUtil.getVideoCacheDir(), mFileName, mUrl).build());
//                    CommonServRouter.create().fileName(mFileName).newVersion(newVersion).url(mUrl).route(this);
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
        if (TextUtil.isEmpty(mFilePath)) {
            return;
        }
        File file = new File(mFilePath);
        if (!file.exists()) {
            // 如果文件不存在，则还原到低版本
            SpApp.inst().saveLoginVideoVersion(KInitVersion);
            return;
        }

        if (mCustomVideoView == null) {
            return;
        }

        mCustomVideoView.setVideoPath(mFilePath);
        mCustomVideoView.setOnErrorListener((mediaPlayer, i, i1) -> false);
        mCustomVideoView.setOnPreparedListener(mediaPlayer -> mCustomVideoView.start());
        mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomVideoView != null) {
            mCustomVideoView.suspend();
            mCustomVideoView.stopPlayback();
        }
    }

    abstract protected int getVideoViewId();
}
