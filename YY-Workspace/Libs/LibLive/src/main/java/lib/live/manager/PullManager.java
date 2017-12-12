package lib.live.manager;

import android.content.Context;
import android.view.View;

import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLivePlayer;

import lib.live.TxProvider;
import lib.live.ui.LiveView;

/**
 * 拉流管理
 *
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
public class PullManager {

    private final TxProvider mProvider;

    public PullManager(Context context) {
        TXLiveBase.getSDKVersionStr();
        mProvider = new TxProvider(context);
    }

    /**
     * 开始拉流
     * {@link TXLivePlayer#startPlay}
     *
     * @param playUrl 拉流地址
     * @return true 成功(0) 失败(-1/-2)
     */
    public boolean startPullStream(String playUrl, LiveView view) {
        int playType;
        if (playUrl.startsWith("rtmp://")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            playType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        }
        mProvider.setPlayerView(view);
        return 0 == mProvider.startPlay(playUrl, playType);
    }

    /**
     * 停止拉流
     */
    public void stopPullStream() {
        mProvider.pause();
    }

    /**
     * 静音设置
     *
     * @param b 静音变量, true 表示静音, false 表示非静音
     */
    public void audio(boolean b) {
        mProvider.setMute(b);
    }
}
