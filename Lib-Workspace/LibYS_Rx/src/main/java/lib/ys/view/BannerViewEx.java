package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import lib.ys.adapter.PagerAdapterEx;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.pager.AutoScrollViewPager;
import lib.ys.view.pager.indicator.IconPageIndicator;
import lib.ys.view.pager.indicator.PageIndicator;

/**
 * 封装广告栏
 * 内部需要有{@link AutoScrollViewPager} 和 {@link PageIndicator}的布局
 *
 * @author yuansui
 */
abstract public class BannerViewEx<T, A extends PagerAdapterEx> extends RelativeLayout {

    private A mAdapter;
    private AutoScrollViewPager mVp;
    private PageIndicator mIndicator;
    private int mInitCurrItem;

    public BannerViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }

        Class<A> adapterClass = GenericUtil.getClassType(getClass(), PagerAdapterEx.class);
        mAdapter = ReflectionUtil.newInst(adapterClass);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isInEditMode()) {
            return;
        }

        if (mVp != null) {
            // 不需要二次初始化
            return;
        }

        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);
            if (v instanceof AutoScrollViewPager) {
                mVp = (AutoScrollViewPager) v;
            } else if (v instanceof PageIndicator) {
                mIndicator = (PageIndicator) v;
            }
        }

        if (mVp == null) {
            throw new IllegalStateException("must have AutoScrollViewPager");
        }

        setViews();
    }

    private void setViews() {
        /**
         * 设置banner viewpager的属性
         */
        mVp.setAdapter(mAdapter);

        mVp.setInterval(getInterval());
        mVp.setScrollDuration(getDuration());

        if (mIndicator != null) {
            mIndicator.setViewPager(mVp);
            if (mIndicator instanceof IconPageIndicator) {
                ((IconPageIndicator) mIndicator).setIndicatorSpace(getIndicatorSpace());
            }

            if (getCount() > 1) {
                ViewUtil.showView((View) mIndicator);
            } else {
                ViewUtil.hideView((View) mIndicator);
            }
        }

        invalidate();

        mVp.setCurrentItem(mInitCurrItem);
        mVp.startAutoScroll();
    }

    protected long getInterval() {
        return 4000;
    }

    protected int getDuration() {
        return 600;
    }

    /**
     * Only use for{@link IconPageIndicator}
     *
     * @return px
     */
    protected int getIndicatorSpace() {
        return 0;
    }

    public void setData(List<T> ts) {
        mAdapter.setData(ts);
        if (mIndicator != null) {
            if (ts.size() > 1) {
                ViewUtil.showView((View) mIndicator);
            } else {
                ViewUtil.hideView((View) mIndicator);
            }
        }
        invalidate();
    }

    public int getCount() {
        return mAdapter.getCount();
    }

    /**
     * 刷新数据
     */
    public void invalidate() {
        mAdapter.notifyDataSetChanged();
        if (mIndicator != null) {
            mIndicator.notifyDataSetChanged();
        }
    }

    public void onPause() {
        if (mVp != null) {
            mVp.stopAutoScroll();
        }
    }

    public void onResume() {
        if (mVp != null) {
            mVp.startAutoScroll();
        }
    }

    public void initCurrentItem(int item) {
        mInitCurrItem = item;
    }

    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }

        if (mVp != null) {
            mVp.stopAutoScroll();
        }
    }

}
