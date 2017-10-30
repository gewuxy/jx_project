package lib.yy.contract;

import lib.network.model.NetworkReq;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
public interface BaseContract {

    interface BaseView {
    }

    interface BasePresenter {

        void exeNetworkReq(NetworkReq req);

        void exeNetworkReq(int id ,NetworkReq req);

        WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l);

        void onDestroy();
    }

}
