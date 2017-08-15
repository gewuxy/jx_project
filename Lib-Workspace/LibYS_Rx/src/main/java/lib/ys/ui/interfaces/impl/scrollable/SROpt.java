package lib.ys.ui.interfaces.impl.scrollable;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import lib.network.Network;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IListResult;
import lib.ys.AppEx;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.R;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.fitter.LayoutFitter;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.IScrollable;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnSROptListener;
import lib.ys.util.DeviceUtil;
import lib.ys.util.UtilEx;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;
import lib.ys.view.swipeRefresh.interfaces.OnSRListener;

/**
 * 下拉刷新和加载更多组件
 *
 * @author yuansui
 */
public class SROpt<T> implements OnSRListener {

    private static final String TAG = SROpt.class.getSimpleName();

    private OnSROptListener mSROptListener;
    private IScrollable<T> mScrollable;

    // 翻页标识
    private String mLastId = ListConstants.KDefaultInitLastId;
    private int mOffset;

    protected BaseSRLoadMoreLayout mSRLayout;

    private List<T> mTsNet;
    private List<T> mTsLocal;

    private boolean mLoadMore = false;

    private boolean mFirstRefresh = true;

    private boolean mRefreshLocal = false;

    private RelativeLayout mFooterEmptyView;

    private Handler mHandler;


