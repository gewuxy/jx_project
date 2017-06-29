package lib.ys.ui.interfaces.opt;


import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 网络操作
 *
 * @author yuansui
 */
public interface INetworkOpt {

    int KDefaultId = -1;

    /**
     * 默认使用{@link #KDefaultId}
     *
     * @param req
     */
    void exeNetworkReq(NetworkReq req);

    /**
     * @param id
     * @param req
     */
    void exeNetworkReq(int id, NetworkReq req);

    /**
     * 可以自行设置重试次数及超时时间, 多用于一些需要不断重试的任务
     *
     * @param id
     * @param req
     * @param l
     */
    void exeNetworkReq(int id, NetworkReq req, OnNetworkListener l);

    /**
     * 执行web socket的请求
     *
     * @param req
     * @param l
     * @return
     */
    WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l);

    /**
     * 取消所有网络任务
     */
    void cancelAllNetworkReq();

    void cancelNetworkReq(int id);
}
