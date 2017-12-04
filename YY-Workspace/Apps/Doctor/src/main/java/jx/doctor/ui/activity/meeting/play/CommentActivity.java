package jx.doctor.ui.activity.meeting.play;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.jx.ui.activity.base.BaseListActivity;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import jx.doctor.R;
import jx.doctor.adapter.meeting.CommentAdapter;
import jx.doctor.model.meet.Comment;
import jx.doctor.model.meet.Comment.TComment;
import jx.doctor.model.meet.CommentHistories;
import jx.doctor.model.meet.CommentHistories.TCommentHistories;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetFactory;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.Util;

/**
 * 会议评论界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
@Route
public class CommentActivity extends BaseListActivity<Comment, CommentAdapter> {

    private static final String KSender = "sender"; // 发送人名称
    private static final String KMessage = "message"; // 发送内容
    private static final String KSendTime = "sendTime"; // 发送时间
    private static final String KHeadImg = "headimg"; // 发送人头像

    @Arg
    String mMeetId;

    private TextView mTvSend;
    private EditText mEtSend;
    private WebSocket mWebSocket;
    private boolean mSuccess; // WebSocket连接成功


    @Override
    public void initData() {
        mSuccess = false; // 没连接默认失败
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_comment;
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.comment, this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSend = findView(R.id.comment_tv_send);
        mEtSend = findView(R.id.comment_et_send);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvSend);

        refresh(RefreshWay.embed);
        getDataFromNet();
    }

    @Override
    public void getDataFromNet() {
        /**
         * @deprecated 这版本没有下拉加载以前数据
         */
        exeNetworkReq(MeetAPI.commentHistories(mMeetId, 100, 1).build());
        mWebSocket = exeWebSocketReq(NetFactory.commentIM(mMeetId), new CommentListener());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), CommentHistories.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            CommentHistories history = (CommentHistories) r.getData();
            if (history == null) {
                return;
            }
            List<Comment> comments = history.getList(TCommentHistories.dataList);
            // 排序(升序)
            if (comments == null) {
                return;
            }
            Collections.sort(comments, (lhs, rhs) -> lhs.getLong(TComment.sendTime) > rhs.getLong(TComment.sendTime) ? 1 : -1);
            addAll(comments);
            setSelection(getCount());
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_tv_send:
                String message = mEtSend.getText().toString().trim();
                if (TextUtil.isEmpty(message)) {
                    // 过滤空信息
                    showToast(R.string.comment_import);
                    return;
                }
                if (mSuccess) {
                    mWebSocket.send(toJson(message));
                } else {
                    showToast(R.string.send_error);
                }
                // 清空发送框
                mEtSend.setText("");
                break;
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    /**
     * 转化为指定json的格式
     *
     * @param s
     * @return
     */
    private String toJson(String s) {
        Comment comment = new Comment();
        comment.put(TComment.meetId, mMeetId);
        comment.put(TComment.message, s);
        return comment.toJson();
    }

    /**
     * 转化为item的数据类型
     *
     * @param text
     * @return
     */
    private Comment toComment(String text) {
        Comment comment = new Comment();
        try {
            JSONObject object = new JSONObject(text);
            comment.put(TComment.sender, object.getString(KSender));
            comment.put(TComment.message, object.getString(KMessage));
            comment.put(TComment.sendTime, object.getString(KSendTime));
            comment.put(TComment.headimg, object.getString(KHeadImg));
        } catch (JSONException e) {
            YSLog.d(TAG, "toComment:" + e.toString());
        }
        return comment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MeetWebSocketListener.close(mWebSocket);
    }

    public class CommentListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
            mSuccess = true; // 连接成功
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "onMessage:String" + text);
            runOnUIThread(() -> {
                // 添加发送的数据
                addItem(toComment(text));
                invalidate();
                // 跳转到最后一条
                setSelection(getCount());
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
            if (isFinishing()) {
                return;
            }
            // 没退出继续发任务
            runOnUIThread(() -> {
                if (isFinishing() || Util.noNetwork()) {
                    return;
                }

                // 没退出继续重连
                mWebSocket = exeWebSocketReq(NetFactory.commentIM(mMeetId), new CommentListener());
            }, TimeUnit.SECONDS.toMillis(2));

        }
    }

}
