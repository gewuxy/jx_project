package yy.doctor.ui.activity.meeting.play;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.JsonUtil;
import lib.ys.util.UtilEx;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.LiveOrder;
import yy.doctor.model.meet.ppt.LiveOrder.TLiveOrder;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/10/16
 */

public class MeetingLivePresenterImpl extends BasePresenterImpl<MeetingLiveContract.View> implements MeetingLiveContract.Presenter {

    private PPT mPpt;

    public MeetingLivePresenterImpl(MeetingLiveContract.View view) {
        super(view);
    }

    @Override
    public void getDataFromNet(String meetId, String moduleId) {
        exeNetworkReq(MeetAPI.toCourse(meetId, moduleId).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPpt = r.getData();
            getView().initView(mPpt);
            if (mPpt != null) {
                String url = mPpt.getString(TPPT.socketUrl);
                url = "ws://10.0.0.250:8081/live/order?courseId=14379";
                exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }

    public class WebSocketImpl extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "onMessage:String" + text);
            UtilEx.runOnUIThread(() -> {
                try {
                    JSONObject object = new JSONObject(text);
                    int position = object.optInt("pageNum");

                    int order = object.optInt("order");
                    if (order == 0) {
                        // 直播
                        /*CourseInfo courseInfo = mPpt.get(TPPT.course);
                        if (courseInfo == null) {
                            return;
                        }
                        List<Course> courses = courseInfo.getList(TCourseInfo.details);
                        if (courses == null) {
                            return;
                        }
                        courses.get(position).put(TCourse.audioUrl, object.optString("audioUrl"));*/
                    } else {
                        // 同步
//                        mView.addCourse(position);
                    }
                    // FIXME:先不考虑2
                } catch (JSONException e) {
                    YSLog.d(TAG, "onMessage:" + e.getMessage());
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
            // 2S秒后重连
            UtilEx.runOnUIThread(() -> {
                if (Util.noNetwork()) {
                    return;
                }

                // 重连
                if (mPpt != null) {
                    String url = mPpt.getString(TPPT.socketUrl);
                    exeWebSocketReq(NetFactory.webLive(url), new WebSocketImpl());
                }
            }, TimeUnit.SECONDS.toMillis(2));

        }
    }

}
