package jx.doctor.ui.activity.meeting.play;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.List;

import inject.annotation.router.Route;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.ui.activity.meeting.play.presenter.RebPresenterImpl;
import lib.ys.ui.other.NavBar;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class RebActivity extends BasePptActivity<RebContact.View, RebContact.Presenter> {

    private final int KCodeReq = 10;

    @Override
    public int getContentViewId() {
        return R.layout.activity_ppt_reb;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        bar.addViewRight(R.drawable.nav_bar_ic_preview, l ->
                getP().toOverview(this, mUnitNum, KCodeReq));
    }

    @NonNull
    @Override
    protected RebContact.View createV() {
        return new RebViewImpl();
    }

    @NonNull
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KCodeReq && resultCode == RESULT_OK && data != null) {
            int index = data.getIntExtra(Extra.KData, 0);
            getFragPpt().setCurrPosition(index, false);
        }
    }

    private class RebViewImpl extends BasePptViewImpl implements RebContact.View {

        @Override
        public boolean getNavBarLandscape() {
            return false;
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
