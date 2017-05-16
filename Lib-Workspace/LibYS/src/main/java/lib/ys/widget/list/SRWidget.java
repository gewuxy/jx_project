package lib.ys.widget.list;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import lib.network.model.NetworkResponse;
import lib.ys.AppEx;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.LogMgr;
import lib.ys.R;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.decor.DecorViewEx.ViewState;
import lib.ys.fitter.LayoutFitter;
import lib.ys.network.result.IListResponse;
import lib.ys.util.DeviceUtil;
import lib.ys.util.UtilEx;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;
import lib.ys.view.swipeRefresh.interfaces.ISRListener;
import lib.ys.widget.list.mix.IMixScrollWidget;
import lib.ys.widget.list.mix.MixOnScrollListener;

/**
 * 下拉刷新和加载更多组件
 *
 * @author yuansui
 */
public class SRWidget<T> implements ISRListener {

    private static final String TAG = SRWidget.class.getSimpleName();

    private OnSRWidgetListener mListener;
    private IMixScrollWidget<T> mWidget;

    // 翻页标识
    private String mLastItemId = ListConstants.KDefaultInitItemId;
    private int mOffset;

    protected BaseSRLoadMoreLayout mSRLayout;

    private List<T> mTsNet;
    private List<T> mTsLocal;

    private boolean mLoadMore = false;

    private boolean mFirstRefresh = true;

    private boolean mRefreshLocal = false;

    private RelativeLayout mFooterEmptyView;

    private Handler mHandler;


