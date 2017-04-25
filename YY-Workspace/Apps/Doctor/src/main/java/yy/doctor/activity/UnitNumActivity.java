package yy.doctor.activity;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.BlurInterceptor;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.network.image.renderer.CornerRenderer;
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

    private static final float KAvatarScale = 0.15f;

    private SwipeZoomListView mZoomView;
    private NetworkImageView mIvZoom;
    private NetworkImageView mIvAvatar;
    private NetworkImageView mIvAttention;
    private View mLayoutHeaderRoot;


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
        setNavBarAutoAlphaByScroll(fitDp(219), bar);
    }

    @Override
    public void findViews() {
        super.findViews();

        mZoomView = findView(R.id.unit_num_layout_zoom);
        mIvZoom = findView(R.id.unit_num_zoom_iv);
        mIvAvatar = findView(R.id.unit_num_iv_avatar);
        mIvAttention = findView(R.id.unit_num_iv_attention);
        mLayoutHeaderRoot = findView(R.id.unit_num_zoom_header_root);
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_unit_num_header);
    }

    @Override
    public void setViewsValue() {
        super.setViewsValue();

        mIvAttention.res(R.mipmap.unit_num_detail_ic_attention)
                .renderer(new CornerRenderer(fitDp(1)))
                .load();

        mZoomView.setZoomEnabled(true);

        mIvAvatar.res(R.mipmap.ic_launcher)
                .renderer(new CircleRenderer())
                .load();


        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int w = mLayoutHeaderRoot.getWidth();
                int h = mLayoutHeaderRoot.getHeight();
                if (w == 0 || h == 0) {
                    return;
                }

                AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(w, h);
                mZoomView.setHeaderLayoutParams(localObject);

                mIvZoom.res(R.mipmap.ic_launcher)
                        .addInterceptor(new CutInterceptor(0, 0, (int) (w * KAvatarScale), (int) (h * KAvatarScale)))
                        .addInterceptor(new BlurInterceptor(UnitNumActivity.this))
                        .load();

                removeOnGlobalLayoutListener(this);
            }
        });
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
