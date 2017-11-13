package jx.csp.contact;

import android.view.TextureView;

import lib.yy.contract.IContract;

/**
 * @author CaiXiang
 * @since 2017/9/22
 */
public interface LiveRoomContract {

    interface View extends IContract.View {

        TextureView getTextureView();

        void setLiveTimeTv(String s);

        void setCountDownRemindTv(int i);

        void startLiveState();

        void stopLiveState();

        void liveFailState();

        void changeLiveIvRes();

        void setSilenceIvSelected(boolean b);

        void onFinish();
    }

    interface Presenter extends IContract.Presenter<View> {

        void initLiveRoom(String roomId);

        void startCountDown(long startTime, long stopTime);

        void startLive(String streamId, String title);

        void stopLive();

        void switchCamera();

        void useMic();
    }

}
