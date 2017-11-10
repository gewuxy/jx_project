package yy.doctor.ui.frag.meeting.topic;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import inject.annotation.router.Arg;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.yy.ui.frag.base.BaseFrag;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
abstract public class BaseTopicFrag extends BaseFrag {

    public interface OnTopicListener {

        /**
         * 完成题目
         *
         * @param listId  题号
         * @param titleId 题目Id
         * @param answer  该题选择的答案
         */
        void toFinish(int listId, int titleId, String answer);

        /**
         * 下一题
         */
        void toNext();

    }

    private OnTopicListener mListener;

    @Arg
    Topic mTopic; // 该题目的信息

    @Arg(opt = true)
    int mSort; // 题号

    @Arg(opt = true)
    boolean mLastTopic; // 最后一题

    private TextView mTvTitle; // 题目
    private LinearLayout mLayout; // 答题
    private TextView mTvNext; // 下一题(提交)按钮

    public void setTopicListener(OnTopicListener listener) {
        mListener = listener;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        // do nothing
    }

    @Deprecated
    @Override
    public void initNavBar(NavBar bar) {
        // no navBar
    }

    @CallSuper
    @Override
    public void findViews() {
        mTvTitle = findView(R.id.topic_tv_title);
        mLayout = findView(R.id.topic_layout);
        mTvNext = findView(R.id.topic_tv_btn);

        LayoutParams params = LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT);
        mLayout.addView(inflate(getContentId()), params);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.layout_topic;
    }

    @CallSuper
    @Override
    public void setViews() {
        setBackgroundColor(Color.WHITE);
        mTvTitle.setText(String.format("%d. (%s)%s", mSort + 1, getTitleType(), mTopic.getString(TTopic.title))); // 设置题目

        setContent(); // 设置内容

        if (getButtonVisible()) {
            showView(mTvNext);
        } else {
            goneView(mTvNext);
        }

        // 最后一题的时候
        if (mLastTopic) {
            showView(mTvNext);
            mTvNext.setText(R.string.submit);
        }
        setOnClickListener(mTvNext);
    }

    @Override
    public final void onClick(View v) {
        nextTopic();
    }

    protected void nextTopic() {
        if (mListener != null) {
            mListener.toNext();
        }
    }

    protected void topicFinish(String answer) {
        if (mListener != null) {
            mListener.toFinish(mSort, mTopic.getInt(TTopic.id), answer);
        }
    }

    abstract protected int getContentId();

    /**
     * 题目
     */
    abstract protected CharSequence getTitleType();

    /**
     * 内容
     */
    abstract protected void setContent();

    /**
     * 确定按钮是否显示
     */
    abstract protected boolean getButtonVisible();

}
