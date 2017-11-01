package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.def.MeetState;
import jx.csp.model.main.Meet;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;

/**
 * 首页左右滑动的列表的frag
 * @auther WangLan
 * @since 2017/10/17
 */
@Route
public class MeetVPFrag extends BaseVPFrag implements OnPageChangeListener {

    private final int KOne = 1;
    private final float KVpScale = 0.2f; // vp的缩放比例

    private float mLastOffset;

    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvReminder;
    private View mLayout;
    private View mSlideDataLayout;

    @Arg
    public String mCourseId;

    @Override
    public void initData() {
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
        setOnPageChangeListener(this);
        setOffscreenPageLimit(3);
        setScrollDuration(300);
        getViewPager().setPageMargin(fitDp(27));
        setOnClickListener(R.id.click_continue);

    }

    @Override
    public void onClick(View v) {
        LiveRecordActivityRouter.create(mCourseId).route(getContext());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition;
        float realOffset;
        int nextPosition;
        if (mLastOffset > positionOffset) {
            realPosition = position + KOne;
            nextPosition = position;
            realOffset = KOne - positionOffset;
        } else {
            realPosition = position;
            nextPosition = position + KOne;
            realOffset = positionOffset;
        }

        if (nextPosition > getCount() - KOne || realPosition > getCount() - KOne) {
            return;
        }

        viewChange(realPosition, KOne - realOffset);
        viewChange(nextPosition, realOffset);

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        mTvCurrentPage.setText(String.valueOf(getCurrentItem() + KOne));

        YSLog.d("position", "position");
        if (getItem(position) instanceof MeetSingleFrag && ((MeetSingleFrag) (getItem(position))).getType() == MeetState.living) {
            showView(mLayout);
        } else if (getItem(position) instanceof MeetSingleFrag && ((MeetSingleFrag) (getItem(position))).getType() == MeetState.playing) {
            showView(mLayout);
            mTvReminder.setText(R.string.playing);
        }else {
            goneView(mLayout);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        YSLog.d("position", "position----");
    }

    /**
     * 改变view的大小  缩放
     */
    private void viewChange(int position, float offset) {
        View view = getItem(position).getView();
        if (view == null) {
            return;
        }
        float scale = KOne + KVpScale * offset;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    public void setData(List<Meet> data) {
        // 记录当前index
        int index = getCurrentItem();

        if (data == null) {
            goneView(mSlideDataLayout);
            add(new EmptyFrag());
            invalidate();
        } else {
            removeAll();
            for (Meet s : data) {
                add(MeetSingleFragRouter.create(s).route());
            }
            invalidate();
            setCurrentItem(index);
            mTvTotalPage.setText(String.valueOf(data.size()));
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }
}
