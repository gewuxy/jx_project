package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.ui.activity.base.BaseListActivity;
import lib.yy.network.Result;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.CommentAdapter;
import yy.doctor.model.meet.Comment;
import yy.doctor.model.meet.Comment.TComment;
import yy.doctor.model.meet.CommentHistories;
import yy.doctor.model.meet.CommentHistories.TCommentHistories;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 会议评论界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */
public class MeetingCommentActivity extends BaseListActivity<Comment, CommentAdapter> {

    private static final int KCloseNormal = 1000; //  1000表示正常关闭,意思是建议的连接已经完成了

    private TextView mTvSend;
    private EditText mEtSend;
    private String mMeetId;
    private WebSocket mWebSocket;

    public static void nav(Context context, String meetId) {
        Intent i = new Intent(context, MeetingCommentActivity.class)
                .putExtra(Extra.KMeetId, meetId);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_comment;
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.meeting_comment, this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSend = findView(R.id.meeting_comment_tv_send);
        mEtSend = findView(R.id.meeting_comment_et_send);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvSend);

        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.histories(mMeetId));
        mWebSocket = exeWebSocketReq(NetFactory.commentIM(mMeetId), new CommentListener());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), CommentHistories.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<CommentHistories> r = (Result<CommentHistories>) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            List<Comment> comments = r.getData().getList(TCommentHistories.datas);
            Collections.sort(comments, (lhs, rhs) -> lhs.getLong(TComment.sendTime) > rhs.getLong(TComment.sendTime) ? 1 : -1);
            addAll(comments);
            setSelection(getCount());
        } else {
            setViewState(ViewState.error);
            showToast(r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_comment_tv_send:
                String message = mEtSend.getText().toString().trim();
                if (TextUtil.isEmpty(message)) {
                    showToast("请输入评论内容");
                    return;
                }
                mWebSocket.send(toJson(message));
                break;
        }
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
        return comment.toCommonJson();
    }

    /**
     * 转化为item的数据类型
     *
     * @param text
     * @return
     */
    private Comment toComment(String text) {
        Comment comment = new Comment();
        JSONObject jb = null;
        try {
            jb = new JSONObject(text);
            comment.put(TComment.sender, jb.getString("sender"));
            comment.put(TComment.message, jb.getString("message"));
            comment.put(TComment.sendTime, jb.getString("sendTime"));
            comment.put(TComment.headimg, jb.getString("headimg"));
        } catch (JSONException e) {
            YSLog.d(TAG, "toComment:" + e.toString());
        }
        return comment;
    }

    public class CommentListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            YSLog.d(TAG, "onOpen:" + response.message());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            YSLog.d(TAG, "onMessage:String" + text);
            runOnUIThread(() -> {
                addItem(toComment(text));
                invalidate();
                setSelection(getCount());
                mEtSend.setText("");
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
            runOnUIThread(() -> exeWebSocketReq(NetFactory.commentIM(mMeetId), new CommentListener()), 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWebSocket != null) {
            mWebSocket.close(KCloseNormal, "主动关闭");
            mWebSocket = null;
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}
