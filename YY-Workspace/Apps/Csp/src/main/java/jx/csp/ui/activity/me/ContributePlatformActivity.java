package jx.csp.ui.activity.me;

import android.widget.TextView;

import java.util.ArrayList;

import jx.csp.R;
import jx.csp.adapter.PlatformAdapter;
import jx.csp.adapter.PlatformAdapter.OnPlatformCheckedListener;
import jx.csp.contact.ContributePlatformContract;
import jx.csp.contact.ContributePlatformContract.P;
import jx.csp.dialog.PlatformDialog;
import jx.csp.model.contribute.Platform;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.presenter.ContributePlatformPresenterImpl;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;

/**
 * 投稿平台
 *
 * @auther Huoxuyu
 * @since 2017/9/28
 */

public class ContributePlatformActivity extends BaseSRListActivity<Platform, PlatformAdapter>
        implements OnPlatformCheckedListener {

    private final int KCodePlatform = 1;

    private TextView mTv;
    private ArrayList<Platform> mSelectedItem;
    private Platform mPlatform;

    private P mPresenter;
    private ContributePlatformContract.V mView;

    @Override
    public void initData() {
        mSelectedItem = new ArrayList<>();

        mView = new ContributePlatformViewImpl();
        mPresenter = new ContributePlatformPresenterImpl(mView);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.contribute_platform, this);
        mView.showDialog(bar);
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
        mTv = findView(R.id.contribute_tv_platform);
    }

    @Override
    public void setViews() {
        super.setViews();
        mTv.setEnabled(false);
        setOnClickListener(R.id.contribute_tv_platform);

        getAdapter().setListener(this);
    }

    @Override
    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.contribute_tv_platform: {
                refresh(RefreshWay.dialog);
                mPresenter.clickContributeReq(mSelectedItem, mPlatform);
            }
            break;
        }
    }

    @Override
    public void onPlatformChecked(int position, boolean isSelected) {
        mView.getAcceptIdItem(position, isSelected);
        mView.changeButtonStatus();
    }

    private class ContributePlatformViewImpl implements ContributePlatformContract.V {

        @Override
        public void showDialog(NavBar bar) {
            bar.addViewRight(R.drawable.ic_default_hint, v -> {
                PlatformDialog dialog = new PlatformDialog(ContributePlatformActivity.this);
                dialog.show();
            });
        }

        @Override
        public void getAcceptIdItem(int position, boolean isSelected) {
            if (isSelected) {
                mSelectedItem.add(getItem(position));
            } else {
                mSelectedItem.remove(getItem(position));
            }
        }

        @Override
        public void changeButtonStatus() {
            // 判断确认按钮
            if (mSelectedItem.isEmpty()) {
                // 不可点
                mTv.setEnabled(false);
            } else {
                // 可投稿
                mTv.setEnabled(true);
            }
        }

        @Override
        public void stopRefreshItem() {
            stopRefresh();
            finish();
        }
    }
}
