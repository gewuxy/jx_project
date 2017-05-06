package lib.ys.view.swipeRefresh.interfaces;

public interface ISRCtrl {

    /**
     * 自动下拉刷新
     */
    void startRefresh();

    /**
     * 停止刷新
     */
    void stopRefresh();

    void setSRListener(ISRListener lsn);

    /**
     * 是否正在下拉刷新
     *
     * @return
     */
    boolean isSwipeRefreshing();
}
