package jx.csp.contact;

import android.view.TextureView;

import lib.yy.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */
public interface LiveVideoContract {

    interface V extends IContract.View {

        void setLiveTime(String s);

        void setCountDownRemind(int i);

        void startLiveState();

        void stopLiveState();

        void liveFailState();

        void changeLiveIvRes();

        void setSilenceIvSelected(boolean b);

        void finishLive();
    }

    interface P extends IContract.Presenter<V> {

        void initLiveRoom(String roomId, TextureView textureView);

        void startCountDown(long startTime, long stopTime, long serverTime);

        void startLive(String streamId, String title);

        void stopLive();

        void switchCamera();

        void useMic();
    }
}
