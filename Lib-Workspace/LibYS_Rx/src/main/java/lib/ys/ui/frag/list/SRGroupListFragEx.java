package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.network.model.NetworkResponse;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.R;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.interfaces.opts.list.SROpt;
import lib.ys.ui.interfaces.opts.impl.list.SROptImpl;
import lib.ys.ui.interfaces.MixOnScrollListener;

/**
 * 下拉刷新floating group listview fragment
 */
abstract public class SRGroupListFragEx<T> extends GroupListFragEx<T> implements SROpt {

    private SROptImpl<T> mSROptImpl = new SROptImpl<T>(this, getListWidget());

    @Override
    public int getContentViewId() {
        return R.layout.sr_group_list_layout;
    }

    @Override
    public int getListViewResId() {
        return R.id.sr_group_list_view;
    }

    @Override
    public int getSRLayoutResId() {
        return R.id.sr_group_list_layout;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();
        mSROptImpl.findViews(getDecorView(), getSRLayoutResId());
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mSROptImpl.setViews();

        if (getInitRefreshWay() == RefreshWay.embed) {
            // 为了更好的体验, 在embed loading显示之前先隐藏掉
            hideView(getDecorView().getContentView());
        }
    }

    @Override
    public View getFooterEmptyView() {
        return null;
    }

    @Override
    abstract public void getDataFromNet();

    @Override
    public boolean canAutoRefresh() {
        return true;
    }

    @Override
    public boolean isFirstRefresh() {
        return mSROptImpl.isFirstRefresh();
    }

    @Override
    public IListResponse<T> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mSROptImpl.setOnScrollListener(listener);
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return true;
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        mSROptImpl.setRefreshEnable(enable);
    }

    @Override
    public void stopLoadMore() {
        mSROptImpl.stopLoadMore();
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mSROptImpl.isSwipeRefreshing();
    }

    @Override
    public void setAutoLoadEnable(boolean enable) {
        mSROptImpl.setAutoLoadEnable(enable);
    }

    @Override
    public int getOffset() {
        return mSROptImpl.getOffset();
    }

    @Override
    public String getLastItemId() {
        return mSROptImpl.getLastItemId();
    }

    @Override
    public int getLimit() {
        return ListConstants.KDefaultLimit;
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
        return ListConstants.KDefaultInitOffset;
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            return mSROptImpl.onRetryClick();
        }
        return true;
    }

    @Override
    @PageDownType
    public int getListPageDownType() {
        return ListConfig.getType();
    }

    @Override
    public void setRefreshLocalState(boolean state) {
        mSROptImpl.setRefreshLocalState(state);
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
        mSROptImpl.resetNetDataState();
    }

    @Override
    public List<T> getNetData() {
        return mSROptImpl.getNetData();
    }

    @Override
    public void dialogRefresh() {
        super.dialogRefresh();
        mSROptImpl.dialogRefresh();
    }

    @Override
    public void embedRefresh() {
        super.embedRefresh();
        mSROptImpl.embedRefresh();
    }

    @Override
    public void swipeRefresh() {
        super.swipeRefresh();
        mSROptImpl.swipeRefresh();
    }

    @Override
    public void stopRefresh() {
        super.stopRefresh();
        mSROptImpl.stopRefresh();
    }

    @Override
    public void dismissLoadingDialog() {
        super.dismissLoadingDialog();
    }

    @Override
    public final void stopSwipeRefresh() {
        mSROptImpl.stopSwipeRefresh();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return mSROptImpl.onNetworkResponse(id, nr, TAG);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        mSROptImpl.onNetworkSuccess((IListResponse) result);
    }

    @Override
    public void refresh() {
        mSROptImpl.refresh();
    }

    @Override
    public void onDataSetChanged() {
        mSROptImpl.onDataSetChanged();
    }

}
