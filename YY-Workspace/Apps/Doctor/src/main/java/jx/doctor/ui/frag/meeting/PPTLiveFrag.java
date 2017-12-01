package jx.doctor.ui.frag.meeting;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.TextureView;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.frag.base.BaseFrag;
import lib.live.ILiveCallback;
import lib.live.ILiveCallback.UserType;
import lib.live.LiveApi;
import jx.doctor.BuildConfig;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;

/**
 * 直播部分
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class PPTLiveFrag extends BaseFrag {

    @IntDef({
            LiveType.no_live,
            LiveType.loading,
            LiveType.living,
            LiveType.live_break,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LiveType {
        int no_live = 0; // 无直播
        int loading = 1; // 加载中
        int living = 2; // 直播中
        int live_break = 3; // 断开直播
    }

    private View mLayoutDefault; // 无直播界面
    private View mLayoutLoading; // 加载中界面
    private View mLayoutBreak; // 直播断开界面

    private TextureView mViewLive; // 直播画面的载体

    private LiveCallbackImpl mLiveCallbackImpl;

    private String mStream; // 直播的流

    private boolean mPull;

    @Override
    public void initData(Bundle savedInstanceState) {
        mLiveCallbackImpl = new LiveCallbackImpl();
        mPull = true;
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_ppt_live;
    }

    @Override
    public void initNavBar(NavBar bar) {
        // do nothing
    }

    @Override
    public void findViews() {
        mViewLive = findView(R.id.ppt_live_video);
        mLayoutDefault = findView(R.id.ppt_live_layout_default);
        mLayoutLoading = findView(R.id.ppt_live_layout_loading);
        mLayoutBreak = findView(R.id.ppt_live_layout_live_break);
    }

    @Override
    public void setViews() {
        setLiveState(LiveType.loading);
    }

    public void loginRoom(String roomId) {
        LiveApi.getInst().init(getContext(), Profile.inst().getString(TProfile.id), Profile.inst().getString(TProfile.linkman));
        // FIXME:
        LiveApi.getInst().setTest(BuildConfig.DEBUG_NETWORK);
        LiveApi.getInst().setCallback(roomId, UserType.audience, mLiveCallbackImpl);
    }

    public void startPullStream() {
        mPull = true;
        if (TextUtil.isEmpty(mStream) || mViewLive == null) {
            return;
        }
        setLiveState(LiveType.loading);
        if (LiveApi.getInst().startPullStream(mStream, mViewLive)) {
            setLiveState(LiveType.living);
        } else {
            setLiveState(LiveType.no_live);
        }
    }

    public void stopPullStream() {
        mPull = false;
        setLiveState(LiveType.live_break);
        LiveApi.getInst().stopPullStream(mStream);
    }

    public void startAudio() {
        LiveApi.getInst().audio(true);
    }

    public void closeAudio() {
        LiveApi.getInst().audio(false);
    }

    private void setLiveState(@LiveType int state) {
        switch (state) {
            case LiveType.no_live: {
                showView(mLayoutDefault);
                goneView(mLayoutLoading);
                goneView(mViewLive);
                goneView(mLayoutBreak);
            }
            break;
            case LiveType.loading: {
                goneView(mLayoutDefault);
                showView(mLayoutLoading);
                showView(mViewLive);
                goneView(mLayoutBreak);
            }
            break;
            case LiveType.living: {
                goneView(mLayoutDefault);
                goneView(mLayoutLoading);
                showView(mViewLive);
                goneView(mLayoutBreak);
            }
            break;
            case LiveType.live_break: {
                goneView(mLayoutDefault);
                goneView(mLayoutLoading);
                showView(mViewLive);
                showView(mLayoutBreak);
            }
            break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LiveApi.getInst().logoutRoom();
    }

    private class LiveCallbackImpl extends ILiveCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            if (i == 0) {
                YSLog.d(TAG, "loginRoom:success");
                if (TextUtil.isEmpty(stream)) {
                    // 直播未开始
                    setLiveState(LiveType.no_live);
                } else {
                    boolean state = LiveApi.getInst().startPullStream(stream, mViewLive);
                    if (state) {
                        // 拉流成功
                        setLiveState(LiveType.living);
                        mStream = stream;
                    }
                }
            } else {
                // 登录失败
                YSLog.d(TAG, "loginRoom:error ");
            }
        }

        @Override
        public void onUserUpdate(int number) {
        }

        @Override
        public void onStreamUpdated(int i, String stream) {
            if (!mPull) {
                return;
            }
            if (i == ILiveCallback.Constants.KStreamAdd) {
                YSLog.d(TAG, "onStreamUpdated:play");
                if (TextUtil.isNotEmpty(stream)) {
                    LiveApi.getInst().startPullStream(stream, mViewLive);
                    setLiveState(LiveType.living);
                    mStream = stream;
                }
            } else if (i == ILiveCallback.Constants.KStreamDel) {
                YSLog.d(TAG, "onStreamUpdated:stop");
                if (TextUtil.isNotEmpty(stream)) {
                    LiveApi.getInst().stopPullStream(stream);
                    setLiveState(LiveType.live_break);
                }
            }

        }
    }
}
