package yy.doctor.frag;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.ex.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.pager.indicator.PageIndicator;
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.frag.data.ClinicalGuideFrag;
import yy.doctor.frag.data.ClinicalShareFrag;
import yy.doctor.frag.data.DrugListbFrag;
import yy.doctor.frag.data.ThomsonLibFrag;

/**
 * 数据
 *
 * @author CaiXiang
 * @since 2017//6
 */
public class DataFrag extends BaseVPFrag {

    private static final int KIndicatorColor = Color.parseColor("#006ebd");
    private static final int KIndicatorWidth = 50;

    @IntDef({
            PageType.thomson_lib,
            PageType.drug_list,
            PageType.clinical_guide,
            PageType.clinical_share,
    })
    private @interface PageType {
        int thomson_lib = 0;  //汤森路透图书馆
        int drug_list = 1;    //药品目录
        int clinical_guide = 2;   //临床指南
        int clinical_share = 3;   //临床分享
    }

    private ViewGroup mLayoutTab;
    private UnderlinePageIndicator mIndicator;

    private View mPreTab;
    private OnClickListener mTabListener;

    @Override
    public void initData() {

        add(new ThomsonLibFrag());
        add(new DrugListbFrag());
        add(new ClinicalGuideFrag());
        add(new ClinicalShareFrag());

    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_data;
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addTextViewMid("数据中心");
        bar.addViewRight(R.mipmap.nav_bar_ic_search, new OnClickListener() {

            @Override
            public void onClick(View v) {
                showToast("585");
            }
        });

    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.data_layout_tab);
        mIndicator = findView(R.id.data_layout_indicator);

    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        addTabs();

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

        addTab(PageType.thomson_lib, "汤森路透图书馆");
        addTab(PageType.drug_list, "药品目录");
        addTab(PageType.clinical_guide, "临床指南");
        addTab(PageType.clinical_share, "临床分享");

        invalidate();
    }

    private void addTab(final int index, CharSequence text) {

        View v = inflate(R.layout.layout_meeting_tab);

        TextView tv = (TextView) v.findViewById(R.id.meeting_tab_tv);
        tv.setText(text);
        v.setTag(index);

        if (mTabListener == null) {
            mTabListener = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    setPreTab(v);
                    setCurrentItem((Integer) v.getTag());
                }
            };
        }

        if (index == PageType.thomson_lib) {
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

}
