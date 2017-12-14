package jx.csp.ui.activity;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.sp.SpApp;
import jx.csp.ui.frag.GuideFragRouter;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;

/**
 * 新手引导页
 *
 * @author CaiXiang
 * @since 2017/12/14
 */

public class GuideActivity extends BaseVpActivity {

    private ImageView mIvBlueDot;
    private LinearLayout mLayoutGreyDot;
    private TextView mTv;

    private int[] mImgResArray;
    private float mDotWidth;

    @Override
    public void initData() {
        mImgResArray = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3, R.drawable.guide_4};
        for (int i = 0; i < mImgResArray.length; ++i) {
            add(GuideFragRouter.create(mImgResArray[i]).route());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {}

    @Override
    public int getContentViewId() {
        return R.layout.activity_guide;
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvBlueDot = findView(R.id.activity_guide_iv_blue);
        mLayoutGreyDot = findView(R.id.activity_guide_layout_dot);
        mTv = findView(R.id.activity_guide_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        // UIUtil.setFlatBar(getWindow());
        getNavBar().setBackgroundColor(ResLoader.getColor(R.color.translucent));
        setOnClickListener(R.id.activity_guide_tv);

        // 添加灰色小圆点
        for (int i = 0; i < mImgResArray.length; i++) {
            ImageView dot = new ImageView(GuideActivity.this);
            dot.setImageResource(R.drawable.guide_circle_grey);
            //设置圆点布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {  //设置每个圆点的间隔
                params.leftMargin = 8;
            }
            dot.setLayoutParams(params);
            fit(dot);
            mLayoutGreyDot.addView(dot);
        }
        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // 页面绘制结束后，计算圆点间的间距
                mDotWidth = mLayoutGreyDot.getChildAt(1).getLeft() - mLayoutGreyDot.getChildAt(0).getLeft();
                removeOnGlobalLayoutListener(this);
            }
        });

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 计算当前小蓝点的移动量
                int leftMargin = (int) (mDotWidth * positionOffset + position * mDotWidth);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIvBlueDot.getLayoutParams();
                params.leftMargin = leftMargin;
                mIvBlueDot.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mImgResArray.length - 1) {
                    showView(mTv);
                } else {
                    goneView(mTv);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        SpApp.inst().saveGuideState();
        finish();
    }
}
