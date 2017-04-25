package yy.doctor.frag;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import lib.ys.LogMgr;
import lib.ys.ex.NavBar;
import lib.ys.util.UIUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.view.DividerGridItemDecoration;
import lib.ys.view.DividerGridItemDecoration.Decoration;
import lib.ys.view.pager.indicator.PageIndicator;
import lib.ys.view.pager.indicator.UnderlinePageIndicator;
import lib.yy.frag.base.BaseVPFrag;
import yy.doctor.R;
import yy.doctor.adapter.SectionAdapter;
import yy.doctor.adapter.SectionAdapter.OnItemClickListener;
import yy.doctor.adapter.SectionAdapter.ViewHolder;
import yy.doctor.frag.meeting.ProgressingMeetingsFrag;
import yy.doctor.frag.meeting.ReviewMeetingsFrag;
import yy.doctor.frag.meeting.WaitMeetingsFrag;

/**
 * 会议界面
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeetingFrag extends BaseVPFrag {

    private static final int KPopupWindowBackground = R.color.main_text_bg;
    private static final int KIndicatorColor = Color.parseColor("#006ebd");
    private static final int KIndicatorWidth = 30;//滑块的宽度
    private static final int KGridViewRowCount = 3;//列数
    private static final long KDuration = 100l;//切换科室动画时长
    private static final float KDividerHeight = 14.0f;//分割线高度

    //科室选择
    private ImageView mIvSection;
    private TextView mTvSection;

    //PopupWindow
    private View mSections;//内容
    private PopupWindow mPopupWindow;
    private Drawable mPopupWindowBackground;//背景

    //选择科室前的动画,箭头向上
    private Animation mAnimDown;
    //选择科室后的动画,箭头向下
    private Animation mAnimUp;

    private ViewGroup mLayoutTab;
    private UnderlinePageIndicator mIndicator;

    private View mPreTab;
    private OnClickListener mTabListener;


    @IntDef({
            PageType.progressing,
            PageType.wait,
            PageType.review,
    })
    private @interface PageType {
        int progressing = 0;//进行中
        int wait = 1;//未开始
        int review = 2;//精彩回顾
    }

    @Override
    public void initData() {
        mPopupWindowBackground = ContextCompat.getDrawable(getContext(), KPopupWindowBackground);

        mAnimUp = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimUp.setDuration(KDuration);
        mAnimUp.setFillAfter(true);

        mAnimDown = new RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimDown.setDuration(KDuration);
        mAnimDown.setFillAfter(true);

        add(new ProgressingMeetingsFrag());
        add(new WaitMeetingsFrag());
        add(new ReviewMeetingsFrag());
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_meeting;
    }

    @Override
    public void initNavBar(final NavBar bar) {
        View v = inflate(R.layout.layout_meeting_nav_bar_mid);
        mIvSection = (ImageView) v.findViewById(R.id.meeting_nav_bar_mid_iv);
        mTvSection = (TextView) v.findViewById(R.id.meeting_nav_bar_mid_tv);
        bar.addViewMid(v, new OnClickListener() {

            @Override
            public void onClick(View v) {
                mIvSection.startAnimation(mAnimUp);
                showSection(bar);
            }
        });

        bar.addViewRight(R.mipmap.nav_bar_ic_search, new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 跳转搜索界面
                LogMgr.e(TAG, "搜索");
            }
        });
    }

    /**
     * 显示选择科室
     */
    private void showSection(View parent) {
        if (mSections == null) {
            mSections = inflate(R.layout.layout_meeting_select_section);
            RecyclerView section =
                    (RecyclerView) mSections.findViewById(R.id.meeting_layout_recyclerview);
            section.setLayoutManager(new StaggeredGridLayoutManager(KGridViewRowCount,
                    StaggeredGridLayoutManager.VERTICAL));
            section.addItemDecoration(new DividerGridItemDecoration(getContext(),
                    Decoration.vertical, UIUtil.dpToPx(KDividerHeight, getContext()), R.drawable.divider_bg));
            section.setAdapter(new SectionAdapter(new OnItemClickListener() {
                @Override
                public void onItemClick(ViewHolder viewHolder) {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                        mTvSection.setText(viewHolder.getText());
                    }
                }
            }));
            mSections.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow();
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(mPopupWindowBackground);
            mPopupWindow.setContentView(mSections);
            mPopupWindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    mIvSection.startAnimation(mAnimDown);
                }
            });
        }
        mPopupWindow.showAsDropDown(parent, 0, 0);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.meeting_layout_tab);
        mIndicator = findView(R.id.meeting_layout_indicator);
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
        addTab(PageType.progressing, "进行中");
        addTab(PageType.wait, "未开始");
        addTab(PageType.review, "精彩回顾");

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

        if (index == PageType.progressing) {
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

        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }
}
