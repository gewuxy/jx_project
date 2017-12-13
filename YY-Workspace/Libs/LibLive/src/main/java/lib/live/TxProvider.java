package lib.live;

import android.content.Context;
import android.os.Bundle;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;

import lib.live.ui.LiveView;

/**
 * 腾讯
 * https://cloud.tencent.com/document/product/454/7877
 *
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
public class TxProvider extends BaseProvider {

    private TXLivePlayer mPlayer;

    private LiveView mView;

    public TxProvider(Context context) {
        super(context);

        mPlayer = new TXLivePlayer(context);
    }

    @Override
    public void loginRoom() {

    }

    @Override
    public void logoutRoom() {
    }

    public TxProvider Listener(LiveListener listener) {
        mPlayer.setPlayListener(new ITXLivePlayListener() {

            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                switch (i) {
                    case TXLiveConstants.PLAY_EVT_PLAY_LOADING: {
                        listener.load();
                    }
                    break;
                    case TXLiveConstants.PLAY_EVT_PLAY_BEGIN: {
                        listener.begin();
                    }
                    break;

                }


            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });
        return this;
    }

    @Override
    public void onDestroy() {
        mView.onDestroy();
    }

    public int startPlay(String playUrl, int type) {
        return mPlayer.startPlay(playUrl, type);
    }

    public void pause() {
        mPlayer.pause();
    }

    public void setPlayerView(LiveView view) {
        mView = view;
        mPlayer.setPlayerView(view);
    }

    public void setMute(boolean b) {
        mPlayer.setMute(b);
    }

}
