package yy.doctor.ui.activity.meeting.play;

import inject.annotation.router.Route;
import yy.doctor.R;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptContract;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingPptPresenterImpl;

/**
 * 录播界面
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class MeetingRebActivity extends BaseMeetingPptActivity<MeetingPptContract.View, MeetingPptContract.Presenter> {

    @Override
    protected void set() {
        goneView(R.id.meet_play_layout_online);
    }

    @Override
    public MeetingPptContract.View createView() {
        return new MeetingPptViewImpl();
    }

    @Override
    public MeetingPptContract.Presenter createPresenter(MeetingPptContract.View view) {
        return new MeetingPptPresenterImpl(view);
    }

}
