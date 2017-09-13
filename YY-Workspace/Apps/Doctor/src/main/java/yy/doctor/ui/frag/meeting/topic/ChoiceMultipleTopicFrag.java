package yy.doctor.ui.frag.meeting.topic;

import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import inject.annotation.router.Route;
import io.reactivex.Flowable;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 多选题
 *
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
@Route
public class ChoiceMultipleTopicFrag extends BaseChoiceTopicFrag {

    @Override
    protected CharSequence getTitleType() {
        return "多选";
    }

    @Override
    protected boolean getButtonVisible() {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 取反刷新
        Choice item = getItem(position);
        boolean selected = !item.getBoolean(TChoice.check);
        item.put(TChoice.check, selected);
        invalidate(position);
        // 更新答案
        StringBuffer sb = new StringBuffer();
        Flowable.fromIterable(getData())
                .filter(choice -> (choice.getBoolean(TChoice.check)))
                .subscribe(choice -> sb.append(choice.getString(TChoice.key)));

        topicFinish(sb.toString());
    }

}
