package lib.ys.view.swipeRefresh.interfaces;

public interface OnSRListener {

    /**
     * 下拉刷新
     */
    void onSwipeRefresh();

    /**
     * 结束下拉刷新
     */
    void onSwipeRefreshFinish();

    /**
     * 滑到底部自动加载更多
     *
     * @return
     */
    boolean onAutoLoadMore();

    /**
     * 手动点击加载更多(在网络失败后点重试)
     *
     * @return
     */
    boolean onManualLoadMore();

}
