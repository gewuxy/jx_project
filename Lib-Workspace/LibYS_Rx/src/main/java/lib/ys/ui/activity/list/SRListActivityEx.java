package lib.ys.ui.activity.list;

import android.support.annotation.CallSuper;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.network.Network;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IListResult;
import lib.ys.AppEx;
import lib.ys.R;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.ui.interfaces.impl.scrollable.SROpt;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnSROptListener;
import lib.ys.util.DeviceUtil;


/**
 * Base activity with swipe listView
 *
 * @param <T>
 */
abstract public class SRListActivityEx<T, A extends IAdapter<T>> extends ListActivityEx<T, A> implements OnSROptListener {

    private SROpt<T> mSROpt = new SROpt<>(this);

    @Override
    public int getContentViewId() {
        return R.layout.sr_list_layout;
    }

    @Override
    public int getScrollableViewId() {
        return R.id.sr_list_view;
    }

    @Override
    public int getSRLayoutResId() {
        return R.id.sr_list_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();
        mSROpt.findViews(getDecorView(), getSRLayoutResId());
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mSROpt.setViews();

        if (getInitRefreshWay() == RefreshWay.embed && enableInitRefresh()) {
            // 为了更好的体验, 在embed loading显示之前先隐藏掉
            hideView(getDecorView().getContentView());
        }
    }

    @Override
    public View createEmptyFooterView() {
        return null;
    }

    @Override
    abstract public void getDataFromNet();

    @Override
    public boolean enableInitRefresh() {
        return true;
    }

    @Override
    public boolean isFirstRefresh() {
        return mSROpt.isFirstRefresh();
    }

    @Override
    abstract public IListResult<T> parseNetworkResponse(int id, String text) throws JSONException;

    @Override
    public void setOnScrollListener(OnScrollMixListener listener) {
        mSROpt.setOnScrollListener(listener);
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return true;
    }

    @Override
    public void setRefreshEnabled(boolean enabled) {
        mSROpt.setRefreshEnabled(enabled);
    }

    @Override
    public void stopLoadMore(boolean isSucceed) {
        mSROpt.stopLoadMore(isSucceed);
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mSROpt.isSwipeRefreshing();
    }

    @Override
    public void setAutoLoadMoreEnabled(boolean enabled) {
        mSROpt.setAutoLoadMoreEnabled(enabled);
    }

    @Override
    public int getOffset() {
        return mSROpt.getOffset();
    }

    @Override
    public String getLastItemId() {
        return mSROpt.getLastItemId();
    }

    @Override
    public int getLimit() {
        return AppEx.getListConfig().getLimit();
    }

    @Override
    public void onNetRefreshSuccess() {
        showView(getDecorView().getContentView());
    }

    @Override
    public void onNetRefreshError() {
        showView(getDecorView().getContentView());
    }

    @Override
    public boolean hideHeaderWhenInit() {
        return true;
    }

    @Override
    public boolean useErrorView() {
        return true;
    }

    @Override
    public int getInitOffset() {
        return AppEx.getListConfig().getInitOffset();
    }

   /* @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            return mSROpt.onRetryClick();
        }
        return true;
    }*/

    @Override
    public boolean onRetryClick() {
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast(Network.getConfig().getDisconnectToast());
            return true;
        }
        return false;
    }

    @Override
    @PageDownType
    public int getListPageDownType() {
        return AppEx.getListConfig().getType();
    }

    @Override
    public void enableLocalRefresh(boolean state) {
        mSROpt.setRefreshLocalState(state);
    }

    @Override
    public void onLocalRefreshSuccess() {
    }

    @Override
    public void onLocalRefreshError() {
    }

    @Override
    public List<T> onLocalTaskResponse() {
        return null;
    }

    @Override
    public void resetNetDataState() {
        mSROpt.resetNetDataState();
    }

    @Override
    public List<T> getNetData() {
        return mSROpt.getNetData();
    }

    @Override
    public void dialogRefresh() {
        super.dialogRefresh();
        mSROpt.dialogRefresh();
    }

    @Override
    public void embedRefresh() {
        super.embedRefresh();
        mSROpt.embedRefresh();
    }

    @Override
    public void swipeRefresh() {
        super.swipeRefresh();
        mSROpt.swipeRefresh();
    }

    @Override
    public void stopRefresh() {
        super.stopRefresh();
        mSROpt.stopRefresh();
    }

    @Override
    public void dismissLoadingDialog() {
        super.dismissLoadingDialog();
    }

    @Override
    public final void stopSwipeRefresh() {
        mSROpt.stopSwipeRefresh();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return mSROpt.onNetworkResponse(id, r, TAG);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (!isFinishing()) {
            mSROpt.onNetworkSuccess((IListResult) result);
        }
    }

    @Override
    public void refresh() {
        mSROpt.refresh();
    }

    @Override
    public void onSwipeRefreshAction() {
    }

    @Override
    public void onDataSetChanged() {
        mSROpt.onDataSetChanged();
    }
}
