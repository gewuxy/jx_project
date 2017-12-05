package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.util.ScaleTransformer;
import jx.csp.util.Util;
import lib.jx.ui.frag.base.BaseVPFrag;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;

/**
 * 首页左右滑动的列表的frag
 *
 * @auther WangLan
 * @since 2017/10/17
 */
@Route
public class MeetVpFrag extends BaseVPFrag implements IMeetOpt {

    private final int KOne = 1;
    private final float KVpScale = 0.1f; // vp的缩放比例

    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvReminder;
    private View mLayoutLiveReminder;
    private View mLayoutPageNum;
    private View mLayoutEmpty;

    private Meet mMeet;

    private List<Meet> mMeets;


    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_main_vp;
    }

    @Override
    protected int getViewPagerResId() {
        return R.id.main_vp_layout_vp;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvCurrentPage = findView(R.id.main_vp_tv_current_page);
        mTvTotalPage = findView(R.id.main_vp_tv_total_page);
        mLayoutLiveReminder = findView(R.id.main_vp_layout_live_reminder);
        mTvReminder = findView(R.id.main_vp_tv_reminder);
        mLayoutPageNum = findView(R.id.main_vp_layout_page_num);
        mLayoutEmpty = findView(R.id.main_vp_layout_empty);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(3);
        setScrollDuration(300);
        setOnClickListener(R.id.main_vp_tv_continue);

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrPosition() + KOne));
                YSLog.d("position", "position");
                if (getItem(position) instanceof MeetSingleFrag) {
                    // 在进行中要有提示 通过时间判断
                    mMeet = ((MeetSingleFrag) (getItem(position))).getMeet();
                    if (mMeet == null) {
                        goneView(mLayoutLiveReminder);
                        return;
                    }
                    switch (mMeet.getInt(TMeet.playType)) {
                        case PlayType.reb: {
                            goneView(mLayoutLiveReminder);
                        }
                        break;
                        case PlayType.live:
                        case PlayType.video: {
                            long startTime = mMeet.getLong(TMeet.startTime);
                            long endTime = mMeet.getLong(TMeet.endTime);
                            long currentTime = System.currentTimeMillis();
                            if (startTime < currentTime && endTime > currentTime) {
                                YSLog.d(TAG, "直播会议进行中");
                                showView(mLayoutLiveReminder);
                                mTvReminder.setText(R.string.living);
                            } else {
                                YSLog.d(TAG, "直播会议不在进行中");
                                goneView(mLayoutLiveReminder);
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        nativeInvalidate();

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ScaleTransformer t = new ScaleTransformer(KVpScale, Util.calcVpOffset(getViewPager().getPaddingLeft(), getViewPager().getWidth()));
                setPageTransformer(false, t);

                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        ((MeetSingleFrag) (getItem(getCurrPosition()))).onMeetClick();
    }

    public void setData(List<Meet> data) {
        mMeets = data;
    }

    @Override
    public void setPosition(int position) {
        setCurrPosition(position, false);
    }

    @Override
    public int getPosition() {
        return getCurrPosition();
    }

    public void nativeInvalidate() {
        // 记录当前index
        int index = getCurrPosition();

        removeAll();
        if (mMeets == null || mMeets.isEmpty()) {
            hideView(mLayoutPageNum);
            hideView(getViewPager());
            showView(mLayoutEmpty);
            invalidate();
        } else {
            int size = mMeets.size();
            for (int i = 0; i < size; ++i) {
                add(MeetSingleFragRouter.create(mMeets.get(i)).route());
            }

            if (index > size) {
                index = 0;
            }
            invalidate();

            setCurrPosition(index);

            showView(mLayoutPageNum);
            goneView(mLayoutEmpty);
            showView(getViewPager());

            mTvTotalPage.setText(String.valueOf(mMeets.size()));
        }
    }

    @Override
    public void allowEnter() {
        getCurrItem().allowEnter();
    }

    @Override
    public void notAllowEnter() {
        getCurrItem().notAllowEnter();
    }

    @Override
    protected MeetSingleFrag getCurrItem() {
        return (MeetSingleFrag) super.getCurrItem();
    }
}
