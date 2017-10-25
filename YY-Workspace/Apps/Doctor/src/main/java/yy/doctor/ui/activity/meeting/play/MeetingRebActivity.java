package yy.doctor.ui.activity.meeting.play;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingRebLAdapter;
import yy.doctor.adapter.meeting.MeetingRebPAdapter;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
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
    private RecyclerView mRvL;

    private MeetingRebContract.Presenter mPresenter;
    private MeetingRebContract.View mView;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    private Handler mHandler;

    @Override
    public void initData() {
        notify(NotifyType.study_start);

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
        mRvL = findView(R.id.meet_ppt_rv_l);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.meet_play_iv_left);
        setOnClickListener(R.id.meet_play_iv_right);
        setOnClickListener(R.id.meet_play_iv_left_l);
        setOnClickListener(R.id.meet_play_iv_right_l);

        goneView(R.id.meet_play_layout_online);

        refresh(RefreshWay.embed);
        mPresenter.getDataFromNet(mMeetId, mModuleId);

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
                if (orientationLandscape()) {
                    mRvL.smoothScrollToPosition(position);
                } else {
                    setTextCur(position + 1);
                    mRvP.smoothScrollToPosition(position);
                }
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
        mRvP.smoothScrollToPosition(mFragReb.getCurrentItem());
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
        mRvL.smoothScrollToPosition(mFragReb.getCurrentItem());
        mPresenter.landscapeScreen();
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
            mPresenter.getDataFromNet(mMeetId, mModuleId);
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
        notify(NotifyType.study_end);

        // 保持调用顺序
        super.onDestroy();

        mPresenter.onDestroy();
    }

    private void showLandscapeView() {
        mPresenter.starCount();
        showView(getNavBar());
        showView(R.id.meet_play_iv_left_l);
        showView(R.id.meet_play_iv_right_l);
        showView(R.id.meet_ppt_rv_l);
    }

    private class MeetingRebViewImpl implements MeetingRebContract.View {

        @Override
        public void showToast(String content) {
            if (getViewState() == ViewState.loading) {
                // 网络成功服务器错误回调
                setViewState(ViewState.normal);
            }
            MeetingRebActivity.this.showToast(content);
        }

        @Override
        public void portraitInit(PPT ppt, List<Course> courses) {
            // 初始显示
            setViewState(ViewState.normal);
            mFragReb.setPPT(ppt);
            mFragReb.addCourses();
            setTextComment(ppt.getInt(TPPT.count));

            MeetingRebPAdapter adapter = new MeetingRebPAdapter();
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
            goneView(getNavBar());
        }

        @Override
        public void landscapeInit(List<Course> courses) {
            // 只有网络成功才会有横屏情况
            if (mRvL.getAdapter() != null) {
                return;
            }

            LinearLayoutManager layout = new LinearLayoutManager(MeetingRebActivity.this);
            layout.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRvL.setLayoutManager(layout);
            MeetingRebLAdapter adapter = new MeetingRebLAdapter();
            adapter.setData(courses);
            adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

                @Override
                public void onItemClick(View v, int position) {
                    mFragReb.setCurrentItem(position);
                }

            });
            mRvL.setAdapter(adapter);
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
            if (p != null) {
                p.invalidate(position);
            }
            RecyclerAdapterEx l = (RecyclerAdapterEx) mRvL.getAdapter();
            if (l != null) {
                l.invalidate(position);
            }
        }

        @Override
        public void setNextItem() {
            toRight();
        }

        @Override
        public void finishCount() {
            goneView(getNavBar());
            goneView(R.id.meet_play_iv_left_l);
            goneView(R.id.meet_play_iv_right_l);
            goneView(R.id.meet_ppt_rv_l);
        }

    }
}