package jx.csp.ui.activity.main;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import jx.csp.R;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseVPActivity;

/**
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVPActivity {

    private boolean mFlag;
    private RelativeLayout mRl;
    private ImageView mIvShift;

    @Override
    public void initData() {
        add(new MainSquareFrag());
        add(new MainSlideFrag());
        mFlag = true;
    }

    @Override
    public int getContentViewId() {
            return R.layout.activity_main_slide;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addTextViewMid("CSPmeeting");
        bar.addViewLeft(R.drawable.ic_default_user_header, v -> {
            startActivity(MeActivity.class);
        });
        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector,v ->{
            mIvShift.setSelected(mFlag);
            mFlag = !mFlag;
            if (mFlag) {
                setCurrentItem(0);
                YSLog.d("mFlag", mFlag + "");
            } else {
                setCurrentItem(1);
                YSLog.d("mFlag", mFlag + "");
            }
        });
        mIvShift = Util.getBarView(group,ImageView.class);
    }

    @Override
    public void findViews() {
        super.findViews();
        mRl = findView(R.id.layout_main_slide);
    }

    @Override
    public void setViews() {
        super.setViews();
        //不能左右滑动
        setScrollable(false);
        setScrollDuration(0);
    }
}
