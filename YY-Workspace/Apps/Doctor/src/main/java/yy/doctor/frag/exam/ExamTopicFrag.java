package yy.doctor.frag.exam;

import android.view.View;

import org.json.JSONException;

import lib.ys.LogMgr;
import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.frag.list.SRGroupListFragEx;
import lib.ys.network.resp.IListResponse;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.adapter.ExamTopicAdapter;
import yy.doctor.model.exam.GroupExamTopic;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ExamTopicFrag extends SRGroupListFragEx<GroupExamTopic> {
    @Override
    public void initData() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_exam_topic;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        expandAllGroup();

        setRefreshEnable(false);
    }


    @Override
    public MultiGroupAdapterEx<GroupExamTopic, ? extends ViewHolderEx> createAdapter() {
        return new ExamTopicAdapter();
    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    public IListResponse<GroupExamTopic> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

    @Override
    public View createFooterView() {
        LogMgr.e(TAG, "foot");
        return inflate(R.layout.layout_exam_topic_footer);
    }

    @Override
    public boolean canAutoRefresh() {
        if (BuildConfig.TEST) {
            return false;
        } else {
            return true;
        }
    }

   /* public interface OnNextListener {
        void onNext(View v);
    }

    private TextView mTvQ;
    private TextView mTvBtn;
    private OnNextListener mOnNextListener;

    public void setOnNextListener(OnNextListener onNextListener) {
        mOnNextListener = onNextListener;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        super.findViews();
        mTvQ = findView(R.id.exam_topic_tv_question);
        mTvBtn = findView(R.id.exam_topic_footer_tv_btn);
    }

    @Override
    public void setViews() {
        super.setViews();
        if (mTvQ != null) {
            mTvQ.setText(getData().get(0));
        }
        if (mTvBtn != null) {
            mTvBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNextListener != null) {
                        mOnNextListener.onNext(v);
                    }
                }
            });
        }
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_exam_topic_header);
    }

    @Override
    public View createFooterView() {
        return inflate(R.layout.layout_exam_topic_footer);
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new ExamTopicAdapter();
    }*/
}
