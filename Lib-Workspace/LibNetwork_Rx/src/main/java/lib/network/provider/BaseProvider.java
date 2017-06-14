package lib.network.provider;

import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author yuansui
 */
abstract public class BaseProvider {

    protected String TAG = getClass().getSimpleName();

    private Object mTag;

    public BaseProvider(Object tag) {
        mTag = tag;
    }

    protected Object tag() {
        return mTag;
    }

    abstract public void load(NetworkReq networkReq, int id, OnNetworkListener lsn);

    /**
     * 开启一个web socket
     *
     * @param req
     * @param lsn 暂时先使用ok的listener
     */
    abstract public WebSocket loadWebSocket(NetworkReq req, WebSocketListener lsn);

    abstract public void cancel(int id);

    abstract public void cancelAll();
}
