package jx.doctor.ui.frag.meeting;

import android.support.annotation.NonNull;
import android.view.View;

import inject.annotation.router.Route;
import jx.doctor.BuildConfig;
import jx.doctor.R;
import lib.jx.ui.frag.base.BaseFrag;
import lib.live.pull.PullListener;
import lib.live.pull.PullManager;
import lib.live.ui.LiveView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * 直播部分
 *
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
@Route
public class PPTLiveFrag extends BaseFrag {

    private PullManager mPullManager;

    private View mLayoutDefault; // 无直播界面
    private View mLayoutLoading; // 加载中界面
    private View mLayoutBreak; // 直播断开界面

    private LiveView mViewLive; // 直播画面的载体

    private String mPlayUrl; // 直播的流

    private boolean mFirst;

    @Override
    public void initData() {
        mPullManager = new PullManager(getContext());
        mPullManager.listener(new PullListener() {
            @Override
            public void load() {
                showView(mLayoutLoading);
                goneView(mLayoutBreak);
                goneView(mLayoutDefault);
            }

            @Override
            public void begin() {
                goneView(mLayoutLoading);
                goneView(mLayoutBreak);
                goneView(mLayoutDefault);
                mFirst = false;
            }

            @Override
            public void end() {
                if (mFirst) {
                    mFirst = false;
                    showView(mLayoutDefault);
                    goneView(mLayoutLoading);
                    goneView(mLayoutBreak);
                } else {
                    showView(mLayoutBreak);
                    goneView(mLayoutLoading);
                    goneView(mLayoutDefault);
                }
            }
        });
        mFirst = true;
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
        // do nothing
    }

    public void setPlayUrl(String playUrl) {
        if (TextUtil.isEmpty(playUrl)) {
            return;
        }
        mPlayUrl = playUrl;
        if (BuildConfig.DEBUG) {
            mPlayUrl = "rtmp://3891.liveplay.myqcloud.com/live/3891_user_0da23c97_e723";
        }
        startPullStream();
    }

    public void startPullStream() {
        if (TextUtil.isEmpty(mPlayUrl) || mViewLive == null) {
            return;
        }
        if (mPullManager.startPullStream(mPlayUrl, mViewLive)) {
        } else {
        }
    }

    public void stopPullStream() {
        mPullManager.stopPullStream();
    }

    public void startAudio() {
        mPullManager.audio(false);
    }

    public void closeAudio() {
        mPullManager.audio(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
