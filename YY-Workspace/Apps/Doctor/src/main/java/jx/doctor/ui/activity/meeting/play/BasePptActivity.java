package jx.doctor.ui.activity.meeting.play;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import jx.doctor.R;
import jx.doctor.adapter.meeting.PptBreviaryAdapter;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.ui.activity.meeting.play.contract.BasePptContract;
import jx.doctor.ui.frag.meeting.PPTRebFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.ui.frag.meeting.course.VideoCourseFrag;
import jx.doctor.view.discretescrollview.DiscreteScrollView;
import jx.doctor.view.discretescrollview.ScaleTransformer;

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

    private DiscreteScrollView mRv;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    public PPTRebFrag getFragPpt() {
        return mFragReb;
    }

    public DiscreteScrollView getRv() {
        return mRv;
    }

    @Override
    public final int getContentViewId() {
        return R.layout.activity_ppt;
    }

    @Override
    public final void findViews() {
        super.findViews();

        mFragReb = findFragment(R.id.ppt_frag_ppt);
        mLayoutFrag = findView(R.id.ppt_layout_ppt);

        mRv = findView(R.id.ppt_rv);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        getP().exeNetworkReq(MeetAPI.toCourse(mMeetId, mModuleId).build());

        mFragReb.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        NetworkImageView.clearMemoryCache(BasePptActivity.this);

        setTextCur(position + 1);
        mRv.smoothScrollToPosition(position);
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
            mFragReb.landscapeVisibility(false);
            BasePptActivity.this.showView(R.id.ppt_layout_p);

            if (mParamP == null) {
                mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, (int) ResLoader.getDimension(R.dimen.meet_play_ppt));
            }
            mLayoutFrag.setLayoutParams(mParamP);
            getV().finishCount();
        }

        @Override
        public void landscape() {
            mFragReb.landscapeVisibility(true);
            BasePptActivity.this.goneView(R.id.ppt_layout_p);

            if (mParamL == null) {
                mParamL = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
            }
            mLayoutFrag.setLayoutParams(mParamL);
            showLandscapeView();
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
            CourseInfo courseInfo = ppt.get(TPPT.course);
            setTextTitle(courseInfo.getString(TCourseInfo.title));

            PptBreviaryAdapter adapter = new PptBreviaryAdapter();
            adapter.setData(courses);
            adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    if (position == mFragReb.getCurrPosition()) {
                        getP().playMedia(position);
                    } else {
                        mFragReb.setCurrPosition(position);
                    }
                }

            });
            mRv.setAdapter(adapter);
            mRv.setSlideOnFling(true);
            mRv.setItemTransitionTimeMillis(150);
            mRv.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());

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
        public void invalidate(int position) {
            RecyclerAdapterEx p = (RecyclerAdapterEx) mRv.getAdapter();
            p.invalidate(position);
        }

        @Override
        public void setNextItem() {
            mFragReb.offsetPosition(1, ConstantsEx.KEmpty);
        }

        @Override
        public void finishCount() {
            BasePptActivity.this.goneView(getNavBar());
            mFragReb.landscapeVisibility(false);
        }

        @Override
        public void setTextOnline(int onlineNum) {
            BasePptActivity.this.setTextOnline(onlineNum);
        }

    }
}