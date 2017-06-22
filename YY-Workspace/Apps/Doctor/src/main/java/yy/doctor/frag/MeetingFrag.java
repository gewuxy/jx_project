package yy.doctor.frag;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.pager.indicator.PageIndicator;
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.Notifier.NotifyType;
import lib.yy.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.activity.search.SearchActivity;
import yy.doctor.frag.meeting.NotStartedMeetingsFrag;
import yy.doctor.frag.meeting.RetrospectMeetingsFrag;
import yy.doctor.frag.meeting.UnderWayMeetingsFrag;
import yy.doctor.popup.SectionPopup;

/**
 * 会议界面
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeetingFrag extends BaseVPFrag {

    private static final int KIndicatorColor = Color.parseColor("#006ebd");
    private static final int KIndicatorWidth = 30;//滑块的宽度
    private static final long KDuration = 100L;//切换科室动画时长

    //科室选择
    private ImageView mIvSection;
    private TextView mTvSection;

    //PopupWindow
    private SectionPopup mPopup;

    //选择科室前的动画,箭头向上
    private Animation mAnimDown;
    //选择科室后的动画,箭头向下
    private Animation mAnimUp;

    private ViewGroup mLayoutTab;
    private UnderlinePageIndicator mIndicator;

    private View mPreTab;
    private OnClickListener mTabListener;

    @IntDef({
            PageType.under_way,
            PageType.not_started,
            PageType.retrospect,
    })
    private @interface PageType {
        int under_way = 0;//进行中
        int not_started = 1;//未开始
        int retrospect = 2;//精彩回顾
    }

    @Override
    public void initData() {
        mAnimUp = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimUp.setDuration(KDuration);
        mAnimUp.setFillAfter(true);

        mAnimDown = new RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimDown.setDuration(KDuration);
        mAnimDown.setFillAfter(true);

        add(new UnderWayMeetingsFrag());
        add(new NotStartedMeetingsFrag());
        add(new RetrospectMeetingsFrag());
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_meeting;
    }

    @Override
    public void initNavBar(final NavBar bar) {
        View v = inflate(R.layout.layout_meeting_nav_bar_section);
        mIvSection = (ImageView) v.findViewById(R.id.meeting_nav_bar_mid_iv);
        mTvSection = (TextView) v.findViewById(R.id.meeting_nav_bar_mid_tv);
        bar.addViewMid(v, v1 -> {
            mIvSection.startAnimation(mAnimUp);
            showSection(bar);
        });

        bar.addViewRight(R.mipmap.nav_bar_ic_search, v1 -> startActivity(SearchActivity.class));
    }

    /**
     * 显示选择科室
     */
    private void showSection(View anchor) {
        if (mPopup == null) {
            mPopup = new SectionPopup(getContext(), text -> {
                mTvSection.setText(text);
                notify(NotifyType.section_change, text);
            });
            mPopup.setOnDismissListener(() -> mIvSection.startAnimation(mAnimDown));
        }
        mPopup.showAsDropDown(anchor);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.meeting_layout_tab);
        mIndicator = findView(R.id.meeting_layout_indicator);
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

    @Override
    protected PageIndicator initPageIndicator() {
        mIndicator.setFades(false);
        mIndicator.setLineWidth(fitDp(KIndicatorWidth));
        mIndicator.setSelectedColor(KIndicatorColor);
        return mIndicator;
    }

    private void addTabs() {
        addTab(PageType.under_way, "进行中");
        addTab(PageType.not_started, "未开始");
        addTab(PageType.retrospect, "精彩回顾");

        invalidate();
    }

    private void addTab(final int index, CharSequence text) {
        View v = inflate(R.layout.layout_meeting_tab);

        TextView tv = (TextView) v.findViewById(R.id.meeting_tab_tv);
        tv.setText(text);
        v.setTag(index);

        if (mTabListener == null) {
            mTabListener = v1 -> {
                setPreTab(v1);
                setCurrentItem((Integer) v1.getTag());
            };
        }

        if (index == PageType.under_way) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPopup != null) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    @Override
    protected boolean useLazyLoad() {
        return true;
    }
}
