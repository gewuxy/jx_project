package lib.ys.widget.list;

import android.support.annotation.StringRes;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.config.ListConfig;
import lib.ys.config.ListConfig.PageDownType;
import lib.ys.decor.DecorViewEx.ViewState;
import lib.ys.network.result.IListResponse;

/**
 * 下拉刷新组件
 *
 * @author yuansui
 */
public interface OnSRWidgetListener<T> {

    int getSRLayoutResId();

    void refresh();

    boolean isFirstRefresh();

    View getFooterEmptyView();

    String getLastItemId();

    /**
     * 是否刷新数据的时候加载本地数据
     *
     * @param state
     */
    void setRefreshLocalState(boolean state);

    /**
     * 翻页规则, 默认使用{@link ListConfig#getType()}, 可更改
     *
     * @return
     */
    @PageDownType
    int getListPageDownType();

    void setViewState(@ViewState int state);

    void refresh(@RefreshWay int way);

    @RefreshWay
    int getRefreshWay();

    @RefreshWay
    int getInitRefreshWay();

    void setRefreshWay(@RefreshWay int way);

    void showToast(@StringRes int... resId);

    void showToast(String content);

    /**
     * 是否允许界面初始化时自动加载网络数据
     *
     * @return
     */
    boolean canAutoRefresh();

    void getDataFromNet();

    void dismissLoadingDialog();

    /**
     * 解析从网络获取回来的数据
     *
     * @param text
     * @return 解析结构体BaseResponse
     */
    IListResponse<T> parseNetworkResponse(int what, String text) throws JSONException;

    void stopLoadMore();

    boolean isSwipeRefreshing();

    void setAutoLoadEnable(boolean enable);

    void setRefreshEnable(boolean enable);

    int getOffset();

    /**
     * 获取翻页个数限制
     *
     * @return
     */
    int getLimit();

    /**
     * 网络数据刷新成功
     */
    void onNetRefreshSuccess();

    /**
     * 网络数据刷新错误
     */
    void onNetRefreshError();

    /**
     * 是否在初始化后第一次获取数据时, 无数据后切换到faildView, 部分设计不允许切换
     *
     * @return
     */
    boolean useErrorView();

    /**
     * 获取初始的偏移, 0或1
     *
     * @return
     */
    int getInitOffset();

    /**
     * 获取初始数据的时候是否隐藏header view
     *
     * @return
     */
    boolean hideHeaderWhenInit();

    /**
     * 加载本地任务
     *
     * @return 加载成功的本地数据, 返回null表示失败
     */
    List<T> onLocalTaskResponse();

    /**
     * 本地数据刷新成功
     */
    void onLocalRefreshSuccess();

    /**
     * 本地数据刷新错误
     */
    void onLocalRefreshError();

    /**
     * 重置网络状态
     */
    void resetNetDataState();

    List<T> getNetData();
}
