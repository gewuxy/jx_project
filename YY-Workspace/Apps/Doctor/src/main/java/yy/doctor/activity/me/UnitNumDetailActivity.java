package yy.doctor.activity.me;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.ex.NavBar;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.BlurInterceptor;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.NestCheckBox;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.R;
import yy.doctor.adapter.UnitNumDetailAdapter;
import yy.doctor.util.Util;

/**
 * 单位号
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumDetailActivity extends BaseListActivity<String> {

    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");
    private static final float KAvatarScale = 0.5f;

    private SwipeZoomListView mZoomView;
    private NetworkImageView mIvZoom;
    private NetworkImageView mIvAvatar;
    private View mLayoutHeaderRoot;
    private NestCheckBox mCbAttention;


    @Override
    public void initData() {
        for (int i = 0; i < 5; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_unit_num_detail;
    }

    @Override
    public int getListViewResId() {
        return android.R.id.list;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        setNavBarAutoAlphaByScroll(fitDp(219), bar);

        bar.addViewRight(R.mipmap.nav_bar_ic_search, new OnClickListener() {

            @Override
            public void onClick(View v) {

                showToast("555");
            }
        });

        bar.addViewRight(R.mipmap.nav_bar_ic_more, new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialogCancelAttention();
            }
        });

    }

    @Override
    public void findViews() {
        super.findViews();

        mZoomView = findView(R.id.unit_num_detail_layout_zoom);
        mIvZoom = findView(R.id.unit_num_detail_zoom_iv);
        mIvAvatar = findView(R.id.unit_num_detail_iv_avatar);
        mLayoutHeaderRoot = findView(R.id.unit_num_zoom_header_root);
        mCbAttention = findView(R.id.unit_num_detail_check_box_attention);
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_unit_num_detail_header);
    }

    @Override
    public void setViews() {
        super.setViews();

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

                int cut_w = (int) (w * KAvatarScale);
                int cut_h = (int) (h * KAvatarScale);

                int start_x = (w - cut_w) / 2;
                int start_y = (h - cut_h) / 2;


                AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(w, h);
                mZoomView.setHeaderLayoutParams(localObject);

                mIvZoom.res(R.mipmap.ic_launcher)
                        .addInterceptor(new CutInterceptor(start_x, start_y, cut_w, cut_h))
                        .addInterceptor(new BlurInterceptor(UnitNumDetailActivity.this))
                        .load();

                removeOnGlobalLayoutListener(this);
            }
        });

        mCbAttention.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mCbAttention.getRealCheckBox().setText("已关注");
                } else {
                    mCbAttention.getRealCheckBox().setText("关注");
                }

            }
        });

    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new UnitNumDetailAdapter();
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

    private void showDialogCancelAttention() {

        /*final BottomDialog dialog = new BottomDialog(this);
        dialog.addItem("不再关注", KColorNormal, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.addItem("取消", KColorCancel, new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();*/

    }

}
