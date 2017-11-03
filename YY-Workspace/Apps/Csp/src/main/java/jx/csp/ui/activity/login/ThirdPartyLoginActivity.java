package jx.csp.ui.activity.login;

import android.net.Uri;
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
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.CustomVideoView;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
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
    private String mPath;
    private String mLocatePath;


    @Override
    public void initData() {
        mPlatAuth = new PlatformAuthorizeUserInfoManager(this);
        mPath = "";
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
        setOnClickListener(R.id.protocol);

        exeNetworkReq(KLoginVideo, UserAPI.loginVideo(KInitVersion).build());


            // 从本地获取 null

            //从本地获取视频,读文件是否存在
            File file = new File(mLocatePath);
            if (file.exists()) {
                startPlay();
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
            case R.id.protocol: {
                //Fixme:跳转到h5页面，现在还没有文案
                showToast("没有文案，先酱紫，哈哈");
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
                int version = data.getInt(TLoginVideo.version);
                int location = 0; // 本地

                if (version > location) {
                    String url = data.getString(TLoginVideo.videoUrl);
                    mPath = data.getString(TLoginVideo.videoUrl);

                    mLocatePath = CacheUtil.getAudioCacheDir();
                    String fileName = "login_background_video.mp4";

                    exeNetworkReq(KDownLoadVideo, UserAPI.downLoad(mLocatePath, fileName, url).build());

                    YSLog.d("url", url);
                }
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
        mCustomVideoView.setVideoPath(mPath);
        mCustomVideoView.start();
        mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mCustomVideoView.
    }
}
