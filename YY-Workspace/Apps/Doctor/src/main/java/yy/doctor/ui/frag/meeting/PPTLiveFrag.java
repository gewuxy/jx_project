package yy.doctor.ui.frag.meeting;

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
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.ui.activity.meeting.play.BaseMeetingPlayActivity.OnLiveListener;

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

    private View mLayoutDefault;
    private TextureView mViewLive;
    private View mLayoutLoading;
    private View mLayoutBreak;

    private LiveCallbackImpl mZegoCallbackImpl;

    private OnLiveListener mListener;

    public void setListener(OnLiveListener listener) {
        mListener = listener;
    }

    @Override
    public void initData() {
        mZegoCallbackImpl = new LiveCallbackImpl();
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_ppt_live;
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
        if (BuildConfig.TEST) {
            LiveApi.getInst().setTest(true);
            roomId = "789";
        }
        LiveApi.getInst().setCallback(roomId, UserType.audience, mZegoCallbackImpl);
    }

    public void startAudio() {
        LiveApi.getInst().audio(true);
    }

    public void stopAudio() {
        LiveApi.getInst().audio(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LiveApi.getInst().logoutRoom();
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

    private class LiveCallbackImpl extends ILiveCallback {

        @Override
        public void onLoginCompletion(int i, String stream) {
            if (i == 0) {
                YSLog.d(TAG, "loginRoom:success");
                if (TextUtil.isEmpty(stream)) {
                    // 直播未开始
                    setLiveState(LiveType.no_live);
                } else {
                    setLiveState(LiveType.living);
                    LiveApi.getInst().startPullStream(stream, mViewLive);
                }
            } else {
                // 登录失败
                YSLog.d(TAG, "loginRoom:error ");
            }
        }

        @Override
        public void onUserUpdate(int number) {
            if (mListener != null) {
                mListener.online(number);
            }
        }

        @Override
        public void onStreamUpdated(int i, String stream) {
            if (i == ILiveCallback.Constants.KStreamAdd) {
                YSLog.d(TAG, "onStreamUpdated:play");
                if (TextUtil.isNotEmpty(stream)) {
                    LiveApi.getInst().startPullStream(stream, mViewLive);
                    setLiveState(LiveType.living);
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
