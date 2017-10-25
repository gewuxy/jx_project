package yy.doctor.ui.activity.meeting.play;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView mIvControl;
    private TextView mTvAll;
    private TextView mTvCur;

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
        mIvControl = findView(R.id.meet_play_nav_iv_control);

        mTvCur = findView(R.id.meet_play_tv_current);
        mTvAll = findView(R.id.meet_play_tv_all);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(R.id.meet_play_nav_iv_back);
        setOnClickListener(R.id.meet_play_nav_tv_comment);
        setOnClickListener(R.id.meet_play_nav_iv_control);
        setOnClickListener(R.id.meet_play_nav_tv_online_num);
        setOnClickListener(R.id.meet_play_nav_iv_landscape);

        setOnClickListener(R.id.meet_play_iv_left);
        setOnClickListener(R.id.meet_play_iv_right);

        mIvControl.setImageResource(getControlResId());
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
            case R.id.meet_play_iv_left: {
                toLeft();
            }
            break;
            case R.id.meet_play_iv_right: {
                toRight();
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

    protected void setPlayState(boolean state) {
        mIvControl.setSelected(state);
    }

    protected void setTextComment(int num) {
        mTvComment.setText(num <= 0 ? "评论" : String.valueOf(num));
    }

    protected void setTextCur(int position) {
        mTvCur.setText(String.valueOf(position));
    }

    protected void setTextAll(int size) {
        mTvAll.setText(String.valueOf(size));
    }

    protected void setTextOnline(int people) {
        mTvOnlineNum.setText(String.valueOf(people));
    }

    /**
     * 屏幕方向是否为横屏
     */
    protected boolean orientationLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 上一页
     */
    abstract protected void toLeft();

    /**
     * 下一页
     */
    abstract protected void toRight();

    /**
     * 竖屏
     */
    abstract protected void portrait();

    /**
     * 横屏
     */
    abstract protected void landscape();

    abstract protected void toggle();

    @DrawableRes
    abstract protected int getControlResId();

}
