package yy.doctor.activity.meeting;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.CommentAdapter;
import yy.doctor.util.Util;

/**
 * 会议评论界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class MeetingCommentActivity extends BaseListActivity<String> {

    private TextView mTvSend;
    private EditText mEtSend;

    @Override
    public void initData() {
        for (int i = 0; i < 14; ++i) {
            addItem("");
        }
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
        mTvSend.setOnClickListener(this);
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new CommentAdapter();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.meeting_comment_tv_send:
                mEtSend.getText().toString().trim();
                break;
            default:
                break;
        }
    }
}
