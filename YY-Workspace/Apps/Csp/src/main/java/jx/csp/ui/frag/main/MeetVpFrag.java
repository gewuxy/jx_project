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
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Live.LiveState;
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

    private List<Meet> mMeets;

    private OnMeetVpListener mListener;

    public interface OnMeetVpListener {
        void replace(boolean flag);
    }

    public void setListener(OnMeetVpListener listener) {
        mListener = listener;
    }

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
                pageSelected(position);
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

    public void pageSelected(int position) {
        mTvCurrentPage.setText(String.valueOf(getCurrPosition() + KOne));
        YSLog.d("position", "position");
        if (mMeets == null || mMeets.isEmpty()) {
            return;
        }
        // 在进行中要有提示 通过时间和状态判断
        Meet meet = mMeets.get(position);
        if (meet == null) {
            goneView(mLayoutLiveReminder);
            if (mListener != null) {
                mListener.replace(false);
            }
            return;
        }
        switch (meet.getInt(TMeet.playType)) {
            case CourseType.reb: {
                goneView(mLayoutLiveReminder);
                if (mListener != null) {
                    mListener.replace(false);
                }
            }
            break;
            case CourseType.ppt_live:
            case CourseType.ppt_video_live: {
                if (reminder(position)) {
                    YSLog.d(TAG, "直播会议进行中");
                    showView(mLayoutLiveReminder);
                    if (mListener != null) {
                        mListener.replace(true);
                    }
                    mTvReminder.setText(R.string.living);
                } else {
                    YSLog.d(TAG, "直播会议不在进行中");
                    goneView(mLayoutLiveReminder);
                    if (mListener != null) {
                        mListener.replace(false);
                    }
                }
            }
            break;
        }
    }

    @Override
    protected MeetSingleFrag getItem(int position) {
        return (MeetSingleFrag) super.getItem(position);
    }

    /**
     * 直播是否在进行中
     *
     * @param position
     * @return true 进行中
     */
    public boolean reminder(int position) {
        if (position > getCount() || position < 0) {
            return false;
        }
        Meet mMeet = mMeets.get(position);
        if (mMeet == null) {
            return false;
        }
        long startTime = mMeet.getLong(TMeet.startTime);
        long endTime = mMeet.getLong(TMeet.endTime);
        long serverTime = mMeet.getLong(TMeet.serverTime);
        int liveState = mMeet.getInt(TMeet.liveState);
        return (liveState == LiveState.live || liveState == LiveState.stop) && (startTime < serverTime && endTime > serverTime);
    }

    @Override
    public void onClick(View v) {
        getItem(getCurrPosition()).onMeetClick();
    }

    public void setData(List<Meet> data) {
        mMeets = data;
    }

    @Override
    public void setPosition(int position) {
        if (position == getPosition()) {
            pageSelected(position);
        } else {
            setCurrPosition(position, false);
        }
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
    public void showSharePlayback(String id) {
        for (int i = 0; i < getData().size(); ++i) {
            MeetSingleFrag frag = getItem(i);
            if (mMeets.get(i).getString(TMeet.id).equals(id)) {
                frag.showSharePlayback(id);
            }
        }
    }

    @Override
    public void goneSharePlayback(String id) {
        for (int i = 0; i < getData().size(); ++i) {
            MeetSingleFrag frag = getItem(i);
            if (mMeets.get(i).getString(TMeet.id).equals(id)) {
                frag.goneSharePlayback(id);
            }
        }
    }

    @Override
    protected MeetSingleFrag getCurrItem() {
        return (MeetSingleFrag) super.getCurrItem();
    }
}
