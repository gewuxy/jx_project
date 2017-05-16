package lib.ys.activity.list;

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
import lib.ys.network.result.IListResponse;
import lib.ys.widget.list.OnSRWidgetListener;
import lib.ys.widget.list.SRWidget;
import lib.ys.widget.list.mix.MixOnScrollListener;


abstract public class SRGroupListActivityEx<T> extends GroupListActivityEx<T> implements OnSRWidgetListener {

    private SRWidget<T> mSRWidget = new SRWidget<T>(this, getListWidget());

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
        mSRWidget.findViews(getDecorView(), getSRLayoutResId());
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mSRWidget.setViews();

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
        return mSRWidget.isFirstRefresh();
    }

    @Override
    abstract public IListResponse<T> parseNetworkResponse(int id, String text) throws JSONException;

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mSRWidget.setOnScrollListener(listener);
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return true;
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        mSRWidget.setRefreshEnable(enable);
    }

    @Override
    public void stopLoadMore() {
        mSRWidget.stopLoadMore();
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mSRWidget.isSwipeRefreshing();
    }

    @Override
    public void setAutoLoadEnable(boolean enable) {
        mSRWidget.setAutoLoadEnable(enable);
    }

    @Override
    public int getOffset() {
        return mSRWidget.getOffset();
    }

    @Override
    public String getLastItemId() {
        return mSRWidget.getLastItemId();
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
            return mSRWidget.onRetryClick();
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
        mSRWidget.setRefreshLocalState(state);
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
        mSRWidget.resetNetDataState();
    }

    @Override
    public List<T> getNetData() {
        return mSRWidget.getNetData();
    }

    @Override
    public void dialogRefresh() {
        super.dialogRefresh();
        mSRWidget.dialogRefresh();
    }

    @Override
    public void embedRefresh() {
        super.embedRefresh();
        mSRWidget.embedRefresh();
    }

    @Override
    public void swipeRefresh() {
        super.swipeRefresh();
        mSRWidget.swipeRefresh();
    }

    @Override
    public void stopRefresh() {
        super.stopRefresh();
        mSRWidget.stopRefresh();
    }

    @Override
    public void dismissLoadingDialog() {
        super.dismissLoadingDialog();
    }

    @Override
    public final void stopSwipeRefresh() {
        mSRWidget.stopSwipeRefresh();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        return mSRWidget.onNetworkResponse(id, nr, TAG);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        mSRWidget.onNetworkSuccess((IListResponse) result);
    }

    @Override
    public void refresh() {
        mSRWidget.refresh();
    }

    @Override
    public void onDataSetChanged() {
        mSRWidget.onDataSetChanged();
    }
}
