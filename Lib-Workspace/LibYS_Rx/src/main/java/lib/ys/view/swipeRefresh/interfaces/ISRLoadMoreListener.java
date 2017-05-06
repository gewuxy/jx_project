package lib.ys.view.swipeRefresh.interfaces;

/**
 * @author yuansui
 */
public interface ISRLoadMoreListener {

    /**
     * 是否正在加载更多
     *
     * @return
     */
    boolean isLoadingMore();

    /**
     * 停止加载更多, 重置footer
     */
    void stopLoadMore();

    /**
     * 开始底部load more
     */
    void startLoadMore();

    /**
     * 底部显示加载更多失败
     */
    void stopLoadMoreFailed();

    /**
     * 设置是否能自动加载更多
     *
     * @param enable
     */
    void setAutoLoadEnable(boolean enable);
}
