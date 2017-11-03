package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.MeetContract;
import jx.csp.contact.VPEffectContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.presenter.VPEffectPresenterImpl;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
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
    private View mSlideDataLayout;

    private VPEffectContract.P mEffectPresenter;
    // 单页的p层
    private MeetContract.P mSingleFragPresenter;
    private Meet mMeet;

    @Override
    public void initData() {
        mEffectPresenter = new VPEffectPresenterImpl(this, KVpScale);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_main_slide;
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
        mSlideDataLayout = findView(R.id.main_slide_data_layout);
        mTvReminder = findView(R.id.tv_reminder);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOffscreenPageLimit(3);
        setScrollDuration(300);
        getViewPager().setPageMargin(fitDp(27));
        setOnClickListener(R.id.click_continue);

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mEffectPresenter.onPageScrolled(getPagerAdapter(), position, positionOffset, getCount());
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(getCurrentItem() + KOne));
                YSLog.d("position", "position");
                if (getItem(position) instanceof MeetSingleFrag) {
                    // 在进行中要有提示 通过时间判断
                    mMeet = ((MeetSingleFrag) (getItem(position))).getMeet();
                    mSingleFragPresenter = ((MeetSingleFrag) (getItem(position))).getPresenter();
                    if (mMeet == null || mSingleFragPresenter == null) {
                        goneView(mLayout);
                        return;
                    }
                    switch (mMeet.getInt(TMeet.playType)) {
                        case PlayType.reb: {
                            showView(mLayout);
                            mTvReminder.setText(R.string.playing);
                        }
                        break;
                        case PlayType.live:
                        case PlayType.video: {
                            if (mMeet.getLong(TMeet.startTime) < System.currentTimeMillis() && mMeet.getLong(TMeet.endTime) > System.currentTimeMillis()) {
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
    }

    @Override
    public void onClick(View v) {
        mSingleFragPresenter.onMeetClick(mMeet);
    }

    public void setData(List<Meet> data) {
        // 记录当前index
        int index = getCurrentItem();

        removeAll();
        if (data == null) {
            goneView(mSlideDataLayout);
            add(new EmptyFrag());
            invalidate();
        } else {
            int size = data.size();
            for (int i = 0; i < size; ++i) {
                add(MeetSingleFragRouter.create(data.get(i)).route());

            }
            invalidate();

            if (index > size) {
                index = 0;
            }
            setCurrentItem(index);

            mTvTotalPage.setText(String.valueOf(data.size()));
        }
    }

    public Fragment getItem() {
        return super.getItem(getCurrentItem());
    }

    @Override
    public void setPosition(int position) {
        setCurrentItem(position);
    }

    @Override
    public int getPosition() {
        return getCurrentItem();
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.vp_frag_delete_meeting || type == NotifyType.vp_frag_copy_duplicate) {
            invalidate();
        }
    }

    @Override
    public void onStopRefresh() {

    }
}
