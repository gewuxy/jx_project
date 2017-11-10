package yy.doctor.ui.frag;

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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.pager.indicator.PageIndicator;
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseVPFrag;
import yy.doctor.Constants.MeetStateText;
import yy.doctor.R;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.popup.SectionPopup;
import yy.doctor.popup.SectionPopup.OnSectionListener;
import yy.doctor.ui.activity.search.SearchActivity;
import yy.doctor.ui.frag.meeting.MeetsFragRouter;

/**
 * 会议界面
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeetingFrag extends BaseVPFrag implements OnPageChangeListener, OnSectionListener {

    private static final int KIndicatorColor = R.color.text_006ebd;
    private static final int KIndicatorWidth = 30; // 滑块的宽度
    private static final long KDuration = 100L; // 切换科室动画时长
    private static final String KSectionAll = "全部科室";

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

    private View mPreTab; // 上一个被选择的View
    private OnClickListener mTabListener;

    @IntDef({
            PageType.under_way,
            PageType.not_started,
            PageType.retrospect,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface PageType {
        int under_way = 0;//进行中
        int not_started = 1;//未开始
        int retrospect = 2;//精彩回顾
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        add(MeetsFragRouter.create(MeetState.under_way).route());
        add(MeetsFragRouter.create(MeetState.not_started).route());
        add(MeetsFragRouter.create(MeetState.retrospect).route());
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
        // FIXME: 目前效果是match完以后整个点击都能响应, 看需求是否要变更为只允许点击text view范围
        bar.addViewMid(v, v1 -> {
            if (mAnimUp == null) {
                // FIXME: 考虑加入到{@link AnimateUtil}里面
                mAnimUp = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                mAnimUp.setDuration(KDuration);
                mAnimUp.setFillAfter(true);
            }
            if (mAnimDown == null) {
                mAnimDown = new RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                mAnimDown.setDuration(KDuration);
                mAnimDown.setFillAfter(true);
            }
            if (mPopup == null) {
                mPopup = new SectionPopup(getContext(), this);
                mPopup.setOnDismissListener(() -> mIvSection.startAnimation(mAnimDown));
            }

            mIvSection.startAnimation(mAnimUp);
            // 显示选择科室
            mPopup.showAsDropDown(bar);
        });

        bar.addViewRight(R.drawable.nav_bar_ic_search, v1 -> startActivity(SearchActivity.class));
    }

    @Override
    public void onSectionSelected(String text) {
        mTvSection.setText(text);
        if (KSectionAll.equals(text)) {
            text = ConstantsEx.KEmpty;
        }
        notify(NotifyType.section_change, text);
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

        // 添加导航
        addTab(PageType.under_way, MeetStateText.under_way);
        addTab(PageType.not_started, MeetStateText.not_started);
        addTab(PageType.retrospect, MeetStateText.retrospect);

        invalidate();

        setOnPageChangeListener(this);

        setOffscreenPageLimit(getCount());
    }

    @Override
    protected PageIndicator initPageIndicator() {
        mIndicator.setFades(false);
        mIndicator.setLineWidth(fitDp(KIndicatorWidth));
        mIndicator.setSelectedColor(ResLoader.getColor(KIndicatorColor));
        return mIndicator;
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setPreTab(mLayoutTab.getChildAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    @Override
    protected boolean useLazyLoad() {
        return true;
    }
}
