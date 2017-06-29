package lib.ys.ui.interfaces.opt;


import lib.ys.config.AppConfig.RefreshWay;

/**
 * 刷新操作
 *
 * @author yuansui
 */
public interface IRefreshOpt {

    /**
     * 进行刷新
     *
     * @param style
     */
    void refresh(@RefreshWay int style);

    /**
     * dialog刷新
     */
    void dialogRefresh();

    /**
     * 内嵌式刷新
     */
    void embedRefresh();

    /**
     * 下拉刷新
     */
    void swipeRefresh();

    /**
     * 取消刷新
     */
    void stopRefresh();

    void stopDialogRefresh();

    /**
     * 不知道是否要显示错误页面还是已经获取到正确的数据显示, 需要处理的子类自行处理
     */
    void stopEmbedRefresh();

    void stopSwipeRefresh();

}
