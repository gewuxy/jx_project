package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import inject.annotation.router.Route;
import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.model.login.LoginVideo;
import jx.csp.model.login.LoginVideo.TLoginVideo;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.CustomVideoView;
import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * 第三方登录
 *
 * @auther WangLan
 * @since 2017/9/27
 */
@Route
public class ThirdPartyLoginActivity extends BaseActivity {

    private final int KWechatLogin = 1;
    private final int KWeiboLogin = 2;
    private final int KLoginVideo = 3;
    private final int KDownLoadVideo = 4;

    private final int KInitVersion = 0; // 首次访问此接口，version= 0


    private PlatformAuthorizeUserInfoManager mPlatAuth;

    private CustomVideoView mCustomVideoView;
    private String mUrl;
    private String mLocatePath;
    private String mFileName;


    @Override
    public void initData() {
        mPlatAuth = new PlatformAuthorizeUserInfoManager(this);
        mFileName = "login_background_video.mp4";
        mLocatePath = CacheUtil.getAudioCacheDir() + File.separator  + mFileName;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mCustomVideoView = findView(R.id.login_videoview);
    }


    @Override
    public void setViews() {
        setOnClickListener(R.id.layout_login_wechat);
        setOnClickListener(R.id.layout_login_sina);
        setOnClickListener(R.id.layout_login_jx);
        setOnClickListener(R.id.login_mobile);
        setOnClickListener(R.id.login_mail);
        setOnClickListener(R.id.login_protocol);

        exeNetworkReq(KLoginVideo, UserAPI.loginVideo(KInitVersion).build());

        if (Util.noNetwork()) {
            //没网的时候，从本地获取视频,读文件是否存在，不存在则空，存在则播放
            try {
                File file = new File(mLocatePath);
                if (file.exists()) {
                    startPlay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login_wechat: {
              /*  mPlatAuth.WeiXinAuthorize();
                exeNetworkReq(KWechatLogin, UserAPI.login(LoginType.wechat_login).uniqueId(mUserId).nickName(mUserName).gender(mUserGender)
                        .avatar(mIcon).build());*/
            }
            break;
            case R.id.layout_login_sina: {
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                sina.setPlatformActionListener(new PlatformActionListener() {

                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        showToast("授权成功");
                        PlatformDb platDB = platform.getDb();
                        String userGender = platDB.getUserGender();
                        String icon = platDB.getUserIcon();
                        String userName = platDB.getUserName();
                        String userId = platDB.getUserId();
                        exeNetworkReq(KWeiboLogin,
                                UserAPI.login(LoginType.weibo_login)
                                        .uniqueId(userId)
                                        .nickName(userName)
                                        .gender(userGender)
                                        .avatar(icon)
                                        .build());
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        showToast("失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        showToast("取消");
                    }
                });
                sina.SSOSetting(false);
                sina.authorize();

            }
            break;
            case R.id.layout_login_jx: {
                startActivity(YaYaAuthorizeLoginActivity.class);
            }
            break;
            case R.id.login_mobile: {
                startActivity(CaptchaLoginActivity.class);
            }
            break;
            case R.id.login_mail: {
                startActivity(EmailLoginActivity.class);
            }
            break;
            case R.id.login_protocol: {
                CommonWebViewActivityRouter.create(getString(R.string.service_agreement), UrlUtil.getUrlDisclaimer())
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
                if (TextUtil.isNotEmpty(mUrl)&&newVersion > oldVersion) {
//                    startPlay();
                    mCustomVideoView.setVideoPath(mUrl);
                    mCustomVideoView.start();
                    mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());

                    exeNetworkReq(KDownLoadVideo, UserAPI.downLoad(mLocatePath, mFileName, mUrl).build());
                    SpApp.inst().saveLoginVideoVersion(newVersion);
                } else {
                    //从本地获取视频,读文件是否存在
                    try {
                        File file = new File(mLocatePath);
                        if (file.exists()) {
                            startPlay();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else {
                onNetworkError(id, r.getError());
            }
        } else if (id == KWeiboLogin || id == KWechatLogin) {
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                SpUser.inst().updateProfileRefreshTime();
                startActivity(MainActivity.class);
                finish();
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
        mCustomVideoView.stopPlayback();
    }

    public void startPlay(){
        if (mLocatePath != null) {
            mCustomVideoView.setVideoPath(mLocatePath);
            mCustomVideoView.start();
            mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
        }
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

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.login) {
            finish();
        }
    }
}
