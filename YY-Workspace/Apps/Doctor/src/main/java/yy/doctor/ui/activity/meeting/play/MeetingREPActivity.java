package yy.doctor.ui.activity.meeting.play;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import inject.annotation.router.Route;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingRepLAdapter;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.ui.frag.meeting.PPTRepFrag;
import yy.doctor.ui.frag.meeting.course.BaseCourseFrag;
import yy.doctor.ui.frag.meeting.course.VideoCourseFrag;
import yy.doctor.adapter.meeting.MeetingRepPAdapter;
import yy.doctor.view.discretescrollview.DiscreteScrollView;
import yy.doctor.view.discretescrollview.ScaleTransformer;

/**
 * 观看会议(录播)
 * fixme: 2种布局暂时2个ViewPager(横竖屏), ViewPager是否要合一
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
@Route
public class MeetingRepActivity extends BaseMeetingPlayActivity implements MeetingRepContract.View {

    private PPTRepFrag mFragRepP;
    private PPTRepFrag mFragRepL;

    private DiscreteScrollView mRvP;
    private RecyclerView mRvL;

    private TextView mTvCurrent;
    private TextView mTvAll;

    private View mIvControl;

    private MeetingRepContract.Presenter mPresenter;

    private boolean mSwitch; // 是否切换横竖屏

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mPresenter = new MeetingRepPresenterImpl(this);
        mSwitch = false;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void findViews() {
        super.findViews();

        mFragRepP = findFragment(R.id.meet_ppt_frag_rep_p);
        mFragRepL = findFragment(R.id.meet_ppt_frag_rep_l);

        mRvP = findView(R.id.meet_ppt_rv_p);
        mRvL = findView(R.id.meet_ppt_rv_l);

        mTvCurrent = findView(R.id.meet_play_tv_current);
        mTvAll = findView(R.id.meet_play_tv_all);

        mIvControl = findView(R.id.meet_play_nav_iv_control);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.meet_play_iv_left_p);
        setOnClickListener(R.id.meet_play_iv_right_p);
        setOnClickListener(R.id.meet_play_iv_left_l);
        setOnClickListener(R.id.meet_play_iv_right_l);

        goneView(R.id.meet_play_layout_online);

        refresh(RefreshWay.embed);
        mPresenter.getDataFromNet(mMeetId, mModuleId);

        mFragRepP.addOnPageChangeListener(new RepPageChangeListener(mRvP));
        mFragRepL.addOnPageChangeListener(new RepPageChangeListener(mRvL));

        mFragRepL.setFragClickListener(() -> showLandscapeView());
    }

    @Override
    protected void portrait() {
        goneView(R.id.meet_ppt_layout_l);
        showView(R.id.meet_ppt_layout_p);

        int p = mFragRepP.getCurrentItem();
        int l = mFragRepL.getCurrentItem();
        if (p != l) {
            mSwitch = true;
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mFragRepP.setCurrentItem(l);
                    removeOnGlobalLayoutListener(this);
                }

            });
        }
        finishCount();
        mFragRepL.saveStudyTime();
    }

    @Override
    protected void landscape() {
        goneView(R.id.meet_ppt_layout_p);
        showView(R.id.meet_ppt_layout_l);

        int p = mFragRepP.getCurrentItem();
        int l = mFragRepL.getCurrentItem();
        if (p != l) {
            mSwitch = true;
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mFragRepL.setCurrentItem(p);
                    removeOnGlobalLayoutListener(this);
                }

            });
        }

        mPresenter.landscapeScreen();
        showLandscapeView();
        mFragRepP.saveStudyTime();
    }

    @Override
    protected void toggle() {
        mPresenter.toggle();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meet_play_iv_left_l: {
                // 横屏上一页
                setItem(mFragRepL, -1, "这是第一页喔");
                showLandscapeView();
            }
            break;
            case R.id.meet_play_iv_left_p: {
                // 竖屏上一页
                setItem(mFragRepP, -1, "这是第一页喔");
            }
            break;
            case R.id.meet_play_iv_right_l: {
                // 横屏下一页
                setItem(mFragRepL, +1, "已是最后一页");
                showLandscapeView();
            }
            break;
            case R.id.meet_play_iv_right_p: {
                // 竖屏下一页
                setItem(mFragRepP, +1, "已是最后一页");
            }
            break;
            default: {
                super.onClick(v);
            }
            break;
        }
    }

    private void setItem(PPTRepFrag frag, int offset, String content) {
        int position = frag.getCurrentItem() + offset;
        if (position >= 0 || position <= frag.getCount() - 1) {
            frag.setCurrentItem(position);
        } else {
            showToast(content);
        }
    }

    @Override
    public void showToast(String content) {
        if (getViewState() == ViewState.loading) {
            // 网络成功服务器错误回调
            setViewState(ViewState.normal);
        }
        super.showToast(content);
    }

    @Override
    public void portraitInit(PPT ppt) {
        setViewState(ViewState.normal);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 初始显示
                mFragRepP.setPPT(ppt);

                MeetingRepPAdapter adapter = new MeetingRepPAdapter();
                CourseInfo courseInfo = ppt.getEv(TPPT.course);
                List<Course> courses = courseInfo.getList(TCourseInfo.details);
                adapter.setData(courses);
                adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

                    @Override
                    public void onItemClick(View v, int position) {
                        mFragRepP.setCurrentItem(position);
                    }

                });
                mRvP.setAdapter(adapter);
                mRvP.setSlideOnFling(true);
                mRvP.setItemTransitionTimeMillis(150);
                mRvP.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .build());

                mPresenter.playMedia(0);
                mTvAll.setText(String.valueOf(courses.size()));
                mTvCurrent.setText("1");

                getNavBar().addTextViewMid(courseInfo.getString(TCourseInfo.title));
                goneView(getNavBar());

                removeOnGlobalLayoutListener(this);
            }

        });

    }

    @Override
    public void landscapeInit(PPT ppt) {
        // 只有网络成功才会有横屏情况
        if (mRvL.getAdapter() != null) {
            return;
        }
        mFragRepL.setPPT(ppt);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvL.setLayoutManager(layout);
        MeetingRepLAdapter adapter = new MeetingRepLAdapter();
        adapter.setData(ppt.getEv(TPPT.course).getList(TCourseInfo.details));
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                mFragRepL.setCurrentItem(position);
                showLandscapeView();
            }

        });
        mRvL.setAdapter(adapter);
    }

    @Override
    public void onPlayState(boolean state) {
        mIvControl.setSelected(state);
    }

    @Override
    public PLVideoTextureView getTextureView() {
        BaseCourseFrag f;
        if (orientationLandscape()) {
            f = mFragRepL.getItem(mFragRepL.getCurrentItem());
        } else {
            f = mFragRepP.getItem(mFragRepP.getCurrentItem());
        }
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
        PPTRepFrag frag;
        if (orientationLandscape()) {
            frag = mFragRepL;
        } else {
            frag = mFragRepP;
        }
        setItem(frag, +1, "已是最后一页");
    }

    @Override
    public void finishCount() {
        goneView(getNavBar());
        goneView(R.id.meet_play_iv_left_l);
        goneView(R.id.meet_play_iv_right_l);
        goneView(R.id.meet_ppt_rv_l);
    }

    private void showLandscapeView() {
        mPresenter.starCount();
        showView(getNavBar());
        showView(R.id.meet_play_iv_left_l);
        showView(R.id.meet_play_iv_right_l);
        showView(R.id.meet_ppt_rv_l);
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

    public class RepPageChangeListener implements ViewPager.OnPageChangeListener {

        private RecyclerView mRv;

        RepPageChangeListener(RecyclerView rv) {
            mRv = rv;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // do nothing
        }

        @Override
        public void onPageSelected(int position) {
            NetworkImageView.clearMemoryCache(MeetingRepActivity.this);

            mRv.smoothScrollToPosition(position);

            if (mSwitch) {
                mSwitch = false;
            } else {
                mPresenter.playMedia(position);
            }

            if (orientationLandscape()) {
                showLandscapeView();
            } else {
                mTvCurrent.setText(String.valueOf(position + 1));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // do nothing
        }
    }
}