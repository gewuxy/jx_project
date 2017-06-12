package lib.ys.ui.frag.list;

import android.support.annotation.CallSuper;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.R;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.network.model.interfaces.IListResult;
import lib.ys.ui.interfaces.listener.MixOnScrollListener;
import lib.ys.ui.interfaces.listener.list.SROptListener;
import lib.ys.ui.interfaces.opts.list.SROpt;


/**
 * 下拉刷新fragment
 *
 * @author yuansui
 */
abstract public class SRListFragEx<T, A extends IAdapter<T>> extends ListFragEx<T, A> implements SROptListener<T> {

    private SROpt<T> mSROpt = new SROpt<>(this, getListOpt());

    @Override
    public int getContentViewId() {
        return R.layout.sr_list_layout;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
    }

    @Override
    public int getListViewResId() {
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

        if (getInitRefreshWay() == RefreshWay.embed && enableRefreshWhenInit()) {
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
    public boolean enableRefreshWhenInit() {
        return true;
    }

    @Override
    public boolean isFirstRefresh() {
        return mSROpt.isFirstRefresh();
    }

    @Override
    abstract public IListResult<T> parseNetworkResponse(int id, String text) throws JSONException;

    @Override
    public void setOnScrollListener(MixOnScrollListener listener) {
        mSROpt.setOnScrollListener(listener);
    }

    @Override
    public boolean needDelayAddEmptyView() {
        return true;
    }

    @Override
    public void enableSRRefresh(boolean enable) {
        mSROpt.setRefreshEnable(enable);
    }

    @Override
    public void stopLoadMore() {
        mSROpt.stopLoadMore();
    }

    @Override
    public boolean isSwipeRefreshing() {
        return mSROpt.isSwipeRefreshing();
    }

    @Override
    public void enableAutoLoadMore(boolean enable) {
        mSROpt.enableAutoLoadMore(enable);
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
        return super.onRetryClick() || mSROpt.onRetryClick();
    }

    @Override
    @PageDownType
    public int getListPageDownType() {
        return ListConfig.getType();
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
        mSROpt.onNetworkSuccess((IListResult) result);
    }

    @Override
    public void refresh() {
        mSROpt.refresh();
    }

    @Override
    public void onDataSetChanged() {
        mSROpt.onDataSetChanged();
    }
}
