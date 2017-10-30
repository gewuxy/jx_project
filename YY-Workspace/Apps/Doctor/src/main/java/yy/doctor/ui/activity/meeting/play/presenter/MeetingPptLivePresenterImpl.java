package yy.doctor.ui.activity.meeting.play.presenter;

import yy.doctor.ui.activity.meeting.play.contract.MeetingPptLiveContract;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class MeetingPptLivePresenterImpl extends MeetingPptPresenterImpl implements MeetingPptLiveContract.Presenter {

    public MeetingPptLivePresenterImpl(MeetingPptLiveContract.View view) {
        super(view);
    }

    @Override
    protected MeetingPptLiveContract.View getView() {
        return (MeetingPptLiveContract.View) super.getView();
    }
}
