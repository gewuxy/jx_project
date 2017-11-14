package jx.csp.ui.frag.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.VPEffectContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.presenter.VPEffectPresenterImpl;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.view.pager.transformer.ScaleTransformer;
import lib.yy.ui.frag.base.BaseVPFrag;

/**
 * 首页左右滑动的列表的frag
 *
 * @auther WangLan
 * @since 2017/10/17
 */
@Route
public class MeetVpFrag extends BaseVPFrag implements IMeetOpt, VPEffectContract.V {

    private final int KOne = 1;
    private final float KVpScale = 0.2f; // vp的缩放比例

    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvReminder;
    private View mLayout;

    private VPEffectContract.P mEffectPresenter;
    private Meet mMeet;

    private List<Meet> mMeets;

    @Override
    public void initData(Bundle savedInstanceState) {
        mEffectPresenter = new VPEffectPresenterImpl(this, KVpScale);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_main_vp;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvCurrentPage = findView(R.id.frag_current_page);
        mTvTotalPage = findView(R.id.frag_total_page);
        mLayout = findView(R.id.live_reminder);
        mTvReminder = findView(R.id.tv_reminder);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(3);
        setScrollDuration(300);
        //getViewPager().setPageMargin(fitDp(21));
        setOnClickListener(R.id.click_continue);

        setPageTransformer(false, new ScaleTransformer(KVpScale));
        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //mEffectPresenter.onPageScrolled(getPagerAdapter(), position, positionOffset, getCount());
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrentItem() + KOne));
                YSLog.d("position", "position");
                if (getItem(position) instanceof MeetSingleFrag) {
                    // 在进行中要有提示 通过时间判断
                    mMeet = ((MeetSingleFrag) (getItem(position))).getMeet();
                    if (mMeet == null) {
                        goneView(mLayout);
                        return;
                    }
                    switch (mMeet.getInt(TMeet.playType)) {
                        case PlayType.reb: {
                            if (mMeet.getInt(TMeet.playState) == PlayState.record) {
                                showView(mLayout);
                                mTvReminder.setText(R.string.playing);
                            } else {
                                goneView(mLayout);
                            }
                        }
                        break;
                        case PlayType.live:
                        case PlayType.video: {
                            if (mMeet.getInt(TMeet.liveState) == LiveState.live && mMeet.getLong(TMeet.endTime) < System.currentTimeMillis()) {
                                YSLog.d(TAG, "直播会议进行中");
                                showView(mLayout);
                                mTvReminder.setText(R.string.living);
                            } else {
                                YSLog.d(TAG, "直播会议不在进行中");
                                goneView(mLayout);
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
    }

    @Override
    public void onClick(View v) {
        ((MeetSingleFrag) (getItem(getCurrentItem()))).onMeetClick();
    }

    public void setData(List<Meet> data) {
        mMeets = data;
    }

    public Fragment getItem() {
        return super.getItem(getCurrentItem());
    }

    @Override
    public void setPosition(int position) {
        if (position == 0 && getCount() >= 2) {
            // FIXME: 2017/11/13 缩放有时第一个没有效果， 暂时先这样处理
            // 延时执行
            setCurrentItem(1);
            new Handler().postDelayed(() -> setCurrentItem(0), 50);
        } else {
            setCurrentItem(position);
        }
    }

    @Override
    public int getPosition() {
        return getCurrentItem();
    }

    public void nativeInvalidate() {
        // 记录当前index
        int index = getCurrentItem();

        removeAll();
        if (mMeets == null || mMeets.isEmpty()) {
            add(new EmptyFrag());
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

            setCurrentItem(index);
            mTvTotalPage.setText(String.valueOf(mMeets.size()));
        }
    }

    @Override
    public void onStopRefresh() {

    }
}
