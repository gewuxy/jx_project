package yy.doctor.ui.activity.meeting.play;

import inject.annotation.router.Route;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptLiveContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingPptLivePresenterImpl;

/**
 * ppt直播(无视频)
 *
 * @auther : GuoXuan
 * @since : 2017/10/27
 */
@Route
public class MeetingPptLiveActivity extends BaseMeetingPptActivity<MeetingPptLiveContract.View, MeetingPptLiveContract.Presenter> {

    @Override
    protected void set() {

    }

    @Override
    protected MeetingPptLiveViewImpl createView() {
        return new MeetingPptLiveViewImpl();
    }

    @Override
    protected MeetingPptLiveContract.Presenter createPresenter(MeetingPptLiveContract.View view) {
        return new MeetingPptLivePresenterImpl(view);
    }

    private class MeetingPptLiveViewImpl extends MeetingPptViewImpl implements MeetingPptLiveContract.View {

    }
}
