package jx.csp.ui.activity.main;

import android.view.ViewGroup;
import android.widget.ImageView;

import jx.csp.R;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseVPActivity;

/**
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVPActivity{

    private ImageView mIvShift;
    private ImageView mIvScan;

    private boolean mFlag;

    @Override
    public void initData() {
        add(new MainSquareFrag());
        add(new MainSlideFrag());
        mFlag = true;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addTextViewMid(getString(R.string.CSPmeeting));
        bar.addViewLeft(R.drawable.ic_default_user_header, v -> {
            startActivity(MeActivity.class);
        });

        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector, v -> {
            mIvShift.setSelected(mFlag);
            mFlag = !mFlag;
            if (mFlag) {
                setCurrentItem(0);
            } else {
                setCurrentItem(1);
            }
        });
        mIvShift = Util.getBarView(group, ImageView.class);
    }

    @Override
    public void findViews() {
        super.findViews();
        mIvScan = findView(R.id.main_scan);
    }

    @Override
    public void setViews() {
        super.setViews();
        //不能左右滑动
        setScrollable(false);
        setScrollDuration(0);

    }

}
