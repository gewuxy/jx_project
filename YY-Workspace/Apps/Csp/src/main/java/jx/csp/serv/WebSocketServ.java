package jx.csp.serv;

import android.content.Intent;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.network.model.NetworkReq;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.ys.util.DeviceUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 直播、录播WebSocket的服务
 *
 * @author CaiXiang
 * @since 2017/10/31
 */
@Route
public class WebSocketServ extends ServiceEx implements OnLiveNotify {

    private static final int KWebSocketCloseNormal = 1000; //  1000表示正常关闭

    @Arg
    String mWsUrl;

    private WebSocket mWebSocket;
    private boolean mWsSuccess = false; // WebSocket连接是否成功

    @Override
    public void onCreate() {
        super.onCreate();
        LiveNotifier.inst().add(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWsUrl).build(), new WebSocketLink());
    }

    // 接收通知 发送websocket指令
    @Override
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        switch (type) {
            case LiveNotifyType.send_msg: {
                boolean b = mWebSocket.send((String) data);
                YSLog.d(TAG, "WebSocket.send msg = " + data);
                YSLog.d(TAG, "WebSocket.send state " + b);
            }
            break;
        }
    }

    public void notify(@LiveNotifyType int type, Object data) {
        LiveNotifier.inst().notify(type, data);
    }

    public void notify(@LiveNotifyType int type) {
        LiveNotifier.inst().notify(type);
    }

    @Override
    public void onDestroy() {
        if (mWebSocket != null) {
            mWebSocket.close(KWebSocketCloseNormal, "close");
            YSLog.d(TAG, "close WebSocket");
        }
        LiveNotifier.inst().remove(this);
        super.onDestroy();
    }

    public class WebSocketLink extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
            mWsSuccess = true; // 连接成功
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "WebSocket onMessage receiver String = " + text);
            try {
                JSONObject ob = new JSONObject(text);
                int order = ob.optInt("order");
                switch (order) {
                    case WsOrderType.sync: {
                        String from = ob.optString("orderFrom");
                        if (from.equals(WsOrderFrom.web)) {
                            // 接收到同步指令
                            WebSocketServ.this.notify(LiveNotifyType.sync, ob.getInt("pageNum"));
                        }
                    }
                    break;
                    case WsOrderType.inquire: {
                        // 接收到被踢的询问
                        WebSocketServ.this.notify(LiveNotifyType.inquired);
                    }
                    break;
                    case WsOrderType.accept: {
                        // 接收到同意被踢指令
                        WebSocketServ.this.notify(LiveNotifyType.accept);
                    }
                    break;
                    case WsOrderType.reject: {
                        // 接收到拒绝被踢指令
                        WebSocketServ.this.notify(LiveNotifyType.reject);
                    }
                    break;
                    case WsOrderType.online_num: {
                        // 接收到直播间人数指令
                        WebSocketServ.this.notify(LiveNotifyType.online_num, ob.getInt("onLines"));
                    }
                    break;
                    case WsOrderType.flow_insufficient: {
                        // 流量不足预警
                        WebSocketServ.this.notify(LiveNotifyType.flow_insufficient);
                    }
                    break;
                    case WsOrderType.flow_run_out_of: {
                        // 流量耗尽通知
                        WebSocketServ.this.notify(LiveNotifyType.flow_run_out_of);
                    }
                    break;
                    case WsOrderType.flow_sufficient: {
                        // 流量充足通知
                        WebSocketServ.this.notify(LiveNotifyType.flow_sufficient);
                    }
                    break;
                    case WsOrderType.open_star: {
                        // 开启星评通知
                        WebSocketServ.this.notify(LiveNotifyType.open_star);
                    }
                    break;
                    case WsOrderType.live_end: {
                        // 直播结束通知
                        WebSocketServ.this.notify(LiveNotifyType.live_end);
                    }
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            YSLog.d(TAG, "onMessage:ByteString" + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            YSLog.d(TAG, "onClosing:" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            YSLog.d(TAG, "onClosed:" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            YSLog.d(TAG, "onFailure:");
            mWsSuccess = false;
            // 1秒后重连
            // 没退出继续发任务
            if (!DeviceUtil.isNetworkEnabled()) {
                return;
            }
            YSLog.d(TAG, "webSocket 失败重连");
            SystemClock.sleep(1000);
            // 没退出继续重连
            mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWsUrl).build(), new WebSocketLink());
        }
    }
}
