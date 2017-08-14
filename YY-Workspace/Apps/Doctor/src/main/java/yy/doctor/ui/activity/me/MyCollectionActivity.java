package yy.doctor.ui.activity.me;

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
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.ui.activity.base.BaseVPActivity;
import yy.doctor.R;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.ui.frag.me.CollectionFragRouter;
import yy.doctor.ui.frag.me.MyMeetingFrag;
import yy.doctor.util.Util;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class MyCollectionActivity extends BaseVPActivity {

    @IntDef({
            PageType.meeting,
            PageType.thomson,
            PageType.drug_list,
            PageType.clinical_guide,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface PageType {
        int meeting = 0; // 会议
        int thomson = 1;  //汤森路透
        int drug_list = 2;    //药品目录
        int clinical_guide = 3;   //临床指南
    }

    private ViewGroup mLayoutTab;
    private UnderlinePageIndicator mIndicator;

    private View mPreTab;
    private OnClickListener mTabListener;

    @Override
    public void initData() {
        add(new MyMeetingFrag());
        add(CollectionFragRouter.create(DataType.thomson).route());
        add(CollectionFragRouter.create(DataType.drug).route());
        add(CollectionFragRouter.create(DataType.clinic).route());
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_my_collection;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_collection, this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mLayoutTab = findView(R.id.collection_layout_tab);
        mIndicator = findView(R.id.collection_layout_indicator);
    }

    @Override
    public void setViews() {
        super.setViews();
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

    private void addTabs() {
        addTab(PageType.meeting, R.string.meeting);
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

        if (index == PageType.meeting) {
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


}
