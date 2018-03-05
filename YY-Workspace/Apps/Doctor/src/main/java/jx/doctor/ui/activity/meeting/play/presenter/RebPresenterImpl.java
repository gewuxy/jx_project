package jx.doctor.ui.activity.meeting.play.presenter;

import android.content.Context;

import java.util.ArrayList;

import jx.doctor.ui.activity.meeting.OverviewActivityRouter;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Time;
import lib.ys.util.UtilEx;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */
public class RebPresenterImpl extends BasePlayPresenterImpl<RebContact.View> implements RebContact.Presenter {

    private int mProgress;

    public RebPresenterImpl(RebContact.View view) {
        super(view);
    }

    @Override
    protected void nativePlay(int position) {
        boolean b = mPosition == position;
        super.nativePlay(position);
        if (b && mProgress != 0) {
            // 同一个
            UtilEx.runOnUIThread(() -> {
                NetPlayer.inst().seekTo((int) (mProgress * mMediaTime / NetPlayer.KMaxProgress));
                mProgress = 0;
            }, 100);
        }
    }

    @Override
    public void stopMedia() {
        super.stopMedia();

        getView().recordProgress();
    }

    @Override
    public void toggle(int index) {
        super.toggle(index);

        if (mPlay) {
            playMedia(index);
        } else {
            stopMedia();
        }
    }

    @Override
    protected void playProgress(String time, int progress) {
        getView().playProgress(time, progress);
    }

    @Override
    public void toOverview(Context context, String title, int code) {
        OverviewActivityRouter.create(title, new ArrayList<>(getCourses())).route(context, code);
    }

    @Override
    public void changeTime(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > NetPlayer.KMaxProgress) {
            progress = NetPlayer.KMaxProgress;
        }
        mProgress = progress;
        String time = Time.getTime((NetPlayer.KMaxProgress - progress) * mMediaTime / NetPlayer.KMaxProgress);
        getView().setTime(time);
    }

    @Override
    public void setProgress(int progress) {
        mProgress = progress;
    }

}
