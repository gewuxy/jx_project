package lib.live.manager;

import android.content.Context;
import android.os.Bundle;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;

import lib.live.LiveListener;
import lib.live.ui.LiveView;
import lib.ys.util.TextUtil;

/**
 * 拉流管理
 *
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
public class PullManager {

    private TXLivePlayer mPlayer;

    private LiveView mView;
    private Context mContext;
    private LiveListener mListener;

    public PullManager(Context context) {
//        TXLiveBase.getSDKVersionStr();

        mContext = context;
    }

    public void listener(LiveListener listener) {
        mListener = listener;
    }

    /**
     * 开始拉流
     * {@link TXLivePlayer#startPlay}
     *
     * @param playUrl 拉流地址
     * @return true 成功(0) 失败(-1/-2)
     */
    public boolean startPullStream(String playUrl, LiveView view) {
        if (TextUtil.isEmpty(playUrl) || view == null) {
            return false;
        }
        int playType;
        if (playUrl.startsWith("rtmp://")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        }
        mView = view;
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        mPlayer.setPlayerView(mView);
        mPlayer.setPlayListener(new ITXLivePlayListener() {

            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                switch (i) {
                    case TXLiveConstants.PLAY_EVT_PLAY_LOADING: {
                        mListener.load();
                    }
                    break;
                    case TXLiveConstants.PLAY_EVT_PLAY_BEGIN: {
                        mListener.begin();
                    }
                    break;

                }

            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });
        return 0 == mPlayer.startPlay(playUrl, playType);
    }

    /**
     * 停止拉流
     */
    public void stopPullStream() {
        mPlayer.pause();
        mPlayer.stopPlay(true);
        mPlayer = null;
    }

    /**
     * 静音设置
     *
     * @param b 静音变量, true 表示静音, false 表示非静音
     */
    public void audio(boolean b) {
        mPlayer.setMute(b);
    }
}
