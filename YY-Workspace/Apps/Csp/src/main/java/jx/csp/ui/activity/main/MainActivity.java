package jx.csp.ui.activity.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import jx.csp.R;
import jx.csp.model.main.Square;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseVPActivity;

/**
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVPActivity {

    private ImageView mIvShift;

    private boolean mFlag;

    @Override
    public void initData() {
        // 列表(空)
        MainSlideFrag slideFrag = new MainSlideFrag();
        // 网格
        MainSquareFrag squareFrag = new MainSquareFrag();

        squareFrag.setListener((OnSquareRefreshListener) data -> slideFrag.setData(data));
        add(squareFrag);

        add(slideFrag);

        mFlag = true;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
//        NetworkImageView view = new NetworkImageView(this);
//        view.setLayoutParams(LayoutUtil.getViewGroupParams(fitDp(11),fitDp(11)));
//        view.placeHolder(R.drawable.ic_default_user_header)
//                .url(Profile.inst().getString(TProfile.avatar))
//                .load();
        bar.addViewLeft(R.drawable.ic_default_user_header, v -> startActivity(MeActivity.class));

        bar.addTextViewMid(getString(R.string.CSPmeeting));

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
    public void setViews() {
        super.setViews();
        //不能左右滑动
        setScrollable(false);
        setScrollDuration(0);
        setOnClickListener(R.id.main_scan);
    }

    @Override
    public void onClick(View v) {
        startActivity(ScanActivity.class);
    }

    public interface OnSquareRefreshListener {
        void onSquareRefresh(List<Square> data);
    }

}
