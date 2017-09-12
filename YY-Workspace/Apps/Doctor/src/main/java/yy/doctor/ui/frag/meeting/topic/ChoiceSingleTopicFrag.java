package yy.doctor.ui.frag.meeting.topic;

import android.view.View;

import inject.annotation.router.Route;
import lib.ys.ConstantsEx;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 单选题
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */
@Route
public class ChoiceSingleTopicFrag extends BaseTopicFrag {

    private int mLastPosition; // 记录单选的上一个选择

    @Override
    public void initData() {
        mLastPosition = ConstantsEx.KInvalidValue;
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mLastPosition != position) {
            if (mLastPosition != ConstantsEx.KInvalidValue) {
                // 取消之前的选择
                Choice item = (Choice) getItem(mLastPosition);
                item.put(TChoice.check, false);
                invalidate(mLastPosition);
            }
            // 选择其他选项
            Choice item = (Choice) getItem(position);
            item.put(TChoice.check, true);
            invalidate(position);

            topicFinish(item.getString(TChoice.key));
            mLastPosition = position;
        } // 不能取消单选的选择答案

        if (!mLastTopic) {
            nextTopic();
        }
    }

    @Override
    protected CharSequence getTitleType() {
        return "单选";
    }

    @Override
    protected void setContent() {
        setData(mTopic.getList(TTopic.options));
    }

    @Override
    protected boolean getButtonVisible() {
        return false;
    }

}
