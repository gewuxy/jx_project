package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import lib.ys.ex.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.DataFrag;
import yy.doctor.frag.HomeFrag;
import yy.doctor.frag.MeFrag;
import yy.doctor.frag.MeetingFrag;

public class MainActivity extends BaseVPActivity {

    public static void nav(Context context) {
        nav(context, KTabHome);
    }

    public static void nav(Context context, int page) {
        LaunchUtil.startActivity(context, newIntent(context, page));
    }

    public static Intent newIntent(Context context, int page) {
        return new Intent(context, MainActivity.class)
                .putExtra(Extra.KPage, page);
    }

    public static final int KTabHome = 0;
    public static final int KTabMeeting = 1;
    public static final int KTabData = 2;
    public static final int KTabMe = 3;

    private LinearLayout mLayoutTab;
    private View mTabPrev;

    private int mCurrPage;

    @Override
    public void initData() {
        mCurrPage = getIntent().getIntExtra(Extra.KPage, KTabHome);

        add(new HomeFrag());
        add(new MeetingFrag());
        add(new DataFrag());
        add(new MeFrag());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.main_layout_tab);
    }

    @Override
    public void setViews() {
        super.setViews();

        addIndicators();

        setOffscreenPageLimit(getCount());
        setScrollable(false);

        setCurrentItem(mCurrPage);
    }

    private void addIndicators() {

        addIndicator(KTabHome, R.drawable.main_selector_home, "首页");
        addIndicator(KTabMeeting, R.drawable.main_selector_meeting, "会议");
        addIndicator(KTabData, R.drawable.main_selector_data, "数据");
        addIndicator(KTabMe, R.drawable.main_selector_me, "我");

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mTabPrev != null) {
                    mTabPrev.setSelected(false);
                }
                mTabPrev = mLayoutTab.getChildAt(position);
                mTabPrev.setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void addIndicator(final int index, @DrawableRes int drawableId, CharSequence text) {
        View v = inflate(R.layout.layout_main_tab);

        ImageView iv = (ImageView) v.findViewById(R.id.main_tab_iv);
        iv.setImageResource(drawableId);

        TextView tv = (TextView) v.findViewById(R.id.main_tab_tv);
        tv.setText(text);

        v.setOnClickListener(v1 -> setCurrentItem(index, false));

        if (index == KTabHome) {
            v.setSelected(true);
            mTabPrev = v;
        }

        fit(v);

        LayoutParams p = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        p.weight = 1;
        mLayoutTab.addView(v, p);
    }

    @Override
    protected Boolean enableSwipeFinish() {
        return false;
    }
}
