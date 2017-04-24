package yy.doctor.frag;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.LogMgr;
import lib.ys.util.view.LayoutUtil;
import lib.yy.frag.base.BaseFrag;
import yy.doctor.R;

/**
 * 会议界面
 *
 * @author CaiXiang
 * @since 2017/4/6
 */
public class MeetingFrag extends BaseFrag {

    // 会议导航栏
    private LinearLayout mLayoutTab;
    /* 科室选择 */
    private ImageView mIvSection;
    private TextView mTvSetion;
    //选择科室前的动画
    private Animation mAnimationBeforeSelectSection;
    //选择科室后的动画
    private Animation mAnimationAfterSelectSection;
    //是否选择过科室,false箭头向下,true箭头向上
    private boolean KIsSelect = false;

    private static final int KUnderWay = 0;//进行中
    private static final int KNotStarted = 1;//未开始
    private static final int KRetrospect = 2;//精彩回顾

    private int LastViewType = -1;
    private View.OnClickListener mTabListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int type = (int) view.getTag();
            if (type != LastViewType) {
                if (LastViewType != -1)
                    mLayoutTab.getChildAt(LastViewType).setSelected(false);
                view.setSelected(true);
                LastViewType = type;
            }
        }
    };

    @Override
    public void initData() {

        mAnimationAfterSelectSection = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimationAfterSelectSection.setDuration(100L);
        mAnimationAfterSelectSection.setFillAfter(true);

        mAnimationBeforeSelectSection = new RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimationBeforeSelectSection.setDuration(100L);
        mAnimationBeforeSelectSection.setFillAfter(true);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_meeting;
    }

    @Override
    public void initNavBar() {
        final View view = inflate(R.layout.layout_meeting_nav_bar);
        mIvSection = (ImageView) view.findViewById(R.id.meeting_select_section_ic);
        mTvSetion = (TextView) view.findViewById(R.id.meeting_select_section_name);
        getNavBar().addViewMid(view, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIvSection.startAnimation(KIsSelect ? mAnimationBeforeSelectSection : mAnimationAfterSelectSection);
                KIsSelect = ! KIsSelect;
            }
        });
    }

    @Override
    public void findViews() {
        mLayoutTab = findView(R.id.vp_header);
    }

    @Override
    public void setViewsValue() {
        addIndicators();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        /*--------- 防止选择科室的时候切换导致错乱 --------*/
        if (KIsSelect) {
        LogMgr.e(TAG,"onInvisible"+KIsSelect);
            mIvSection.startAnimation(mAnimationBeforeSelectSection);
            KIsSelect = !KIsSelect;
        }
    }

    private void addIndicators() {

        addIndicator(KUnderWay, "进行中");
        addIndicator(KNotStarted, "未开始");
        addIndicator(KRetrospect, "精彩回顾");

    }

    private void addIndicator(final int index, CharSequence text) {

        View v = inflate(R.layout.layout_meeting_tab);

        TextView tv = (TextView) v.findViewById(R.id.main_tab_tv);
        tv.setText(text);
        v.setTag(index);

        v.setOnClickListener(mTabListener);

        if (index == KUnderWay) {
            v.setSelected(true);
            LastViewType = index;
        }

        fit(v);

        LinearLayout.LayoutParams p = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        p.weight = 1;
        mLayoutTab.addView(v, p);
    }

}
