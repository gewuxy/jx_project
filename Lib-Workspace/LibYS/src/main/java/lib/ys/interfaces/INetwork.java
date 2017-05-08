package lib.ys.interfaces;


import lib.network.model.NetworkRequest;
import lib.network.model.OnNetworkListener;

/**
 * 网络操作
 *
 * @author yuansui
 */
public interface INetwork extends OnNetworkListener {

    /**
     * @param id
     * @param request
     */
    void exeNetworkRequest(int id, NetworkRequest request);

    /**
     * 可以自行设置重试次数及超时时间, 多用于一些需要不断重试的任务
     *
     * @param id
     * @param request
     * @param listener
     */
    void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener);

    /**
     * 取消所有网络任务
     */
    void cancelAllNetworkRequest();

    void cancelNetworkRequest(int id);
}
