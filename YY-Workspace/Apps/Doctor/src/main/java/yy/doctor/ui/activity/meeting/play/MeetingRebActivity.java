package yy.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import yy.doctor.R;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.ui.activity.meeting.play.contract.MeetingRebContact;
import yy.doctor.ui.activity.meeting.play.presenter.MeetingRebPresenterImpl;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class MeetingRebActivity extends BasePptActivity<MeetingRebContact.View, MeetingRebContact.Presenter> {

    private MeetingRebContact.View mView;
    private MeetingRebContact.Presenter mPresenter;

    @Override
    public void setViews() {
        super.setViews();

        goneView(R.id.meet_play_layout_online);
    }

    @Override
    protected MeetingRebContact.View createView() {
        if (mView == null) {
            mView = new MeetingRebViewImpl();
        }
        return mView;
    }

    @Override
    protected MeetingRebContact.Presenter createPresenter(MeetingRebContact.View view) {
        if (mPresenter == null) {
            mPresenter = new MeetingRebPresenterImpl(view);
        }
        return mPresenter;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        mPresenter.start(position);
    }

    @Override
    protected boolean getNavBarLandscape() {
        return false;
    }

    @Override
    protected int getControlResId() {
        return R.drawable.meet_play_reb_select_control;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.stop();
    }

    private class MeetingRebViewImpl extends BasePptViewImpl implements MeetingRebContact.View {

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            super.portraitInit(ppt, courses);

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mPresenter.start(0);
                    removeOnGlobalLayoutListener(this);
                }

            });
        }
    }

}
