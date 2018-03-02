package jx.doctor.ui.activity.meeting.play;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import jx.doctor.R;
import jx.doctor.ui.activity.meeting.play.contract.BasePlayContract;
import jx.doctor.util.Util;
import lib.jx.contract.IContract;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.opt.ICommonOpt;
import lib.ys.ui.other.NavBar;

/**
 * @auther : GuoXuan
 * @since : 2017/9/25
 */

abstract public class BasePlayActivity<V extends BasePlayContract.View, P extends IContract.Presenter<V>> extends BaseActivity {

    @Arg(opt = true)
    String mUnitNum; // 单位号

    @Arg(opt = true)
    String mTitle; // 标题

    @Arg
    String mMeetId; // 会议ID

    @Arg
    String mModuleId; // 模块ID

    private ImageView mIvControlL; // nav bar 右上角的按钮
    private TextView mNavBarMid;

    // 底部按钮
    private TextView mTvComment;
    private ImageView mIvControlP;
    private TextView mTvAll;
    private TextView mTvCur;

    private V mV;
    private P mP;

    protected V getV() {
        if (mV == null) {
            mV = createV();
        }
        return mV;
    }

    protected P getP() {
        if (mP == null) {
            mP = createP(getV());
        }
        return mP;
    }

    protected ImageView getNavBarControl() {
        return mIvControlL;
    }

    @CallSuper
    @Override
    public void initData() {
        notify(NotifyType.study_start);

        mV = getV();
        mP = getP();
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> onBackPressed());

        mNavBarMid = bar.addTextViewMid(mUnitNum);
        if (mV.getNavBarLandscape()) {
            ViewGroup view = bar.addViewRight(R.drawable.play_audio_selector, v -> mIvControlP.performClick());
            mIvControlL = Util.getBarView(view, ImageView.class);
        }
    }

    @CallSuper
    @Override
    public void findViews() {
        mTvComment = findView(R.id.play_nav_tv_comment);
        mIvControlP = findView(R.id.play_nav_iv_control);

        mTvCur = findView(R.id.play_tv_current);
        mTvAll = findView(R.id.play_tv_all);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(R.id.play_nav_iv_comment);
        setOnClickListener(R.id.play_nav_iv_control);
        setOnClickListener(R.id.play_nav_iv_landscape);

//        setOnClickListener(R.id.play_iv_left);
//        setOnClickListener(R.id.play_iv_right);

//        mIvControlP.setImageResource(mV.getControlResId());

        addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (orientationLandscape()) {
                    setNavBarL();
                }
                removeOnGlobalLayoutListener(this);
            }

        });
    }

    protected void setNavBarL() {
        getNavBar().setBackgroundColor(Color.BLACK);
        getNavBar().setBackgroundAlpha(127);
    }

    protected void setNavBarP() {
        getNavBar().setBackgroundResource(R.color.app_nav_bar_bg);
        getNavBar().setBackgroundAlpha(255);
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_nav_iv_comment: {
                CommentActivityRouter.create(mMeetId).route(this);
            }
            break;
            case R.id.play_nav_iv_control: {
                mV.toggle();
            }
            break;
            case R.id.play_nav_iv_landscape: {
                // 切换横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mNavBarMid.setText(mTitle);
                mV.landscape();
            }
            break;
//            case R.id.play_iv_left: {
//                mV.toLeft();
//            }
//            break;
//            case R.id.play_iv_right: {
//                mV.toRight();
//            }
//            break;
            default: {
                onClick(v.getId());
            }
            break;
        }
    }

    @Override
    public final void onBackPressed() {
        if (orientationLandscape()) {
            nativePortrait();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (orientationLandscape()) {
            mV.showLandscapeView();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        notify(NotifyType.study_end);

        // 保持调用顺序
        super.onDestroy();
    }

    private void nativePortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mNavBarMid.setText(mUnitNum);
        setNavBarP();
        mV.portrait();
    }

    /**
     * 点击事件
     *
     * @param id View的id{@link View#getId()}
     */
    protected void onClick(int id) {
        // do nothing
    }

    protected void setPlayState(boolean state) {
        mIvControlP.setSelected(state);
        if (mIvControlL != null) {
            // 录播横屏没有按钮
            mIvControlL.setSelected(state);
        }
    }

    /**
     * 屏幕方向是否为横屏
     */
    protected boolean orientationLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    protected void goneView(@IdRes int resId) {
        goneView(findView(resId));
    }

    protected void showView(@IdRes int resId) {
        showView(findView(resId));
    }

    protected void setTextComment(int num) {
        //mTvComment.setText(num <= 0 ? "评论" : String.valueOf(num));
    }

    protected void setTextCur(int position) {
        mTvCur.setText(fitNumber(position));
    }

    protected void setTextAll(int size) {
        mTvAll.setText(fitNumber(size));
    }

    @NonNull
    private StringBuffer fitNumber(int number) {
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

    @NonNull
    abstract protected V createV();

    @NonNull
    abstract protected P createP(V view);

    /**
     * BaseView暂时没有extends ICommonOpt(项目框架)
     */
    protected class BaseViewImpl implements IContract.View, ICommonOpt {

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
    }

}
