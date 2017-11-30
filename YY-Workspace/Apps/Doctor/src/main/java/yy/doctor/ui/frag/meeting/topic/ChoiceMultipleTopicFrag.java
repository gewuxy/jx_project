package yy.doctor.ui.frag.meeting.topic;

import inject.annotation.router.Route;
import io.reactivex.Flowable;
import yy.doctor.model.meet.topic.TopicChoice;
import yy.doctor.model.meet.topic.TopicChoice.TTopicChoice;

/**
 * 多选题
 *
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
@Route
public class ChoiceMultipleTopicFrag extends BaseTopicFrag {

    @Override
    protected void select(int position) {
        // 取反刷新
        TopicChoice item = (TopicChoice) getItem(position);
        boolean selected = !item.getBoolean(TTopicChoice.check);
        item.put(TTopicChoice.check, selected);
        invalidate(position);
        // 更新答案
        StringBuffer sb = new StringBuffer();
        Flowable.fromIterable(getData())
                .filter(iSubject -> iSubject instanceof TopicChoice)
                .filter(choice -> ((TopicChoice)choice).getBoolean(TTopicChoice.check))
                .subscribe(choice -> sb.append(((TopicChoice)choice).getString(TTopicChoice.key)));

        topicFinish(sb.toString());
    }

}
