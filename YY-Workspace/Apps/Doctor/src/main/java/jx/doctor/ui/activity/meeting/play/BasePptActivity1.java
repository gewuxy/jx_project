package jx.doctor.ui.activity.meeting.play;

import android.support.annotation.CallSuper;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import jx.doctor.App;
import jx.doctor.R;
import jx.doctor.ui.activity.meeting.play.contract.BasePptContract1;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.ui.frag.meeting.course.VideoCourseFrag;
import lib.ys.ConstantsEx;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;

/**
 * 观看会议(录播 / PPT直播)
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
abstract public class BasePptActivity1<V extends BasePptContract1.View, P extends BasePptContract1.Presenter<V>>
        extends BasePlayActivity1<V, P>
        implements ViewPager.OnPageChangeListener {

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    private TextView mTvTitle;

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.ppt_tv_title);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mTvTitle.setText(mTitle);
    }

    abstract protected class BasePptViewImpl extends BasePlayViewImpl implements BasePptContract1.View {

        @Override
        public void portrait() {
            super.portrait();

            if (mParamP == null) {
                mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, (int) ResLoader.getDimension(R.dimen.meet_play_ppt));
                mParamP.topMargin = fit(App.NavBarVal.KHeightDp) + findView(R.id.ppt_live_layout_title).getMeasuredHeight();
            }
            mLayoutFrag.setLayoutParams(mParamP);
        }

        @Override
        public void landscape() {
            super.landscape();

            if (mParamL == null) {
                mParamL = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
            }
            mLayoutFrag.setLayoutParams(mParamL);
        }

    }
}