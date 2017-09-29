package jx.csp.ui.activity.liveroom;

import android.view.TextureView;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */

public interface LiveRoomContract {

    interface LiveRoomView {

        TextureView getTextureView();

        void setLiveTimeTv(String s);

        void setCountDownRemindTv(boolean show, int i);

        void setOnlineNumTv(int i);

        void startLiveState();

        void stopLiveState();

        void changeLiveIvRes();

        void setSilenceIvSelected(boolean b);

        void onFinish();
    }

    interface LiveRoomPresenter {

        void initLiveRoom(String roomId);

        void zegoCallback();

        void startCountDown(long startTime, long stopTime);

        void startLive(String streamId, String title);

        void stopLive();

        void switchCamera();

        void useMic();

        void onDestroy();
    }

}
