package jx.csp.ui.activity.main;

import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.ui.frag.main.PreviewPhotoFragRouter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;

/**
 * 预览选择的照片界面
 *
 * @auther : GuoXuan
 * @since : 2018/2/3
 */
@Route
public class PreviewPhotoActivity extends BaseVpActivity implements ViewPager.OnPageChangeListener {

    @Arg(opt = true, defaultInt = Constants.KPhotoMax)
    int mMaxSelect;

    @Arg
    ArrayList<String> mPhotosPath; // 选择的照片

    private TextView mTvGuide;
    private TextView mTvFinish;

    @Override
    public int getContentViewId() {
        return R.layout.activity_preview_photo;
    }

    @Override
    public void initData() {
        // do nothing
    }

    @Override
    public void initNavBar(NavBar bar) {
        ViewGroup viewLeft = bar.addViewLeft(R.drawable.nav_bar_ic_back, "1/" + mPhotosPath.size(), l -> finish());
        mTvGuide = Util.getBarView(viewLeft, TextView.class);

        View viewRight = inflate(R.layout.layout_preview_photo_nav_bar_right);
        mTvFinish = viewRight.findViewById(R.id.preview_photo_nav_bar_tv);
        bar.addViewRight(viewRight, null);

        bar.setBackgroundAlpha(242);
        Util.addDivider(bar);
    }

    @Override
    public void setViews() {
        super.setViews();

        for (String path : mPhotosPath) {
            add(PreviewPhotoFragRouter.create(path).route());
        }
        invalidate();

        mTvGuide.setTextSize(TypedValue.COMPLEX_UNIT_PX, fit(18));
        setOnPageChangeListener(this);

        mTvFinish.setText(getString(R.string.finish) + mPhotosPath.size() + "/" + mMaxSelect);
        setOnClickListener(mTvFinish);
    }

    @Override
    protected DecorViewEx.TNavBarState getNavBarState() {
        return DecorViewEx.TNavBarState.above;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_photo_nav_bar_tv: {
                setResult(RESULT_OK);
                finish();
            }
            break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing
    }

    @Override
    public void onPageSelected(int position) {
        mTvGuide.setText(position + 1 + "/" + mPhotosPath.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // do nothing
    }
}
