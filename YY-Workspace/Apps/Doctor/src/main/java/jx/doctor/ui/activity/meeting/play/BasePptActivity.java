package jx.doctor.ui.activity.meeting.play;

import android.support.annotation.CallSuper;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import jx.doctor.App;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.ui.activity.meeting.play.contract.BasePptContract;
import jx.doctor.ui.frag.meeting.PPTRebFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.ui.frag.meeting.course.VideoCourseFrag;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;

/**
 * 观看会议(录播 / PPT直播)
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
abstract public class BasePptActivity<V extends BasePptContract.View, P extends BasePptContract.Presenter<V>> extends BasePlayActivity<V, P>
        implements ViewPager.OnPageChangeListener {

    private View mLayoutFrag;
    private PPTRebFrag mFragReb;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    private TextView mTvTitle;

    public PPTRebFrag getFragPpt() {
        return mFragReb;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mFragReb = findFragment(R.id.play_frag_ppt);
//        mLayoutFrag = findView(R.id.ppt_layout_ppt);
        mTvTitle = findView(R.id.ppt_tv_title);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        getP().exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());

        mFragReb.addOnPageChangeListener(this);
        mTvTitle.setText(mTitle);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        NetworkImageView.clearMemoryCache(BasePptActivity.this);

        setTextCur(position + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getP().exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        getP().stopMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getP().onDestroy();
    }

    abstract protected class BasePptViewImpl extends BaseViewImpl implements BasePptContract.View {

        @Override
        public void portrait() {
            BasePptActivity.this.showView(R.id.play_layout_portrait);

            if (mParamP == null) {
                mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, (int) ResLoader.getDimension(R.dimen.meet_play_ppt));
                mParamP.topMargin = fit(App.NavBarVal.KHeightDp) + findView(R.id.ppt_live_layout_title).getMeasuredHeight();
            }
            mLayoutFrag.setLayoutParams(mParamP);
            mFragReb.landscapeVisibility(false);
            getP().stopCount();
            showView(getNavBar());
            setNavBarP();
        }

        @Override
        public void landscape() {
            BasePptActivity.this.goneView(R.id.play_layout_portrait);

            if (mParamL == null) {
                mParamL = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
            }
            mLayoutFrag.setLayoutParams(mParamL);
            showLandscapeView();
            setNavBarL();
        }

        @Override
        public void toggle() {
            getP().toggle(mFragReb.getCurrPosition());
        }

        @Override
        public void toLeft() {
            mFragReb.offsetPosition(-1, getString(R.string.course_first));
        }

        @Override
        public void toRight() {
            mFragReb.offsetPosition(1, getString(R.string.course_last));
        }

        @Override
        public void showLandscapeView() {
            getP().starCount();
            showView(getNavBar());
            mFragReb.landscapeVisibility(true);
        }

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            // 初始显示
            setViewState(ViewState.normal);
            mFragReb.setPPT(ppt);
            mFragReb.addCourses();

            setTextComment(ppt.getInt(TPPT.count));

            setTextAll(courses.size());
            setTextCur(1);
        }

        @Override
        public void onPlayState(boolean state) {
            setPlayState(state);
        }

        @Override
        public PLVideoTextureView getTextureView() {
            BaseCourseFrag f = mFragReb.getItem(mFragReb.getCurrPosition());
            if (f instanceof VideoCourseFrag) {
                VideoCourseFrag item = (VideoCourseFrag) f;
                return item.getTextureView();
            }
            return null;
        }

        @Override
        public void setNextItem() {
            mFragReb.offsetPosition(1, ConstantsEx.KEmpty);
        }

        @Override
        public void finishCount() {
            goneView(getNavBar());
            mFragReb.landscapeVisibility(false);
        }

    }
}