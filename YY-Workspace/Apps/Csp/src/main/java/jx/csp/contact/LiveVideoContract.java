package jx.csp.contact;

import android.content.Context;

import lib.jx.contract.IContract;
import lib.live.LiveView;

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

        void initLive(Context context, LiveView liveView);

        void startCountDown(long startTime, long stopTime, long serverTime);

        void startLive(String rtmpUrl, boolean mute);

        void stopLive();

        void switchCamera();

        void mute();
    }
}
