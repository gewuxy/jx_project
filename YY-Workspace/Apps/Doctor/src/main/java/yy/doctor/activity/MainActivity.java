package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import lib.sliding.menu.SlidingMenu;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseVPActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.frag.DiscoverFrag;
import yy.doctor.frag.HomeFrag;
import yy.doctor.frag.MeFrag;
import yy.doctor.frag.MeetingFrag;

public class MainActivity extends BaseVPActivity {

    public static void nav(Context context, String test) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.KTestAble, test);
        LaunchUtil.startActivity(context, intent);
    }

    private final float KFactor = 0.25f;
    private final float KSeven = 0.75f;

    private final int KTabRecommend = 0;
    private final int KTabMeeting = 1;
    private final int KTabDiscover = 2;
    private final int KTabMe = 3;


    private SlidingMenu mMenu;
    private LinearLayout mLayoutTab;
    private View mTabPrev;

    @Override
    public void initData() {
        getIntent().getStringExtra(Extra.KTestAble);

        add(new HomeFrag());
        add(new MeetingFrag());
        add(new DiscoverFrag());
        add(new MeFrag());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initTitleBar() {
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.main_layout_tab);
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        addMenu();
        addIndicators();

        setOffscreenPageLimit(getCount());
        setScrollable(false);
    }

    private void addMenu() {
        mMenu = new SlidingMenu(this);
        mMenu.setMode(SlidingMenu.LEFT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setShadowWidthRes(R.dimen.shadow_width);
        mMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        mMenu.setShadowDrawable(R.drawable.menu_shadow);
        mMenu.setFadeDegree(0.25f);
        mMenu.setBackgroundImage(R.mipmap.menu_bg);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mMenu.setMenu(R.layout.layout_menu);

        // 收缩动画 侧滑栏
        mMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * KFactor + 0.75);
                // 后两个数，是变化的中心点参数
                canvas.scale(scale, scale, -canvas.getWidth() / 2, canvas.getHeight() / 2);
            }
        });

        // 收缩动画 主界面
        mMenu.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * KFactor);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }
        });
    }

    private void addIndicators() {
        addIndicator(KTabRecommend, R.drawable.main_selector_recommend, "推荐");
        addIndicator(KTabMeeting, R.drawable.main_selector_meeting, "会议");
        addIndicator(KTabDiscover, R.drawable.main_selector_discover, "发现");
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

        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setCurrentItem(index, false);
            }
        });

        if (index == KTabRecommend) {
            v.setSelected(true);
            mTabPrev = v;
        }

        fit(v);

        LayoutParams p = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        p.weight = 1;
        mLayoutTab.addView(v, p);
    }

    /**
     * 开启关闭侧滑menu
     */
    public void toggleMenu() {
        if (mMenu != null) {
            mMenu.toggle();
        }
    }

}
