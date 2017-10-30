package yy.doctor.ui.activity.meeting.play;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import inject.annotation.router.Route;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.contract.BaseContract;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingBreviaryAdapter;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.NetworkApiDescriptor;
import yy.doctor.ui.frag.meeting.PPTRebFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.view.discretescrollview.DiscreteScrollView;
import yy.doctor.view.discretescrollview.ScaleTransformer;

/**
 * 观看会议(录播)
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
@Route
public class MeetingRebActivity extends BaseMeetingPlayActivity {

    private View mLayoutFrag;
    private PPTRebFrag mFragReb;

    private DiscreteScrollView mRvP;

    private MeetingRebContract.Presenter mPresenter;
    private MeetingRebContract.View mView;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    private Handler mHandler;

    @Override
    public void initData() {
        super.initData();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int position = (int) msg.obj;
                mPresenter.playMedia(position);
            }
        };
        mView = new MeetingRebViewImpl();
        mPresenter = new MeetingRebPresenterImpl(mView);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void findViews() {
        super.findViews();

        mFragReb = findFragment(R.id.meet_ppt_frag_rep);
        mLayoutFrag = findView(R.id.meet_ppt_layout);

        mRvP = findView(R.id.meet_ppt_rv_p);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.meet_play_iv_left);
        setOnClickListener(R.id.meet_play_iv_right);
        setOnClickListener(R.id.meet_play_iv_left_l);
        setOnClickListener(R.id.meet_play_iv_right_l);

        set();

        refresh(RefreshWay.embed);
        mPresenter.exeNetworkReq(NetworkApiDescriptor.MeetAPI.toCourse(mMeetId, mModuleId).build());

        mFragReb.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                NetworkImageView.clearMemoryCache(MeetingRebActivity.this);

                mHandler.removeMessages(0);
                Message message = Message.obtain();
                message.obj = position;
                message.what = 0;
                mHandler.sendMessageDelayed(message, 500);
                setTextCur(position + 1);
                mRvP.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }

        });
    }

    @Override
    protected void portrait() {
        goneView(R.id.meet_ppt_layout_l);
        showView(R.id.meet_ppt_layout_p);

        if (mParamP == null) {
            mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, (int) ResLoader.getDimension(R.dimen.meet_play_ppt));
        }
        mLayoutFrag.setLayoutParams(mParamP);
        mView.finishCount();
    }

    @Override
    protected void landscape() {
        goneView(R.id.meet_ppt_layout_p);
        showView(R.id.meet_ppt_layout_l);

        if (mParamL == null) {
            mParamL = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        }
        mLayoutFrag.setLayoutParams(mParamL);
        showLandscapeView();
    }

    @Override
    protected void toggle() {
        mPresenter.toggle();
    }

    @Override
    protected int getControlResId() {
        return R.drawable.meet_play_reb_select_control;
    }

    @Override
    public void onClick(int id) {
        switch (id) {
            case R.id.meet_play_iv_left_l: {
                toLeft();
            }
            break;
            case R.id.meet_play_iv_right_l: {
                toRight();
            }
            break;
        }
    }

    @Override
    protected void toLeft() {
        mFragReb.setCurrentItem(-1, getString(R.string.course_first));
    }

    @Override
    protected void toRight() {
        mFragReb.setCurrentItem(1, getString(R.string.course_last));
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.exeNetworkReq(NetworkApiDescriptor.MeetAPI.toCourse(mMeetId, mModuleId).build());
            ;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (orientationLandscape()) {
            showLandscapeView();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenter.stopMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.onDestroy();
    }

    private void showLandscapeView() {
        mPresenter.starCount();
        showView(getNavBar());
        showView(R.id.meet_play_iv_left_l);
        showView(R.id.meet_play_iv_right_l);
    }

    /**
     * 设置
     */
    protected void set() {
        goneView(R.id.meet_play_layout_online);
    }

    private class MeetingRebViewImpl extends BaseViewImpl implements MeetingRebContract.View {

        @Override
        public void showToast(String content) {
            if (getViewState() == ViewState.loading) {
                // 网络成功服务器错误回调
                setViewState(ViewState.normal);
            }
            super.showToast(content);
        }

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            // 初始显示
            setViewState(ViewState.normal);
            mFragReb.setPPT(ppt);
            mFragReb.addCourses();
            setTextComment(ppt.getInt(TPPT.count));

            MeetingBreviaryAdapter adapter = new MeetingBreviaryAdapter();
            adapter.setData(courses);
            adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    mFragReb.setCurrentItem(position);
                }

            });
            mRvP.setAdapter(adapter);
            mRvP.setSlideOnFling(true);
            mRvP.setItemTransitionTimeMillis(150);
            mRvP.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());

            mPresenter.playMedia(0);
            setTextAll(courses.size());
            setTextCur(1);

            CourseInfo courseInfo = ppt.get(TPPT.course);
            getNavBar().addTextViewMid(courseInfo.getString(TCourseInfo.title));
            MeetingRebActivity.this.goneView(getNavBar());
        }

        @Override
        public void onPlayState(boolean state) {
            setPlayState(state);
        }

        @Override
        public PLVideoTextureView getTextureView() {
            BaseCourseFrag f = mFragReb.getItem(mFragReb.getCurrentItem());
            if (f instanceof VideoCourseFrag) {
                VideoCourseFrag item = (VideoCourseFrag) f;
                return item.getTextureView();
            }
            return null;
        }

        @Override
        public void invalidate(int position) {
            RecyclerAdapterEx p = (RecyclerAdapterEx) mRvP.getAdapter();
            p.invalidate(position);
        }

        @Override
        public void setNextItem() {
            toRight();
        }

        @Override
        public void finishCount() {
            MeetingRebActivity.this.goneView(getNavBar());
            MeetingRebActivity.this.goneView(R.id.meet_play_iv_left_l);
            MeetingRebActivity.this.goneView(R.id.meet_play_iv_right_l);
        }

    }
}