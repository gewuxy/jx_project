package jx.csp.ui.activity.me;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.main.Meet;
import jx.csp.util.ScaleTransformer;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.ys.ui.other.NavBar;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

@Route
public class GreenHandsGuideActivity extends BaseVpActivity {

    private final int KBreathIntervalTime = 1500; // 设置呼吸灯时间间隔
    protected final int KOne = 1;
    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KVpScale = 0.038f; // vp的缩放比例

    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvTime;
    private ImageView mIvAlpha;
    private ImageView mIvState;

    private Meet mShareArg;

    private AlphaAnimation mAnimationFadeIn;
    private AlphaAnimation mAnimationFadeOut;
    private ScaleTransformer mTransformer;

    @Arg
    String mCourseId;  // 课程id

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_base_record_nav_bar);
        mTvCurrentPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_current_page);
        mTvTotalPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_total_page);
        bar.addViewMid(view);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
//            if (mShareArg != null) {
//                ShareDialog dialog = new ShareDialog(this, mShareArg);
//                dialog.show();
//            }
        });
        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTime = findView(R.id.green_hands_guide_tv_time);
        mIvState = findView(R.id.green_hands_guide_iv_state);
        mIvState = findView(R.id.green_hands_guide_iv_state_alpha);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrPosition() + KOne));

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mTransformer = new ScaleTransformer(KVpScale, Util.calcVpOffset(getViewPager().getPaddingLeft(), getViewPager().getWidth()));
                setPageTransformer(false, mTransformer);
                removeOnGlobalLayoutListener(this);
            }
        });

        mAnimationFadeIn = new AlphaAnimation(0.2f, 1.0f);
        mAnimationFadeIn.setDuration(KBreathIntervalTime);
        mAnimationFadeIn.setFillAfter(false);  //动画结束后不保持状态
        mAnimationFadeOut = new AlphaAnimation(1.0f, 0.2f);
        mAnimationFadeOut.setDuration(KBreathIntervalTime);
        mAnimationFadeOut.setFillAfter(false);
        mAnimationFadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });
        mAnimationFadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });
    }
}
