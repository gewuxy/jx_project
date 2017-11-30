package yy.doctor.adapter.meeting;

import android.support.annotation.DrawableRes;
import android.widget.TextView;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.TopicVH;
import yy.doctor.model.constants.SubjectType;
import yy.doctor.model.meet.topic.ITopic;
import yy.doctor.model.meet.topic.TopicButton;
import yy.doctor.model.meet.topic.TopicButton.TTopicButton;
import yy.doctor.model.meet.topic.TopicChoice;
import yy.doctor.model.meet.topic.TopicChoice.TTopicChoice;
import yy.doctor.model.meet.topic.TopicFill;
import yy.doctor.model.meet.topic.TopicFill.TTopicFill;
import yy.doctor.model.meet.topic.TopicTitle;
import yy.doctor.model.meet.topic.TopicTitle.TTopicTitle;

/**
 * 回答内容Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class TopicAdapter extends MultiAdapterEx<ITopic, TopicVH> {

    @Override
    protected void refreshView(int position, TopicVH holder, int itemType) {
        switch (itemType) {
            case SubjectType.title: {
                TopicTitle item = (TopicTitle) getItem(position);
                holder.getTvTitle().setText(item.getString(TTopicTitle.name));
            }
            break;
            case SubjectType.choose: {
                TopicChoice item = (TopicChoice) getItem(position);
                String text = String.format("%s. %s", item.getString(TTopicChoice.key), item.getString(TTopicChoice.value));
                holder.getTvChoose().setText(text); // 选项
                holder.getIvChoose().setSelected(item.getBoolean(TTopicChoice.check));
                setOnViewClickListener(position, holder.getLayout());
            }
            break;
            case SubjectType.fill: {
                TopicFill item = (TopicFill) getItem(position);
                holder.getEtFill().setText(item.getString(TTopicFill.text));
            }
            break;
            case SubjectType.button: {
                TopicButton item = (TopicButton) getItem(position);
                TextView b = holder.getButton();
                b.setText(item.getString(TTopicButton.text));
                setOnViewClickListener(position, b);
            }
            break;
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        @DrawableRes int id = R.layout.layout_topic_item_title;
        switch (itemType) {
            case SubjectType.title: {
                id = R.layout.layout_topic_item_title;
            }
            break;
            case SubjectType.choose: {
                id = R.layout.layout_topic_item_choice;
            }
            break;
            case SubjectType.fill: {
                id = R.layout.layout_topic_item_fill;
            }
            break;
            case SubjectType.button: {
                id = R.layout.layout_topic_item_button;
            }
            break;
        }
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return SubjectType.class.getDeclaredFields().length;
    }

}
