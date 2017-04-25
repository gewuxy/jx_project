package yy.doctor.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import lib.ys.LogMgr;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.ex.NavBar;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.service.RunnableEx;
import lib.ys.util.bmp.BmpUtil;
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
    private ImageView mIvZoom;
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

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        mZoomView.setHeaderLayoutParams(localObject);

        mIvAvatar.res(R.mipmap.ic_launcher).listener(new NetworkImageListener() {

            @Override
            public void onImageSet(@Nullable ImageInfo info, @Nullable final Bitmap bmp) {
                LogMgr.d(TAG, "onImageSet: bitmap = " + bmp);
                if (info != null) {
                    mIvAvatar.fetch();
                } else if (bmp != null) {
                    runOnUIThread(new RunnableEx() {

                        @Override
                        public void run() {
                            // TODO: 缩放
                            Bitmap resizeBmp = BmpUtil.resizeBmpMutable(bmp, mIvZoom.getWidth(), mIvZoom.getHeight());

                            Bitmap blurBmp = BmpUtil.blur(resizeBmp, 15, UnitNumActivity.this);
                            mIvZoom.setImageBitmap(blurBmp);
                        }
                    });
                }
            }
        }).load();
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
