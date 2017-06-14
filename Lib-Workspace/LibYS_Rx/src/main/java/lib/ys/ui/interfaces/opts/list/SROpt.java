package lib.ys.ui.interfaces.opts.list;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.AppEx;
import lib.ys.ConstantsEx.ListConstants;
import lib.ys.YSLog;
import lib.ys.R;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.fitter.LayoutFitter;
import lib.network.model.interfaces.IListResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.interfaces.listener.MixOnScrollListener;
import lib.ys.ui.interfaces.listener.list.MixScrollOpt;
import lib.ys.ui.interfaces.listener.list.SROptListener;
import lib.ys.util.DeviceUtil;
import lib.ys.util.UtilEx;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;
import lib.ys.view.swipeRefresh.interfaces.ISRListener;

/**
 * 下拉刷新和加载更多组件
 *
 * @author yuansui
 */
public class SROpt<T> implements ISRListener {

    private static final String TAG = SROpt.class.getSimpleName();

    private SROptListener mSROptListener;
    private MixScrollOpt<T> mScrollOpt;

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


    public SROpt(@NonNull SROptListener<T> l, MixScrollOpt<T> scrollOpt) {
        mSROptListener = l;
        mScrollOpt = scrollOpt;

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
            mScrollOpt.addFooterView(layoutFooterEmpty);
        }
    }

    public void setViews() {
        mScrollOpt.hideFooterView();
        hideFooterEmptyView();

        enableAutoLoadMore(false);
        setSRListener(this);

        if (mSROptListener.enableRefreshWhenInit()) {
            if (hideHeaderWhenInit()) {
                mScrollOpt.hideHeaderView();
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

    public void enableAutoLoadMore(boolean enable) {
        mSRLayout.enableAutoLoadMore(enable);
    }

    public void setRefreshEnable(boolean enable) {
        mSRLayout.setRefreshEnable(enable);
    }

    @Override
    public final void onSwipeRefresh() {
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
        if (DeviceUtil.isNetworkEnable()) {
            mSROptListener.getDataFromNet();
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
            mSROptListener.showToast(R.string.toast_network_disconnect);
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
                mSROptListener.showToast(response.getError());
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
            case PageDownType.last_id: {
                mLastId = response.getLastId();
            }
            break;
            default:
                break;
        }

        if (mLoadMore) {
            // 分页加载
            mScrollOpt.addAll(mTsNet);
            mScrollOpt.invalidate();
            stopLoadMore();

            if (mTsNet.size() < getLimit()) {
                // 数据不够
                enableAutoLoadMore(false);
                mScrollOpt.showFooterView();
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
            mScrollOpt.addEmptyViewIfNoNull();
        } else {
            mSROptListener.refresh(RefreshWay.swipe);
        }
    }

    /**
     * 重置网络状态
     */
    public void resetNetDataState() {
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
    private int getListPageUpType() {
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
        mScrollOpt.hideFooterView();
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
                mSROptListener.getDataFromNet();
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

        mScrollOpt.setData(mTsNet);
        mScrollOpt.invalidate();

        mScrollOpt.showHeaderView();

        // size < limit 表示没有更多数据了
        if (mTsNet.size() >= getLimit()) {
            enableAutoLoadMore(mSRLayout.isAutoLoadMoreEnabled() & true);
        } else {
            enableAutoLoadMore(false);
        }
//        determineFooterStatus();

        mSROptListener.onNetRefreshSuccess();
    }

    /**
     * 网络数据刷新错误
     */
    protected void onNetRefreshError() {
        YSLog.d(TAG, "onNetRefreshError()");

        if (useErrorView() && mScrollOpt.isEmpty()) {
            mSROptListener.setViewState(ViewState.error);
        } else {
            mSROptListener.setViewState(ViewState.normal);
            if (mLoadMore) {
                stopLoadMoreFailed();
            } else {
                if (mFooterEmptyView == null) {
                    enableAutoLoadMore(false);
                    stopLoadMore();
                    mScrollOpt.showFooterView();
                } else {
                    enableAutoLoadMore(false);
                    stopLoadMore();
                    /**
                     * TODO: 已经监听了数据的变化, 暂时注释掉这里, 可能会出现问题
                     * {@link #onDataSetChanged()}
                     */
//                    determineFooterStatus();
                }
            }
        }

        mSROptListener.onNetRefreshError();
    }

    private void onLocalRefreshSuccess() {
        YSLog.d(TAG, "onLocalRefreshSuccess()");

        mSROptListener.setViewState(ViewState.normal);

        mScrollOpt.setData(mTsLocal);
        mScrollOpt.invalidate();

        mScrollOpt.showHeaderView();
        mScrollOpt.hideFooterView();

        mSROptListener.onLocalRefreshSuccess();
    }

    private void onLocalRefreshError() {
        YSLog.d(TAG, "onLocalRefreshError()");

        if (useErrorView() && mScrollOpt.isEmpty()) {
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
                && mScrollOpt.isEmpty()) {
            mScrollOpt.hideFooterView();
            showFooterEmptyView();
        } else {
            if (mTsNet == null || mTsNet.size() >= getLimit()) {
                mScrollOpt.hideFooterView();
            } else {
                mScrollOpt.showFooterView();
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
