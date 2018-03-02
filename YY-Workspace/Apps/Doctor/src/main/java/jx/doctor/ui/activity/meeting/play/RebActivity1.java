package jx.doctor.ui.activity.meeting.play;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import inject.annotation.router.Route;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.RebContact1;
import jx.doctor.ui.activity.meeting.play.presenter.RebPresenterImpl1;
import jx.doctor.ui.frag.meeting.course.PicCourseFrag;
import jx.doctor.util.Util;
import lib.ys.ui.other.NavBar;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class RebActivity1 extends BasePptActivity1<RebContact1.View, RebContact1.Presenter> {

    private final int KCodeReq = 10;

    private View mLayoutMedia; // 音频时长
    private ImageView mOverView;

    @Override
    public int getContentViewId() {
        return R.layout.activity_ppt_reb;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        ViewGroup viewGroup = bar.addViewRight(R.drawable.nav_bar_ic_preview, l ->
                mP.toOverview(this, mUnitNum, KCodeReq));
        mOverView = Util.getBarView(viewGroup, ImageView.class);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutMedia = findView(R.id.ppt_reb_layout_media_time);
    }

    @NonNull
    @Override
    protected RebContact1.View createV() {
        return new RebViewImpl();
    }

    @NonNull
    @Override
    protected RebContact1.Presenter createP(RebContact1.View view) {
        return new RebPresenterImpl1(view);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        mP.playMedia(position);
        mediaTime(position);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mP.stopMedia();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KCodeReq && resultCode == RESULT_OK && data != null) {
            int index = data.getIntExtra(Extra.KData, 0);
            mFragPpt.setCurrPosition(index, false);
        }
    }

    private void mediaTime(int position) {
        if (mFragPpt.getItem(position) instanceof PicCourseFrag) {
            goneView(mLayoutMedia);
        } else {
            showView(mLayoutFrag);
        }
    }

    private class RebViewImpl extends BasePptViewImpl implements RebContact1.View {

        @Override
        public void onNetworkSuccess(PPT ppt) {
            super.onNetworkSuccess(ppt);

            mP.playMedia(0);
            mediaTime(0);
        }

        @Override
        public void portrait() {
            super.portrait();

            showView(mOverView);
        }

        @Override
        public void landscape() {
            super.landscape();

            goneView(mOverView);
        }

        @Override
        public void landscapeIntercept() {
            super.landscapeIntercept();

            mFragPpt.landscapeVisibility(true);
        }
    }

}
