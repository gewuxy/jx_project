package yy.doctor.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.ex.NavBar;
import lib.ys.model.Screen;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.BlurInterceptor;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.R;
import yy.doctor.adapter.UnitNumAdapter;
import yy.doctor.util.Util;

/**
 * 单位号
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumActivity extends BaseListActivity<String> {

    private SwipeZoomListView mZoomView;
    private NetworkImageView mIvZoom;
    private NetworkImageView mIvAvatar;

    @Override
    public void initData() {
        for (int i = 0; i < 20; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_unit_num;
    }

    @Override
    public int getListViewResId() {
        return android.R.id.list;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void findViews() {
        super.findViews();

        mZoomView = findView(R.id.unit_num_layout_zoom);
        mIvZoom = findView(R.id.unit_num_zoom_iv);
        mIvAvatar = findView(R.id.unit_num_header_iv_avatar);
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mZoomView.setZoomEnabled(true);

        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(Screen.getWidth(), fitDp(219));
        mZoomView.setHeaderLayoutParams(localObject);

        mIvAvatar.res(R.mipmap.ic_launcher)
                .renderer(new CircleRenderer())
                .load();

        mIvZoom.res(R.mipmap.ic_launcher)
                .addInterceptor(new CutInterceptor(0, 0, Screen.getWidth(), fitDp(219)))
                .addInterceptor(new BlurInterceptor(this, 25))
                .load();

    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new UnitNumAdapter();
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void onItemClick(View v, int position) {
        showToast(position + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ViewUtil.recycleIvBmp(mIvZoom);
    }
}
