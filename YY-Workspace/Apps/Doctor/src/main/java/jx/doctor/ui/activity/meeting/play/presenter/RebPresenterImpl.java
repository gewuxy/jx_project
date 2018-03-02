package jx.doctor.ui.activity.meeting.play.presenter;

import android.content.Context;

import java.util.ArrayList;

import jx.doctor.ui.activity.meeting.OverviewActivityRouter;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */
public class RebPresenterImpl extends BasePptPresenterImpl<RebContact.View> implements RebContact.Presenter {

    private boolean auto;

    public RebPresenterImpl(RebContact.View view) {
        super(view);

        auto = true;
    }

    @Override
    public void start(int index) {
        auto = true;

        // 重新开启音频(开启ppt)
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
    public void toOverview(Context context, String title, int code) {
        OverviewActivityRouter.create(title, new ArrayList<>(mCourses)).route(context, code);
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
