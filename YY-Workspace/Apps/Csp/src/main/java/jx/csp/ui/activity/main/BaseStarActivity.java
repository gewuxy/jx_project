package jx.csp.ui.activity.main;

import android.support.annotation.CallSuper;
import android.view.View;

import inject.annotation.router.Arg;
import jx.csp.R;
import jx.csp.contact.StarContract;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;

/**
 * 星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
abstract public class BaseStarActivity<V extends StarContract.V, P extends StarContract.P<V>> extends BaseActivity {

    @Arg
    Meet mMeet;

    protected View mLayoutDefault;
    protected View mLayoutDataMatrix;
    protected NetworkImageView mIvDataMatrix;

    private P mP;
    private V mV;

    public V getV() {
        if (mV == null) {
            mV = createViewImpl();
        }
        return mV;
    }

    public P getP() {
        if (mP == null) {
            mP = createPresenterImpl();
        }
        return mP;
    }

    @Override
    public void initData() {
        mP = createPresenterImpl();
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_star_content;
    }

    @Override
    public final void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.default_ic_close, l -> barBack());
        boolean start = mMeet.getBoolean(Meet.TMeet.starRateFlag);
        bar.addTextViewMid(start ? R.string.start_comment : R.string.finish);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog d = new ShareDialog(BaseStarActivity.this, mMeet);
            d.show();
        });

        Util.addDivider(bar);
    }

    @CallSuper
    @Override
    public void findViews() {
        mLayoutDefault = findView(R.id.star_layout_meet_default);
        mLayoutDataMatrix = findView(R.id.star_layout_data_matrix);
        mIvDataMatrix = findView(R.id.star_iv_data_matrix);
    }

    @CallSuper
    @Override
    public void setViews() {
        getDecorView().setBackgroundResource(R.color.app_nav_bar_bg);
        refresh(RefreshWay.embed);
        mP.getDataFromNet(StarContract.KReqStar);
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed);
            mP.getDataFromNet(StarContract.KReqStar);
        }
        return true;
    }

    /**
     * 星评关闭
     *
     * @param flag true 展示二维码, false 展示会讲制作
     */
    protected void starState(boolean flag) {
        if (flag) {
            goneView(mLayoutDefault);
            showView(mLayoutDataMatrix);
        } else {
            showView(mLayoutDefault);
            goneView(mLayoutDataMatrix);
        }
    }

    /**
     * 设置二维码图片
     *
     * @param url 二维码图片的url
     */
    protected void setDataMatrix(String url) {
        mIvDataMatrix.url(url).load();
    }

    abstract protected P createPresenterImpl();

    abstract protected V createViewImpl();

    /**
     * 按下 barLeft Icon
     */
    abstract protected void barBack();

    abstract protected class BaseStarViewImpl implements StarContract.V {

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
            BaseStarActivity.this.setViewState(state);
        }
    }
}
