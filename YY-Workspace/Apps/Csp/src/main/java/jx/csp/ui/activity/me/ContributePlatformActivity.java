package jx.csp.ui.activity.me;

import android.os.Bundle;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.PlatformAdapter;
import jx.csp.adapter.PlatformAdapter.OnPlatformCheckedListener;
import jx.csp.contact.ContributePlatformContract;
import jx.csp.model.Platform;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.presenter.ContributePlatformPresenterImpl;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseSRListActivity;

/**
 * 投稿平台
 *
 * @auther Huoxuyu
 * @since 2017/9/28
 */
@Route
public class ContributePlatformActivity extends BaseSRListActivity<Platform, PlatformAdapter>
        implements OnPlatformCheckedListener {

    private boolean mTipsVisibility = false;

    private TextView mTvPlatform;
    private TextView mTvTips;

    private ContributePlatformContract.P mPresenter;
    private ContributePlatformContract.V mView;

    @Arg
    String mCourseId;

    @Override
    public void initData(Bundle state) {
        mView = new ContributePlatformViewImpl();
        mPresenter = new ContributePlatformPresenterImpl(mView);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.contribute_platform, this);
        bar.addViewRight(R.drawable.ic_default_hint, v -> {
            if (mTipsVisibility) {
                goneView(mTvTips);
            } else {
                showView(mTvTips);
            }
            mTipsVisibility = !mTipsVisibility;
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(DeliveryAPI.contribute().build());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_contribute_platform_footer;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvPlatform = findView(R.id.contribute_tv_platform);
        mTvTips = findView(R.id.contribute_tv_tips);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvPlatform.setEnabled(false);
        setOnClickListener(R.id.contribute_tv_platform);

        getAdapter().setListener(this);
    }

    @Override
    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.contribute_tv_platform: {
                refresh(RefreshWay.dialog);
                mPresenter.clickContribute(mCourseId);
            }
            break;
        }
    }

    @Override
    public void onPlatformChecked(int position, boolean isSelected) {
        Platform item = getItem(position);
        mPresenter.addItem(item, isSelected);
    }

    private class ContributePlatformViewImpl implements ContributePlatformContract.V {

        @Override
        public void changeButtonStatus(boolean enabled) {
            if (mTvPlatform != null) {
                mTvPlatform.setEnabled(enabled);
            }
        }

        @Override
        public void onFinish() {
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }
}
