package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.CommentAdapter;
import yy.doctor.model.meet.Comment;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 会议评论界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class MeetingCommentActivity extends BaseListActivity<Comment, CommentAdapter> {

    private TextView mTvSend;
    private EditText mEtSend;
    private static final int KHistories = 0;
    private static final int KSend = 1;
    private String mMeetId;

    public static void nav(Context context, String meetId) {
        Intent i = new Intent(context, MeetingCommentActivity.class)
                .putExtra(Extra.KMeetId, meetId);
        LaunchUtil.startActivity(context, i);
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
        Util.addBackIcon(bar, "评论", this);
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

        exeWebSocketReq(NetFactory.commentIM(), new CommentListener());
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KHistories, NetFactory.histories(mMeetId));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_comment_tv_send:
                String message = mEtSend.getText().toString().trim();
//                refresh(RefreshWay.dialog);
//                exeNetworkReq(KSend, NetFactory.send(mMeetId, meesage, "0"));
                break;
            default:
                break;
        }
    }

    public class CommentListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        }
    }
}
