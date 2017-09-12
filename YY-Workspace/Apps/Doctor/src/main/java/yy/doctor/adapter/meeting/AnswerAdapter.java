package yy.doctor.adapter.meeting;

import android.widget.EditText;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.AnswerVH;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Choice.TChoice;
import yy.doctor.model.meet.exam.Fill;
import yy.doctor.model.meet.exam.Fill.TFill;
import yy.doctor.model.meet.exam.IAnswer;
import yy.doctor.model.meet.exam.IAnswer.AnswerType;

/**
 * 回答内容Adapter
 *
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class AnswerAdapter extends MultiAdapterEx<IAnswer, AnswerVH> {

    private EditText mEtAnswer;

    public EditText getEditText() {
        return mEtAnswer;
    }

    @Override
    protected void refreshView(int position, AnswerVH holder, int itemType) {
        switch (itemType) {
            case AnswerType.choice: {
                Choice item = (Choice) getItem(position);
                String text = String.format("%s. %s", item.getString(TChoice.key), item.getString(TChoice.value));
                holder.getTvAnswer().setText(text); // 选项
                holder.getIvAnswer().setSelected(item.getBoolean(TChoice.check));
            }
            break;
            case AnswerType.fill: {
                Fill item = (Fill) getItem(position);
                mEtAnswer = holder.getEtAnswer();
                mEtAnswer.setText(item.getString(TFill.answer));
            }
            break;
        }

    }

    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case AnswerType.choice: {
                return R.layout.layout_answer_item_choice;
            }
            case AnswerType.fill: {
                return R.layout.layout_answer_item_fill;
            }
        }
        return R.layout.layout_answer_item_choice;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return AnswerType.class.getDeclaredFields().length;
    }

}
