package jx.doctor.ui.frag.meeting.topic;

import android.os.Bundle;

import inject.annotation.router.Route;
import lib.ys.ConstantsEx;
import jx.doctor.model.meet.topic.TopicChoice;
import jx.doctor.model.meet.topic.TopicChoice.TTopicChoice;

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
    protected void select(int position) {
        if (mLastPosition != position) {
            if (mLastPosition != ConstantsEx.KInvalidValue) {
                // 取消之前的选择
                TopicChoice item = (TopicChoice) getItem(mLastPosition);
                item.put(TTopicChoice.check, false);
                invalidate(mLastPosition);
            }
            // 选择其他选项
            TopicChoice item = (TopicChoice) getItem(position);
            item.put(TTopicChoice.check, true);
            invalidate(position);

            topicFinish(item.getString(TTopicChoice.key));
            mLastPosition = position;
        } // 不能取消单选的选择答案

        nextTopic();
    }

}
