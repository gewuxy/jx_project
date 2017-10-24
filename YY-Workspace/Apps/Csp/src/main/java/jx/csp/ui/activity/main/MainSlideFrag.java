package jx.csp.ui.activity.main;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import jx.csp.R;
import jx.csp.model.main.Square;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;

/**
 * @auther WangLan
 * @since 2017/10/17
 */

public class MainSlideFrag extends BaseVPFrag implements OnPageChangeListener {

    private final int KOne = 1;
    private final float KVpScale = 0.11f; // vp的缩放比例

    private float mLastOffset;

    private TextView mTvCurrentPage;

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
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnPageChangeListener(this);
        getViewPager().setPageMargin(fitDp(27));
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

    public void setData(List<Square> data) {
        // 记录当前index
        int index = getCurrentItem();
        removeAll();
        for (Square s : data) {
            add(MainMeetingFragRouter.create(s).route());
        }
        invalidate();
        setCurrentItem(index);
    }
}
