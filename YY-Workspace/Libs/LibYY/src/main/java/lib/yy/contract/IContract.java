package lib.yy.contract;

import lib.network.model.NetworkReq;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
public interface IContract {

    interface View {
        /**
         * 停止网络刷新
         */
        void onStopRefresh();
    }

    interface Presenter<V extends View> {

        V getView();

        void exeNetworkReq(NetworkReq req);

        void exeNetworkReq(int id ,NetworkReq req);

        WebSocket exeWebSocketReq(NetworkReq req, WebSocketListener l);

        void onDestroy();
    }

}
