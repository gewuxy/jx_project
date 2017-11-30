package yy.doctor.ui.activity.meeting.play;

import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import yy.doctor.R;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.ui.activity.meeting.play.contract.RebContact;
import yy.doctor.ui.activity.meeting.play.presenter.RebPresenterImpl;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class RebActivity extends BasePptActivity<RebContact.View, RebContact.Presenter> {

    @Override
    public void setViews() {
        super.setViews();

        goneView(R.id.play_layout_online);
    }

    @Override
    protected RebContact.View createV() {
        return new RebViewImpl();
    }

    @Override
    protected RebContact.Presenter createP(RebContact.View view) {
        return new RebPresenterImpl(view);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        getP().start(position);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getP().stop();
    }

    private class RebViewImpl extends BasePptViewImpl implements RebContact.View {

        @Override
        public boolean getNavBarLandscape() {
            return false;
        }

        @Override
        public int getControlResId() {
            return R.drawable.reb_select_control;
        }

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            super.portraitInit(ppt, courses);

            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    getP().start(0);
                    removeOnGlobalLayoutListener(this);
                }

            });
        }
    }

}
