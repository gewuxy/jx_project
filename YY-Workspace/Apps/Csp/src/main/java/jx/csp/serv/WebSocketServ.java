package jx.csp.serv;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.model.meeting.WebSocketMsg.WsOrderFrom;
import jx.csp.model.meeting.WebSocketMsg.WsOrderType;
import jx.csp.util.Util;
import lib.network.model.NetworkReq;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static lib.ys.util.UtilEx.runOnUIThread;

/**
 * 直播、录播WebSocket的服务
 *
 * @author CaiXiang
 * @since 2017/10/31
 */
@Route
public class WebSocketServ extends ServiceEx implements OnLiveNotify{

    @Arg
    String mWsUrl;

    private WebSocket mWebSocket;
    private boolean mWsSuccess = false; // WebSocket连接是否成功

    @Override
    protected void onHandleIntent(Intent intent) {
        LiveNotifier.inst().add(this);

        mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWsUrl).build(), new WebSocketLink());
    }

    // 接收通知 发送websocket指令
    @Override
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        switch (type) {
            case LiveNotifyType.send_msg: {
                mWebSocket.send((String) data);
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

    public class WebSocketLink extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
            mWsSuccess = true; // 连接成功
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "WebSocket onMessage String = " + text);
            runOnUIThread(() -> {
                try {
                    JSONObject ob = new JSONObject(text);
                    int order = ob.getInt("order");
                    switch (order) {
                        case WsOrderType.sync: {
                            String from = ob.getString("orderFrom");
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
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
            if (Util.noNetwork()) {
                return;
            }
            // 1秒后重连
            // 没退出继续发任务
            runOnUIThread(() -> {
                // 没退出继续重连
                mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWsUrl).build(), new WebSocketLink());
            }, TimeUnit.SECONDS.toMillis(1));
        }
    }

}
