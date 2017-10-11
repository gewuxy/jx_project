package yy.doctor.ui.activity.meeting.play;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

import inject.annotation.router.Route;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.view.LayoutUtil;
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
 *
 * @auther : GuoXuan
 * @since : 2017/4/24
 */
@Route
public class MeetingRepActivity extends BaseMeetingPlayActivity implements MeetingRepContract.View {

    private View mLayoutFrag;
    private PPTRepFrag mFragRep;

    private DiscreteScrollView mRvP;
    private RecyclerView mRvL;

    private TextView mTvCurrent;
    private TextView mTvAll;

    private View mIvControl;

    private MeetingRepContract.Presenter mPresenter;

    private LayoutParams mParamP;
    private LayoutParams mParamL;

    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mPresenter = new MeetingRepPresenterImpl(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void findViews() {
        super.findViews();

        mFragRep = findFragment(R.id.meet_ppt_frag_rep);
        mLayoutFrag = findView(R.id.meet_ppt_layout);

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

        mFragRep.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                NetworkImageView.clearMemoryCache(MeetingRepActivity.this);

                mPresenter.playMedia(position);

                if (orientationLandscape()) {
                    showLandscapeView();
                    mRvL.smoothScrollToPosition(position);
                } else {
                    mTvCurrent.setText(String.valueOf(position + 1));
                    mRvP.smoothScrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }

        });

        mFragRep.setFragClickListener(() -> {
            if (orientationLandscape()) {
                showLandscapeView();
            }
        });

        mRvL.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                showLandscapeView();
            }

        });
    }

    @Override
    protected void portrait() {
        goneView(R.id.meet_ppt_layout_l);
        showView(R.id.meet_ppt_layout_p);

        if (mParamP == null) {
            mParamP = LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, fitDp(237));
        }
        mLayoutFrag.setLayoutParams(mParamP);
        finishCount();
        mRvP.smoothScrollToPosition(mFragRep.getCurrentItem());
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
        mRvL.smoothScrollToPosition(mFragRep.getCurrentItem());
        mPresenter.landscapeScreen();
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
    public void onClick(int id) {
        switch (id) {
            case R.id.meet_play_iv_left_l: {
                showLandscapeView();
            } // 横屏是显示(区别于竖屏)
            case R.id.meet_play_iv_left_p: {
                // 上一页
                setItem(mFragRep, -1, "这是第一页喔");
            }
            break;
            case R.id.meet_play_iv_right_l: {
                showLandscapeView();
            } // 横屏是显示(区别于竖屏)
            case R.id.meet_play_iv_right_p: {
                // 下一页
                setItem(mFragRep, +1, "已是最后一页");
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
    public void portraitInit(PPT ppt, List<Course> courses) {
        // 初始显示
        setViewState(ViewState.normal);
        mFragRep.setPPT(ppt);
        setCommentCount(ppt.getInt(TPPT.count));

        MeetingRepPAdapter adapter = new MeetingRepPAdapter();
        adapter.setData(courses);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                mFragRep.setCurrentItem(position);
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

        CourseInfo courseInfo = ppt.getEv(TPPT.course);
        getNavBar().addTextViewMid(courseInfo.getString(TCourseInfo.title));
        goneView(getNavBar());
    }

    @Override
    public void landscapeInit(List<Course> courses) {
        // 只有网络成功才会有横屏情况
        if (mRvL.getAdapter() != null) {
            return;
        }

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvL.setLayoutManager(layout);
        MeetingRepLAdapter adapter = new MeetingRepLAdapter();
        adapter.setData(courses);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                mFragRep.setCurrentItem(position);
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
        BaseCourseFrag f = mFragRep.getItem(mFragRep.getCurrentItem());
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
        setItem(mFragRep, +1, "已是最后一页");
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
}