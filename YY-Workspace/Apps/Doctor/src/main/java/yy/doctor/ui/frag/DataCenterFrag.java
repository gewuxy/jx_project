package yy.doctor.ui.frag;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.ui.other.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.pager.indicator.PageIndicator;
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.ui.activity.search.SearchActivity;
import yy.doctor.ui.frag.data.ClinicsFrag;
import yy.doctor.ui.frag.data.DrugsFrag;
import yy.doctor.ui.frag.data.ThomsonsFrag;

/**
 * 数据
 *
 * @author CaiXiang
 * @since 2017//6
 */
public class DataCenterFrag extends BaseVPFrag {

    private static final int KIndicatorColor = Color.parseColor("#006ebd");
    private static final int KIndicatorWidth = 50;

    @IntDef({
            PageType.thomson,
            PageType.drug_list,
            PageType.clinical_guide,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface PageType {
        int thomson = 0;  //汤森路透
        int drug_list = 1;    //药品目录
        int clinical_guide = 2;   //临床指南
    }

    private ViewGroup mLayoutTab;
    private UnderlinePageIndicator mIndicator;

    private View mPreTab;
    private OnClickListener mTabListener;

    @Override
    public void initData() {

        add(new ThomsonsFrag());
        add(new DrugsFrag());
        add(new ClinicsFrag());
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_data;
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addTextViewMid(R.string.data_center);
        bar.addViewRight(R.mipmap.nav_bar_ic_search, v -> startActivity(SearchActivity.class));
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.data_layout_tab);
        mIndicator = findView(R.id.data_layout_indicator);
    }

    @Override
    public void setViews() {
        super.setViews();

        addTabs();
        setOffscreenPageLimit(getCount());

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setPreTab(mLayoutTab.getChildAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    protected PageIndicator initPageIndicator() {
        mIndicator.setFades(false);
        mIndicator.setLineWidth(fitDp(KIndicatorWidth));
        mIndicator.setSelectedColor(KIndicatorColor);
        return mIndicator;
    }

    private void addTabs() {

        addTab(PageType.thomson, R.string.thomson);
        addTab(PageType.drug_list, R.string.drug_list);
        addTab(PageType.clinical_guide, R.string.clinical_guide);

        invalidate();
    }

    private void addTab(final int index, @StringRes int id) {

        View v = inflate(R.layout.layout_meeting_tab);

        TextView tv = (TextView) v.findViewById(R.id.meeting_tab_tv);
        tv.setText(getString(id));
        v.setTag(index);

        if (mTabListener == null) {
            mTabListener = v1 -> {
                setPreTab(v1);
                setCurrentItem((Integer) v1.getTag());
            };
        }

        if (index == PageType.thomson) {
            setPreTab(v);
        }

        v.setOnClickListener(mTabListener);

        fit(v);

        LinearLayout.LayoutParams p = LayoutUtil.getLinearParams(MATCH_PARENT, MATCH_PARENT);
        p.weight = 1;
        mLayoutTab.addView(v, p);
    }

    // 设置tab被选中
    private void setPreTab(View v) {
        if (mPreTab != null) {
            mPreTab.setSelected(false);
        }

        mPreTab = v;
        mPreTab.setSelected(true);
    }

    /*@Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }*/

    @Override
    protected boolean useLazyLoad() {
        return true;
    }
}
