package lib.ys.frag.form;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.R;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.decor.DecorViewEx.ViewState;
import lib.ys.form.FormItemEx;
import lib.ys.network.result.IListResponse;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;
import lib.ys.view.swipeRefresh.interfaces.ISRListener;

/**
 * @author yuansui
 */
abstract public class SRFormFragEx<T extends FormItemEx<VH>, VH extends ViewHolderEx> extends FormFragEx<T, VH> {

    private BaseSRLayout mSRLayout;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.sr_scroll_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mSRLayout = findView(R.id.sr_scroll_layout);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mSRLayout.setSRListener(new ISRListener() {

            @Override
            public void onSwipeRefresh() {
                setRefreshWay(RefreshWay.swipe);
                getDataFromNet();
            }

            @Override
            public void onSwipeRefreshFinish() {
            }

            @Override
            public boolean onAutoLoadMore() {
                return true;
            }

            @Override
            public boolean onManualLoadMore() {
                return true;
            }
        });

        if (canAutoRefresh()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
    }

    abstract public void getDataFromNet();

    public boolean canAutoRefresh() {
        return true;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        IListResponse<T> r = (IListResponse<T>) result;
        if (r == null || !r.isSucceed() || r.getData() == null) {
            // 表示数据列表返回为null, 解析失败
            stopRefresh();

            if (r != null) {
                onNetworkError(id, new NetError(r.getError()));
            }
            return;
        }

        setItems(r.getData());
        stopRefresh();
        setViewState(ViewState.normal);
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        if (isEmpty()) {
            setViewState(ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (super.onRetryClick()) {
            return true;
        }

        refresh(RefreshWay.embed);
        getDataFromNet();

        return true;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return parseNetworkResponse(id, nr.getText());
    }

    @Override
    public void stopSwipeRefresh() {
        mSRLayout.stopRefresh();
    }

    abstract public IListResponse<T> parseNetworkResponse(int id, String text) throws Exception;
}
