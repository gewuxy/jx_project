package lib.ys.view.swipeRefresh.interfaces;

/**
 * 加载更多监听
 *
 * @author yuansui
 */
public interface SRLoadMoreOpt {

    /**
     * 是否正在加载更多
     *
     * @return
     */
    boolean isLoadingMore();

    /**
     * 停止加载更多, 重置footer
     *
     * @param isSucceed
     */
    void stopLoadMore(boolean isSucceed);

    /**
     * 开始底部load more
     */
    void startLoadMore();

    /**
     * 设置是否开启滑动底部自动加载更多
     *
     * @param enable
     */
    void enableAutoLoadMore(boolean enable);

    void setLoadMoreState(boolean state);
}
