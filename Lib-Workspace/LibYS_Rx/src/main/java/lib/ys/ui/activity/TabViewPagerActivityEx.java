package lib.ys.ui.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RelativeLayout;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.FragPagerAdapterEx;
import lib.ys.adapter.interfaces.IPagerTitle;
import lib.ys.fitter.DpFitter;
import lib.ys.util.res.ResLoader;
import lib.ys.view.pager.indicator.TabStripIndicator;
import lib.ys.view.scrollableLayout.ScrollableHelper.ScrollableContainer;
import lib.ys.view.scrollableLayout.ScrollableLayout;

/**
 * @auther yuansui
 * @since 2017/7/5
 */

abstract public class TabViewPagerActivityEx<TITLE extends IPagerTitle> extends ViewPagerActivityEx {

    private TabStripIndicator mIndicator;
    private ScrollableLayout mLayoutScrollable;
    private RelativeLayout mLayoutHeader;

    private OnPageChangeListener mListener;

    @Override
    public int getContentViewId() {
        return R.layout.layout_viewpager_tab;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mLayoutHeader = findView(R.id.vp_tab_layout_header);
        mIndicator = findView(R.id.vp_tab_indicator);
        mLayoutScrollable = findView(R.id.vp_tab_layout_scrollable);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        getIndicatorSetter().set(mIndicator);
        mIndicator.setViewPager(getViewPager());

        super.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentScrollableContainer(position);

                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });

        if (!getPagerAdapter().isEmpty()) {
            setCurrentScrollableContainer(0);
        }
    }

    public class FragPagerAdapter extends FragPagerAdapterEx<Fragment, TITLE> {

        public FragPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    }

    @NonNull
    @Override
    protected final FragPagerAdapterEx createPagerAdapter() {
        return new FragPagerAdapter(getSupportFragmentManager());
    }

    @Override
    protected void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    protected void setTitles(List<TITLE> titles) {
        getPagerAdapter().setTitles(titles);
    }

    @CallSuper
    @Override
    protected void setCurrentItem(int item) {
        super.setCurrentItem(item);

        setCurrentScrollableContainer(item);
    }

    @CallSuper
    @Override
    protected void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);

        setCurrentScrollableContainer(item);
    }

    private void setCurrentScrollableContainer(int item) {
        Fragment frag = getItem(item);
        if (frag instanceof ScrollableContainer && mLayoutScrollable != null) {
            mLayoutScrollable.getHelper().setCurrentScrollableContainer((ScrollableContainer) frag);
        }
    }

    @Override
    protected final void invalidate() {
        super.invalidate();
        if (mIndicator != null) {
            mIndicator.notifyDataSetChanged();
        }
    }

    /**
     * 简易builder
     */
    public static class IndicatorSetter {
//            <style name="v.tab_strip_indicator">
//        <item name="android:layout_width">wrap_content</item>
//        <item name="android:layout_height">41dp</item>
//        <item name="spiIndicatorColor">#f5f5f5</item>
//        <item name="spiIndicatorHeight">2dp</item>
//        <item name="spiTabPaddingLeftRight">10dp</item>
//        <item name="colorTabTextDefault">@color/black</item>
//        <item name="colorTabTextSelected">@color/blue</item>
//        <item name="spiTabTextSize">16dp</item>
//        <item name="android:layout_gravity">center_vertical</item>
//    </style>


        @ColorInt
        private int mTextColorNormal;

        @ColorInt
        private int mTextColorFocus;

        private int mPaddingDp;
        private int mTextSizeDp;

        @ColorRes
        private int mBgColorRes = 0;

        @ColorInt
        private int mBgColor = 0;


        public IndicatorSetter bgColor(@ColorInt int color) {
            mBgColor = color;
            return this;
        }

        public IndicatorSetter bgColorRes(@ColorRes int res) {
            mBgColorRes = res;
            return this;
        }

        public IndicatorSetter textColorNormal(@ColorInt int color) {
            mTextColorNormal = color;
            return this;
        }

        public IndicatorSetter textColorNormalRes(@ColorRes int res) {
            mTextColorNormal = ResLoader.getColor(res);
            return this;
        }

        public IndicatorSetter textColorFocus(@ColorInt int color) {
            mTextColorFocus = color;
            return this;
        }

        public IndicatorSetter textColorFocusRes(@ColorRes int res) {
            mTextColorFocus = ResLoader.getColor(res);
            return this;
        }

        public IndicatorSetter paddingDp(int dp) {
            mPaddingDp = dp;
            return this;
        }

        public IndicatorSetter textSizeDp(int dp) {
            mTextSizeDp = dp;
            return this;
        }

        public void set(@NonNull TabStripIndicator indicator) {
            if (mTextColorFocus != 0) {
                indicator.setTextColorFocus(mTextColorFocus);
            }

            if (mTextColorNormal != 0) {
                indicator.setTextColorNormal(mTextColorNormal);
            }

            if (mTextSizeDp != 0) {
                indicator.setTextSize(DpFitter.dp(mTextSizeDp));
            }

            if (mBgColorRes != 0) {
                indicator.setBackgroundResource(mBgColorRes);
            }

            if (mBgColor != 0) {
                indicator.setBackgroundColor(mBgColor);
            }

            if (mPaddingDp != 0) {
                indicator.setTabPaddingLeftRight(DpFitter.dp(mPaddingDp));
            }
        }
    }

    @NonNull
    protected IndicatorSetter getIndicatorSetter() {
        return new IndicatorSetter();
    }
}
