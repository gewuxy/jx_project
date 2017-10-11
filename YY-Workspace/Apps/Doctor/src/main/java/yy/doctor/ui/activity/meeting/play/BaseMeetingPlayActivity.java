package yy.doctor.ui.activity.meeting.play;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import inject.annotation.router.Arg;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/9/25
 */

abstract public class BaseMeetingPlayActivity extends BaseActivity {

    @Arg
    String mMeetId; // 会议ID

    @Arg
    String mModuleId; // 模块ID

    private TextView mTvComment;
    private TextView mTvOnlineNum;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_meet_play_bottom_nav;
    }

    @Override
    public final void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.drawable.meet_play_ic_to_portrait, v -> {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            goneView(getNavBar());
            portrait();
        });

        bar.setBackgroundColor(Color.BLACK);
        bar.setBackgroundAlpha(127);
        goneView(bar);
    }

    @CallSuper
    @Override
    public void findViews() {
        mTvComment = findView(R.id.meet_play_nav_tv_comment);
        mTvOnlineNum = findView(R.id.meet_play_nav_tv_online_num);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(R.id.meet_play_nav_iv_back);
        setOnClickListener(R.id.meet_play_nav_tv_comment);
        setOnClickListener(R.id.meet_play_nav_iv_control);
        setOnClickListener(R.id.meet_play_nav_tv_online_num);
        setOnClickListener(R.id.meet_play_nav_iv_landscape);
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.meet_play_nav_iv_back: {
                finish();
            }
            break;
            case R.id.meet_play_nav_tv_comment: {
                MeetingCommentActivityRouter.create(mMeetId).route(this);
            }
            break;
            case R.id.meet_play_nav_iv_control: {
                toggle();
            }
            break;
            case R.id.meet_play_nav_tv_online_num: {
            }
            break;
            case R.id.meet_play_nav_iv_landscape: {
                // 切换横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                showView(getNavBar());
                landscape();
            }
            break;
            default: {
                onClick(v.getId());
            }
            break;
        }

    }

    protected void onClick(int id) {
    }

    protected void goneView(@IdRes int resId) {
        goneView(findView(resId));
    }

    protected void showView(@IdRes int resId) {
        showView(findView(resId));
    }

    protected void setCommentCount(int num) {
        if (mTvComment == null) {
            return;
        }
        if (num <= 0) {
            mTvComment.setText("评论");
        } else {
            mTvComment.setText(String.valueOf(num));
        }
    }

    /**
     * 屏幕方向是否为横屏
     */
    protected boolean orientationLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 竖屏
     */
    abstract protected void portrait();

    /**
     * 横屏
     */
    abstract protected void landscape();

    abstract protected void toggle();

}