    public SRWidget(@NonNull OnSRWidgetListener<T> listener, IMixScrollWidget<T> widget) {
        mListener = listener;
        mWidget = widget;

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
            mWidget.addFooterView(layoutFooterEmpty);
        }
    }

    public void setViews() {
        mWidget.hideFooterView();
        hideFooterEmptyView();

        setAutoLoadEnable(false);
        setSRListener(this);

        if (mListener.canAutoRefresh()) {
            if (hideHeaderWhenInit()) {
                mWidget.hideHeaderView();
            }
            mHandler.sendEmptyMessageDelayed(0, ResLoader.getInteger(R.integer.anim_default_duration));
        }
    }

    protected void setSRListener(ISRListener lsn) {
        mSRLayout.setSRListener(lsn);
    }

    public void setOnScrollListener(MixOnScrollListener listener) {
        mSRLayout.setOnScrollListener(listener);
    }

    public void stopLoadMore() {
        mSRLayout.stopLoadMore();
    }

    private void stopLoadMoreFailed() {
        mSRLayout.stopLoadMoreFailed();
    }

    public boolean isSwipeRefreshing() {
        return mSRLayout.isSwipeRefreshing();
    }

    public void setAutoLoadEnable(boolean enable) {
        mSRLayout.setAutoLoadEnable(enable);
    }

    public void setRefreshEnable(boolean enable) {
        mSRLayout.setRefreshEnable(enable);
    }

    @Override
    public final void onSwipeRefresh() {
        processRefresh();
        mListener.setRefreshWay(RefreshWay.swipe);
    }

    @Override
    public final void onSwipeRefreshFinish() {
        onRefreshFinish();
    }

    @Override
    public boolean onAutoLoadMore() {
        mLoadMore = true;
        if (DeviceUtil.isNetworkEnable()) {
            mListener.getDataFromNet();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onManualLoadMore() {
        mLoadMore = true;
        if (DeviceUtil.isNetworkEnable()) {
            mSRLayout.startLoadMore();
            return true;
        } else {
            mListener.showToast(R.string.toast_network_disconnect);
            return false;
        }
    }

    /**
     * 获取本地数据
     */
    protected void getDataFromLocal() {
        mTsLocal = mListener.onLocalTaskResponse();
        onLocalRefreshSuccess();
    }

    public Object onNetworkResponse(int id, NetworkResponse nr, String tag) throws Exception {
        LogMgr.d(tag, nr.getText());
        return mListener.parseNetworkResponse(id, nr.getText());
    }

    public void onNetworkSuccess(IListResponse response) {
        if (response == null || !response.isSucceed() || response.getData() == null) {
            // 表示数据列表返回为null, 解析失败
            stopRefresh();

            if (response != null) {
                mListener.showToast(response.getError());
            }
            return;
        }

        mTsNet = response.getData();

        switch (getListPageUpType()) {
            case PageDownType.offset: {
                mOffset += mTsNet.size();
            }
            break;
            case PageDownType.page: {
                mOffset++;
            }
            break;
            case PageDownType.last_item_id: {
                mLastItemId = response.getLastItemId();
            }
            break;
            default:
                break;
        }

        if (mLoadMore) {
            // 分页加载
            mWidget.addAll(mTsNet);
            mWidget.invalidate();
            stopLoadMore();

            if (mTsNet.size() < getLimit()) {
                // 数据不够
                setAutoLoadEnable(false);
                mWidget.showFooterView();
            }
            mLoadMore = false;
        } else {
            stopRefresh();
        }
    }

    public int getLimit() {
        return mListener.getLimit();
    }

    public int getOffset() {
        return mOffset;
    }

    public String getLastItemId() {
        return mLastItemId;
    }

    public List<T> getNetData() {
        return mTsNet;
    }

    /**
     * 刷新
     */
    public void refresh() {
        if (mFirstRefresh) {
            mListener.refresh(mListener.getInitRefreshWay());
            mFirstRefresh = false;
            // 第一次刷新以后再添加empty view
            mWidget.addEmptyViewIfNoNull();
        } else {
            mListener.refresh(RefreshWay.swipe);
        }
    }

    /**
     * 重置网络状态
     */
    public void resetNetDataState() {
        mLoadMore = false;
        mOffset = getInitOffset();
        mLastItemId = ListConstants.KDefaultInitItemId;
    }

    private int getInitOffset() {
        return mListener.getInitOffset();
    }

    private boolean hideHeaderWhenInit() {
        return mListener.hideHeaderWhenInit();
    }

    private boolean useErrorView() {
        return mListener.useErrorView();
    }

    public boolean onRetryClick() {
        mListener.refresh(mListener.getInitRefreshWay());
        return true;
    }

    @PageDownType
    private int getListPageUpType() {
        return mListener.getListPageDownType();
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
        mWidget.hideFooterView();
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
            if (DeviceUtil.isNetworkEnable()) {
                mListener.getDataFromNet();
            } else {
                AppEx.showToast(R.string.toast_network_disconnect);
                stopRefresh();
            }
        }
    }

    public void stopRefresh() {
        /**
         * swipe refresh使用单独的处理方式, 为了头部的下拉刷新回去以后才刷新数据, 体验友好
         * @see {@link #onSwipeRefreshFinish()}
         */
        if (mListener.getRefreshWay() != RefreshWay.swipe) {
            if (mListener.getRefreshWay() == RefreshWay.dialog) {
                mListener.dismissLoadingDialog();
            }
            UtilEx.runOnUIThread(new Runnable() {

                @Override
                public void run() {
                    onRefreshFinish();
                }
            });
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
        LogMgr.d(TAG, "onNetRefreshSuccess()");

        mListener.setViewState(ViewState.normal);

        mWidget.setData(mTsNet);
        mWidget.invalidate();

        mWidget.showHeaderView();

        // size < limit 表示没有更多数据了
        if (mTsNet.size() >= getLimit()) {
            setAutoLoadEnable(true);
        } else {
            setAutoLoadEnable(false);
        }
//        determineFooterStatus();

        mListener.onNetRefreshSuccess();
    }

    /**
     * 网络数据刷新错误
     */
    protected void onNetRefreshError() {
        LogMgr.d(TAG, "onNetRefreshError()");

        if (useErrorView() && mWidget.isEmpty()) {
            mListener.setViewState(ViewState.error);
        } else {
            mListener.setViewState(ViewState.normal);
            if (mLoadMore) {
                stopLoadMoreFailed();
            } else {
                if (mFooterEmptyView == null) {
                    setAutoLoadEnable(false);
                    stopLoadMore();
                    mWidget.showFooterView();
                } else {
                    setAutoLoadEnable(false);
                    stopLoadMore();
                    /**
                     * TODO: 已经监听了数据的变化, 暂时注释掉这里, 可能会出现问题
                     * {@link #onDataSetChanged()}
                     */
//                    determineFooterStatus();
                }
            }
        }

        mListener.onNetRefreshError();
    }

    private void onLocalRefreshSuccess() {
        LogMgr.d(TAG, "onLocalRefreshSuccess()");

        mListener.setViewState(ViewState.normal);

        mWidget.setData(mTsLocal);
        mWidget.invalidate();

        mWidget.showHeaderView();
        mWidget.hideFooterView();

        mListener.onLocalRefreshSuccess();
    }

    private void onLocalRefreshError() {
        LogMgr.d(TAG, "onLocalRefreshError()");

        if (useErrorView() && mWidget.isEmpty()) {
            mListener.setViewState(ViewState.error);
        } else {
            mListener.setViewState(ViewState.normal);
        }

        mListener.onLocalRefreshError();
    }

    private View createFooterEmptyView() {
        return mListener.getFooterEmptyView();
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
                && mWidget.isEmpty()) {
            mWidget.hideFooterView();
            showFooterEmptyView();
        } else {
            if (mTsNet == null || mTsNet.size() >= getLimit()) {
                mWidget.hideFooterView();
            } else {
                mWidget.showFooterView();
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
