package jx.doctor.ui.activity.meeting.play.presenter;

import android.content.Context;

import java.util.ArrayList;

import jx.doctor.Constants;
import jx.doctor.ui.activity.meeting.OverviewActivityRouter;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Time;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */
public class RebPresenterImpl extends BasePlayPresenterImpl<RebContact.View> implements RebContact.Presenter {

    private int mPosition;
    private int mProgress;

    public RebPresenterImpl(RebContact.View view) {
        super(view);

        mPosition = Constants.KInvalidValue;
        mProgress = Constants.KInvalidValue;
    }

    @Override
    public void playMedia(int position) {
        super.playMedia(position);

        if (mPosition == position) {
            // 同一个
            if (mProgress > 0) {
                NetPlayer.inst().setProgress(mProgress);
            }
        } else {
            mPosition = position;
            mProgress = 0;
        }
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
        mProgress = progress;
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
        String time = Time.getTime((NetPlayer.KMaxProgress - progress) * mMediaTime);
        getView().setTime(time);
    }

}
