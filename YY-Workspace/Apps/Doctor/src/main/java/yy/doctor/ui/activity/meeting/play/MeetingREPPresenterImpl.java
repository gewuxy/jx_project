package yy.doctor.ui.activity.meeting.play;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class MeetingREPPresenterImpl extends BasePresenter implements MeetingREPContract.Presenter {

    private MeetingREPContract.View mView;

    public MeetingREPPresenterImpl(MeetingREPContract.View view) {
        mView = view;
    }

    @Override
    public void createAdapter() {

    }
}