    public SROpt(@NonNull OnSROptListener<T> l) {
        mSROptListener = l;
        mScrollable = l.getScrollable();

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (mRefreshLocal) {
                    refreshLocal();
                } else {
                    refresh();
                }
            }
        };

        mOffset = getInitOffset();
    }

    public void findViews(View contentView, @IdRes int srLayoutResId) {
        mSRLayout = (BaseSRLoadMoreLayout) contentView.findViewById(srLayoutResId);

        View footerEmpty = createFooterEmptyView();
        if (footerEmpty != null) {
            LayoutInflater inflater = LayoutInflater.from(AppEx.ct());
            View layoutFooterEmpty = inflater.inflate(R.layout.layout_list_footer_empty_container, null);
            mFooterEmptyView = (RelativeLayout) layoutFooterEmpty.findViewById(R.id.list_footer_empty_container);
            mFooterEmptyView.addView(footerEmpty, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(layoutFooterEmpty);
            mScrollable.addFooterView(layoutFooterEmpty);
        }
    }

    public void setViews() {
        mScrollable.hideFooterView();
        hideFooterEmptyView();

        setLoadMoreState(false);

        setSRListener(this);

        if (mSROptListener.enableInitRefresh()) {
            if (hideHeaderWhenInit()) {
                mScrollable.hideHeaderView();
            }
            mHandler.sendEmptyMessageDelayed(0, ResLoader.getInteger(R.integer.anim_default_duration));
        }
    }

    protected void setSRListener(OnSRListener lsn) {
        mSRLayout.setSRListener(lsn);
    }

    public void setOnScrollListener(OnScrollMixListener listener) {
        mSRLayout.setOnScrollListener(listener);
    }

    public void stopLoadMore(boolean isSucceed) {
        mSRLayout.stopLoadMore(isSucceed);
    }

    public boolean isSwipeRefreshing() {
        return mSRLayout.isSwipeRefreshing();
    }

    public void setAutoLoadMoreEnabled(boolean enabled) {
        mSRLayout.setAutoLoadMoreEnabled(enabled);
    }

    private void setLoadMoreState(boolean state) {
        mSRLayout.setLoadMoreState(state);
    }

    public void setRefreshEnabled(boolean enable) {
        mSRLayout.setRefreshEnabled(enable);
    }

    @Override
    public final void onSwipeRefresh() {
        // 保持调用顺序, 先通知外部处理再内部请求数据
        mSROptListener.onSwipeRefreshAction();
        processRefresh();
        mSROptListener.setRefreshWay(RefreshWay.swipe);
    }

    @Override
    public final void onSwipeRefreshFinish() {
        onRefreshFinish();
    }

    @Override
    public boolean onAutoLoadMore() {
        mLoadMore = true;
        if (DeviceUtil.isNetworkEnabled()) {
            mSROptListener.getDataFromNet();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onManualLoadMore() {
        mLoadMore = true;
        if (DeviceUtil.isNetworkEnabled()) {
            mSRLayout.startLoadMore();
            return true;
        } else {
            AppEx.showToast(Network.getConfig().getDisconnectToast());
            return false;
        }
    }

    /**
     * 获取本地数据
     */
    protected void getDataFromLocal() {
        mTsLocal = mSROptListener.onLocalTaskResponse();
        onLocalRefreshSuccess();
    }

    public Object onNetworkResponse(int id, NetworkResp nr, String tag) throws Exception {
        YSLog.d(tag, nr.getText());
        return mSROptListener.parseNetworkResponse(id, nr.getText());
    }

    public void onNetworkSuccess(IListResult response) {
        if (response == null || !response.isSucceed() || response.getData() == null) {
            // 表示数据列表返回为null, 解析失败
            stopRefresh();

            if (response != null) {
                AppEx.showToast(response.getMessage());
            }
            return;
        }

        mTsNet = response.getData();

        switch (getListPageDownType()) {
            case PageDownType.offset: {
                mOffset += mTsNet.size();
            }
            break;
            case PageDownType.page: {
                mOffset++;
            }
            break;
            case PageDownType.last_id: {
                mLastId = response.getLastId();
            }
            break;
            default:
                break;
        }

        if (mLoadMore) {
            // 分页加载
            mScrollable.addAll(mTsNet);
            mScrollable.invalidate();
            stopLoadMore(true);

            if (mTsNet.size() < getLimit()) {
                // 数据不够
                setLoadMoreState(false);
                mScrollable.showFooterView();
            }
            mLoadMore = false;
        } else {
            stopRefresh();
        }
    }

    public int getLimit() {
        return mSROptListener.getLimit();
    }

    public int getOffset() {
        return mOffset;
    }

    public String getLastItemId() {
        return mLastId;
    }

    public List<T> getNetData() {
        return mTsNet;
    }

    /**
     * 刷新
     */
    public void refresh() {
        if (mFirstRefresh) {
            mSROptListener.refresh(mSROptListener.getInitRefreshWay());
            mFirstRefresh = false;
            // 第一次刷新以后再添加empty view
            mScrollable.addEmptyViewIfNonNull();
        } else {
            mSROptListener.refresh(RefreshWay.swipe);
        }
    }

    /**
     * 重置网络状态
     */
    public void resetNetDataState() {
        mTsNet = null;
        mLoadMore = false;
        mOffset = getInitOffset();
        mLastId = ListConstants.KDefaultInitLastId;
    }

    private int getInitOffset() {
        return mSROptListener.getInitOffset();
    }

    private boolean hideHeaderWhenInit() {
        return mSROptListener.hideHeaderWhenInit();
    }

    private boolean useErrorView() {
        return mSROptListener.useErrorView();
    }

    public boolean onRetryClick() {
        mSROptListener.refresh(mSROptListener.getInitRefreshWay());
        return true;
    }

    @PageDownType
    private int getListPageDownType() {
        return mSROptListener.getListPageDownType();
    }

    /**
     * 加载本地数据相关----------------------
     */
    private void refreshLocal() {
        mRefreshLocal = true;
        refresh();
    }

    public void setRefreshLocalState(boolean state) {
        mRefreshLocal = state;
    }

    public void dialogRefresh() {
        mScrollable.hideFooterView();
        processRefresh();
    }

    public void embedRefresh() {
        processRefresh();
    }

    public void swipeRefresh() {
        /**
         * 下拉的处理时机不同, {@link #onSwipeRefresh(boolean)}
         */
        mSRLayout.startRefresh();
    }

    public void stopSwipeRefresh() {
        mSRLayout.stopRefresh();
    }

    private void processRefresh() {
        resetNetDataState();
        if (mRefreshLocal) {
            getDataFromLocal();
        } else {
            if (DeviceUtil.isNetworkEnabled()) {
                mSROptListener.getDataFromNet();
            } else {
                AppEx.showToast(Network.getConfig().getDisconnectToast());
                stopRefresh();
            }
        }
    }

    public void stopRefresh() {
        /**
         * swipe refresh使用单独的处理方式, 为了头部的下拉刷新回去以后才刷新数据, 体验友好
         * @see {@link #onSwipeRefreshFinish()}
         */
        if (mSROptListener.getRefreshWay() != RefreshWay.swipe) {
            if (mSROptListener.getRefreshWay() == RefreshWay.dialog) {
                mSROptListener.dismissLoadingDialog();
            }
            UtilEx.runOnUIThread(() -> onRefreshFinish());
        } else {
            stopSwipeRefresh();
        }
    }

    /**
     * 刷新动作完成, 判断数据的展示形式
     */
    private void onRefreshFinish() {
        if (mRefreshLocal) {
            if (mTsLocal != null) {
                onLocalRefreshSuccess();
            } else {
                onLocalRefreshError();
            }
        } else {
            if (mTsNet != null) {
                onNetRefreshSuccess();
            } else {
                onNetRefreshError();
            }
        }
    }

    /**
     * 网络数据刷新成功
     */
    private void onNetRefreshSuccess() {
        YSLog.d(TAG, "onNetRefreshSuccess()");

        mSROptListener.setViewState(ViewState.normal);

        mScrollable.setData(mTsNet);
        mScrollable.invalidate();

        mScrollable.showHeaderView();

        // size < limit 表示没有更多数据了
        if (mTsNet.size() >= getLimit()) {
            setLoadMoreState(mSRLayout.isAutoLoadMoreEnabled());
        } else {
            setLoadMoreState(false);
        }

        mSROptListener.onNetRefreshSuccess();
    }

    /**
     * 网络数据刷新错误
     */
    protected void onNetRefreshError() {
        YSLog.d(TAG, "onNetRefreshError()");

        if (useErrorView() && mScrollable.isEmpty()) {
            mSROptListener.setViewState(ViewState.error);
        } else {
            mSROptListener.setViewState(ViewState.normal);
            if (mLoadMore) {
                stopLoadMore(false);
            } else {
                stopLoadMore(true);
                setLoadMoreState(false);

                if (mSROptListener.getRefreshWay() == RefreshWay.swipe) {
                    stopSwipeRefresh();
                }

                if (mFooterEmptyView == null) {
                    mScrollable.showFooterView();
                }
            }
        }

        mSROptListener.onNetRefreshError();
    }

    private void onLocalRefreshSuccess() {
        YSLog.d(TAG, "onLocalRefreshSuccess()");

        mSROptListener.setViewState(ViewState.normal);

        mScrollable.setData(mTsLocal);
        mScrollable.invalidate();

        mScrollable.showHeaderView();
        mScrollable.hideFooterView();

        mSROptListener.onLocalRefreshSuccess();
    }

    private void onLocalRefreshError() {
        YSLog.d(TAG, "onLocalRefreshError()");

        if (useErrorView() && mScrollable.isEmpty()) {
            mSROptListener.setViewState(ViewState.error);
        } else {
            mSROptListener.setViewState(ViewState.normal);
        }

        mSROptListener.onLocalRefreshError();
    }

    private View createFooterEmptyView() {
        return mSROptListener.createEmptyFooterView();
    }

    private void showFooterEmptyView() {
        if (mFooterEmptyView != null) {
            ViewUtil.showView(mFooterEmptyView);
        }
    }

    private void hideFooterEmptyView() {
        if (mFooterEmptyView != null) {
            ViewUtil.goneView(mFooterEmptyView);
        }
    }

    private void determineFooterStatus() {
        if ((mTsNet == null || mTsNet.isEmpty())
                && mScrollable.isEmpty()) {
            mScrollable.hideFooterView();
            showFooterEmptyView();
        } else {
            if (mTsNet == null || mTsNet.size() >= getLimit()) {
                mScrollable.hideFooterView();
            } else {
                mScrollable.showFooterView();
            }
            hideFooterEmptyView();
        }
    }

    public void onDataSetChanged() {
        determineFooterStatus();
    }

    public boolean isFirstRefresh() {
        return mFirstRefresh;
    }
}
