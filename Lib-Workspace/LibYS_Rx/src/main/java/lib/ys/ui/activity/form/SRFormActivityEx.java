package lib.ys.ui.activity.form;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import lib.network.model.NetworkResp;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IListResult;
import lib.ys.R;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.view.swipeRefresh.base.BaseSRLayout;
import lib.ys.view.swipeRefresh.interfaces.OnSRListener;

/**
 * @author yuansui
 */
abstract public class SRFormActivityEx<T extends FormEx<VH>, VH extends IViewHolder> extends FormActivityEx<T, VH> {

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

        mSRLayout.setSRListener(new OnSRListener() {

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

        if (getInitRefreshWay() == RefreshWay.embed && enableInitRefresh()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
    }

    abstract public void getDataFromNet();

    public boolean enableInitRefresh() {
        return true;
    }

    protected void setRefreshEnabled(boolean enabled) {
        mSRLayout.setRefreshEnabled(enabled);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        IListResult<T> r = (IListResult<T>) result;
        if (r == null || !r.isSucceed() || r.getData() == null) {
            // 表示数据列表返回为null, 解析失败
            stopRefresh();

            if (r != null) {
                onNetworkError(id, r.getError());
            }
            return;
        }

        setItems(r.getData());
        stopRefresh();
        setViewState(ViewState.normal);
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
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
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return parseNetworkResponse(id, r.getText());
    }

    @Override
    public void stopSwipeRefresh() {
        mSRLayout.stopRefresh();
    }

    abstract public IListResult<T> parseNetworkResponse(int id, String text) throws Exception;

    @Override
    protected boolean enableHideKeyboardWhenChangeFocus() {
        return true;
    }
}
