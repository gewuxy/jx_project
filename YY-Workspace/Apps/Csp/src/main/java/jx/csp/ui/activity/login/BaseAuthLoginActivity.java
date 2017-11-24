package jx.csp.ui.activity.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import java.io.File;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.model.Profile;
import jx.csp.model.login.LoginVideo;
import jx.csp.model.login.LoginVideo.TLoginVideo;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.serv.DownloadServ.DownReqType;
import jx.csp.serv.DownloadServRouter;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.CacheUtil;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.UIUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/11/6
 */

abstract public class BaseAuthLoginActivity extends BaseActivity {

    private final int KInitVersion = 0; // 首次访问此接口，version = 0
    private final int KLoginVideo = 1;

    private PLVideoTextureView mVideo;
    private View mLayoutBg;

    private String mUrl;
    private String mFilePath;
    private String mFileName;

    @Override
    public void initData(Bundle state) {
        mFileName = CacheUtil.getVideoLoginFileName(SpApp.inst().getLoginVideoVersion());
        mFilePath = CacheUtil.getVideoCacheDir() + mFileName;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @CallSuper
    @Override
    public void findViews() {
        mVideo = findView(getVideoViewId());
        mLayoutBg = findView(getVideoBgId());
    }

    @SuppressLint("ResourceAsColor")
    @CallSuper
    @Override
    public void setViews() {
        UIUtil.setFlatBar(getWindow());
        getNavBar().setBackgroundColor(R.color.translucent);

        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_protocol);

        exeNetworkReq(KLoginVideo, UserAPI.loginVideo(SpApp.inst().getLoginVideoVersion()).build());
        // fixme:暂时使用铺满 link{@https://developer.qiniu.com/pili/sdk/1210/the-android-client-sdk#6}
        mVideo.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

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

    public OnAuthListener newListener(int id, @BindId int type) {
        return new OnAuthListener() {

            @Override
            public void onAuthSuccess(AuthParams params) {
                String userGender = params.getGender();
                String icon = params.getIcon();
                String userId = params.getId();
                String userName = params.getName();
                // 微信专用，其他不变
                String uniqueId = params.getUnionId();
                if (type == BindId.wechat) {
                    exeNetworkReq(id, UserAPI.login(type)
                            .uniqueId(uniqueId)
                            .nickName(userName)
                            .gender(userGender)
                            .avatar(icon)
                            .build());
                } else {
                    exeNetworkReq(id, UserAPI.login(type)
                            .uniqueId(userId)
                            .nickName(userName)
                            .gender(userGender)
                            .avatar(icon)
                            .build());
                }
                showToast(R.string.auth_success);
            }

            @Override
            public void onAuthError(String message) {
                showToast(R.string.auth_fail + message);
            }

            @Override
            public void onAuthCancel() {
            }
        };
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KLoginVideo) {
            return JsonParser.ev(r.getText(), LoginVideo.class);
        }
        return JsonParser.ev(r.getText(), Profile.class);
    }


    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KLoginVideo) {
            stopRefresh();
            if (r.isSucceed()) {
                LoginVideo data = (LoginVideo) r.getData();
                int oldVersion = SpApp.inst().getLoginVideoVersion(); // 读取本地
                int newVersion = data.getInt(TLoginVideo.version);
                mUrl = data.getString(TLoginVideo.videoUrl);
                YSLog.d(TAG, "onNetworkSuccess : newVersion = " + newVersion + " , oldVersion = " + oldVersion);
                if (TextUtil.isNotEmpty(mUrl) && newVersion > oldVersion) {
                    DownloadServRouter.create(DownReqType.login_video, mUrl, CacheUtil.getVideoLoginFileName(newVersion))
                            .newVersion(newVersion)
                            .route(this);
                }
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //返回重新加载
        startPlay();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //防止锁屏或者切出的时候，视频在播放
        if (mVideo.isPlaying()) {
            mVideo.stopPlayback();
        }
    }

    private void startPlay() {
        if (TextUtil.isEmpty(mFilePath)) {
            return;
        }
        File file = new File(mFilePath);
        if (!file.exists()) {
            // 如果文件不存在，则还原到低版本
            SpApp.inst().saveLoginVideoVersion(KInitVersion);
            /*DownloadServRouter.create(DownReqType.login_video, mUrl, CacheUtil.getVideoLoginFileName(KInitVersion))
                    .newVersion(KInitVersion)
                    .route(this);*/
            return;
        }

        if (mVideo == null) {
            return;
        }

        prepared(mFilePath);
        mVideo.setOnCompletionListener(mp -> prepared(mFilePath));
    }

    private void prepared(String path) {
        mVideo.setVideoPath(path);
        mVideo.setVolume(0, 0);
        mVideo.setOnPreparedListener(plMediaPlayer -> {
            if (mLayoutBg != null) {
                goneView(mLayoutBg);
            }
        });
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        } else if (type == NotifyType.login_video) {
            mFilePath = CacheUtil.getVideoCacheDir() + data;
            startPlay();
        }
    }

    @IdRes
    abstract protected int getVideoViewId();

    @IdRes
    abstract protected int getVideoBgId();
}
