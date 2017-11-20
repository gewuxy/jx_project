package yy.doctor.ui.frag.meeting.topic;

import android.os.Bundle;
import android.view.View;

import inject.annotation.router.Arg;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseListFrag;
import yy.doctor.R;
import yy.doctor.adapter.meeting.SubjectAdapter;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.exam.ISubject;

/**
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
abstract public class BaseTopicFrag extends BaseListFrag<ISubject, SubjectAdapter> implements MultiAdapterEx.OnAdapterClickListener {

    @Arg
    int mSort; // 题号

    private OnTopicListener mListener;

    public interface OnTopicListener {

        /**
         * 完成题目
         *
         * @param id     题目Id
         * @param answer 该题选择的答案
         */
        void toFinish(int id, String answer);

        /**
         * 下一题
         */
        void toNext();

    }

    public void setTopicListener(OnTopicListener listener) {
        mListener = listener;
    }

    @Override
    public void initData(Bundle state) {
        // do nothing
    }

    @Deprecated
    @Override
    public void initNavBar(NavBar bar) {
        // no navBar
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_topic;
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        int type = getAdapter().getItemViewType(position);
        switch (type) {
            case SubjectType.choose: {
                select(position);
            }
            break;
            case SubjectType.button: {
                nextTopic();
            }
            break;
        }

    }

    /**
     * 选择题的选择
     *
     * @param position 选项位置
     */
    protected void select(int position) {
    }

    /**
     * 下一题
     */
    protected void nextTopic() {
        if (mListener != null) {
            mListener.toNext();
        }
    }

    /**
     * 完成作答
     *
     * @param answer 该题作答答案
     */
    protected void topicFinish(String answer) {
        if (mListener != null) {
            mListener.toFinish(mSort, answer);
        }
    }
}
