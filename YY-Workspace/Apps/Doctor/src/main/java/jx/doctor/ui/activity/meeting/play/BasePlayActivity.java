package jx.doctor.ui.activity.meeting.play;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Arg;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.BasePlayContract;
import jx.doctor.ui.frag.meeting.PPTRebFrag;
import jx.doctor.ui.frag.meeting.course.BaseCourseFrag;
import jx.doctor.ui.frag.meeting.course.VideoCourseFrag;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.ys.ui.other.NavBar;

/**
 * @auther : GuoXuan
 * @since : 2017/9/25
 */
abstract public class BasePlayActivity<V extends BasePlayContract.View, P extends BasePlayContract.Presenter<V>>
        extends BaseActivity
        implements ViewPager.OnPageChangeListener {

    private final int KCodeReq = 100;

    @Arg(opt = true)
    String mUnitNum; // 单位号

    @Arg(opt = true)
    String mTitle; // 标题

    @Arg
    String mMeetId; // 会议ID

    @Arg
    String mModuleId; // 模块ID

    private TextView mNavBarMid; // 标题

    private View mLayoutPortrait;

    // 底部按钮
    private ImageView mIvComment;
    protected ImageView mIvControl;
    private ImageView mIvLandscape;

    private TextView mTvComment;
    protected TextView mTvAll;
    private TextView mTvCur;

    protected View mLayoutFrag;
    protected PPTRebFrag mFragPpt;

    protected V mV;
    protected P mP;
    private int mNum; // 评论数

    @CallSuper
    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mV = createV();
        mP = createP(mV);
        mNum = 0;
        mP.setData(mMeetId, mModuleId);
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> onBackPressed());

        mNavBarMid = bar.addTextViewMid(mUnitNum);
    }

    @CallSuper
    @Override
    public void findViews() {
        mLayoutPortrait = findView(R.id.play_layout_portrait);
        mIvComment = findView(R.id.play_nav_iv_comment);
        mIvControl = findView(R.id.play_nav_iv_control);
        mIvLandscape = findView(R.id.play_nav_iv_landscape);
        mTvComment = findView(R.id.play_nav_tv_comment);
        mTvCur = findView(R.id.play_tv_current);
        mTvAll = findView(R.id.play_tv_all);
        mLayoutFrag = findView(R.id.play_layout_frag_ppt);
        mFragPpt = findFragment(R.id.play_frag_ppt);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(mIvComment);
        setOnClickListener(mIvControl);
        setOnClickListener(mIvLandscape);
        setOnClickListener(mLayoutFrag);

        mFragPpt.addOnPageChangeListener(this);
        mFragPpt.setFragClickListener(() -> mV.landscapeIntercept());

        refresh(AppConfig.RefreshWay.embed);
        mP.getDataFromNet();
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed);
            mP.getDataFromNet();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_nav_iv_comment: {
                CommentActivityRouter.create(mMeetId).num(mNum).route(this, KCodeReq);
            }
            break;
            case R.id.play_nav_iv_control: {
                mP.toggle(mFragPpt.getCurrPosition());
            }
            break;
            case R.id.play_nav_iv_landscape: {
                // 切换横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mNavBarMid.setText(mTitle);
                mV.landscape();
            }
            break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @CallSuper
    @Override
    public void onPageSelected(int position) {
        mTvCur.setText(fitNumber(position + 1));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }

    @Override
    public final void onBackPressed() {
        if (orientation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mNavBarMid.setText(mUnitNum);
            setBarPortrait();
            mV.portrait();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mP.stopMedia();
    }

    @Override
    protected void onDestroy() {
        notify(NotifyType.study_end);

        // 保持调用顺序
        super.onDestroy();

        mP.onDestroy();
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.comment_num) {
            mNum = (int) data;
            setComment();
        }
    }

    /**
     * NavBar竖屏设置
     */
    protected void setBarPortrait() {
        getNavBar().setBackgroundResource(R.color.app_nav_bar_bg);
        getNavBar().setBackgroundAlpha(255);
    }

    /**
     * 屏幕方向
     *
     * @return true 横屏 , false 竖屏
     */
    protected boolean orientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 修正页码
     *
     * @param number 页面
     * @return 形如XXX(001, 010, 100)
     */
    @NonNull
    protected StringBuffer fitNumber(int number) {
        StringBuffer position = new StringBuffer();
        if (number < 10) {
            position.append("00").append(number);
        } else if (number < 100) {
            position.append("0").append(number);
        } else {
            position.append(number);
        }
        return position;
    }

    /**
     * 计时准备
     */
    protected void countStart() {
        mP.startCount();
        showView(getNavBar());
        mFragPpt.landscapeVisibility(true);
    }

    private void setComment() {
        if (mNum <= 0) {
            goneView(mTvComment);
        } else if (mNum < 1000) {
            mTvComment.setText(String.valueOf(mNum));
        } else {
            mTvComment.setText("999+");
        }
    }

    @NonNull
    abstract protected V createV();

    @NonNull
    abstract protected P createP(V view);

    /**
     * BaseView暂时没有extends ICommonOpt(项目框架)
     */
    abstract protected class BasePlayViewImpl implements BasePlayContract.View, ICommonOpt {

        @Override
        public void showView(View v) {
            BasePlayActivity.this.showView(v);
        }

        @Override
        public void hideView(View v) {
            BasePlayActivity.this.hideView(v);
        }

        @Override
        public void goneView(View v) {
            BasePlayActivity.this.goneView(v);
        }

        @Override
        public void startActivity(Class<?> clz) {
            BasePlayActivity.this.startActivity(clz);
        }

        @Override
        public void startActivity(Intent intent) {
            BasePlayActivity.this.startActivity(intent);
        }

        @Override
        public void startActivityForResult(Class<?> clz, int requestCode) {
            BasePlayActivity.this.startActivityForResult(clz, requestCode);
        }

        @Override
        public void showToast(String content) {
            BasePlayActivity.this.showToast(content);
        }

        @Override
        public void showToast(int... resId) {
            BasePlayActivity.this.showToast(resId);
        }

        @Override
        public void onStopRefresh() {
            BasePlayActivity.this.stopRefresh();
        }

        @Override
        public void setViewState(@ViewState int state) {
            BasePlayActivity.this.setViewState(state);
        }

        @CallSuper
        @Override
        public void onNetworkSuccess(PPT ppt) {
            setViewState(ViewState.normal);
            if (ppt != null) {
                CourseInfo courseInfo = ppt.get(PPT.TPPT.course);
                if (courseInfo != null) {
                    List<Course> courses = courseInfo.getList(CourseInfo.TCourseInfo.details);
                    if (courses != null) {
                        mTvAll.setText(fitNumber(courses.size()));
                        mTvCur.setText(fitNumber(1));
                    }
                }
                mNum = ppt.getInt(PPT.TPPT.count);
                setComment();
                mFragPpt.setPPT(ppt);
                mFragPpt.addCourses();
                onPlayState(true);
            }
        }

        @CallSuper
        @Override
        public void portrait() {
            countStart();
            setBarPortrait();
            showView(mLayoutPortrait);
        }

        @CallSuper
        @Override
        public void landscape() {
            countStart();
            getNavBar().setBackgroundColor(Color.BLACK);
            getNavBar().setBackgroundAlpha(127);
            goneView(mLayoutPortrait);
        }

        @CallSuper
        @Override
        public void landscapeIntercept() {
            if (mFragPpt.landscapeVisibility() == View.VISIBLE) {
                countFinish();
            } else {
                countStart();
                showView(getNavBar());
            }
        }

        @CallSuper
        @Override
        public void countFinish() {
            if (orientation()) {
                goneView(getNavBar());
            }
            mFragPpt.landscapeVisibility(false);
        }

        @Override
        public void onPlayState(boolean state) {
            mIvControl.setSelected(state);
        }

        @Override
        public void setNextItem() {
            mFragPpt.offsetPosition(1, ConstantsEx.KEmpty);
        }

        @Override
        public void setLiveVideo() {
            BaseCourseFrag f = mFragPpt.getItem(mFragPpt.getCurrPosition());
            if (f instanceof VideoCourseFrag) {
                VideoCourseFrag item = (VideoCourseFrag) f;
                mP.setLiveMedia(item.getTextureView());
            }
        }
    }

}
