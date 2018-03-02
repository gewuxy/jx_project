package jx.doctor.ui.activity.meeting.play.presenter;

import android.content.Context;

import java.util.ArrayList;

import jx.doctor.model.meet.ppt.Course;
import jx.doctor.ui.activity.meeting.OverviewActivityRouter;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.ui.activity.meeting.play.contract.RebContact1;
import jx.doctor.util.NetPlayer;

/**
 * @auther : GuoXuan
 * @since : 2017/11/17
 */
public class RebPresenterImpl1 extends BasePlayPresenterImpl<RebContact1.View> implements RebContact1.Presenter {

    public RebPresenterImpl1(RebContact1.View view) {
        super(view);
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
    public void toOverview(Context context, String title, int code) {
        OverviewActivityRouter.create(title, new ArrayList<>(getCourses())).route(context, code);
    }

}
