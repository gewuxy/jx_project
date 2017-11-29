package yy.doctor.ui.activity.meeting.play.presenter;

import yy.doctor.ui.activity.meeting.play.contract.MeetingRebContact;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */

public class MeetingRebPresenterImpl extends BasePptPresenterImpl<MeetingRebContact.View> implements MeetingRebContact.Presenter {

    private boolean auto;

    public MeetingRebPresenterImpl(MeetingRebContact.View view) {
        super(view);

        auto = true;
    }

    @Override
    public void start(int index) {
        auto = true;

        // fixme : 重新开启音频？
        playMedia(index);
        getView().onPlayState(auto);
    }

    @Override
    public void stop() {
        auto = false;

        removeMessages();
        stopMedia();
        getView().onPlayState(auto);
    }

    @Override
    public void toggle(int index) {
        super.toggle(index);

        if (auto) {
            stop();
        } else {
            start(index);
        }
    }

    @Override
    public void playMedia(int position) {
        if (auto) {
            super.playMedia(position);
        }
    }

    @Override
    public void onPreparedSuccess(long allMillisecond) {
        super.onPreparedSuccess(allMillisecond);

        getView().onPlayState(true);
    }
}
