package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.ChoiceVH;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;

/**
 * 回答内容Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class ChoiceAdapter extends AdapterEx<Choice, ChoiceVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_topic_choice_item;
    }

    @Override
    protected void refreshView(final int position, ChoiceVH holder) {
        Choice item = getItem(position);
        String text = String.format("%s. %s", item.getString(TChoice.key), item.getString(TChoice.value));
        holder.getTvAnswer().setText(text); // 选项
        holder.getIvAnswer().setSelected(item.getBoolean(TChoice.check));
    }

}
