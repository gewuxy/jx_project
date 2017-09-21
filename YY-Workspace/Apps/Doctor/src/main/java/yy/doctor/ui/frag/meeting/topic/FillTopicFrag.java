package yy.doctor.ui.frag.meeting.topic;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import inject.annotation.router.Route;
import lib.ys.util.KeyboardUtil;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * 填空题
 *
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
@Route
public class FillTopicFrag extends BaseTopicFrag implements TextWatcher, OnFocusChangeListener {

    private EditText mEtAnswer;

    @Override
    public void findViews() {
        super.findViews();

        mEtAnswer = findView(R.id.answer_et);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtAnswer.addTextChangedListener(this);
        mEtAnswer.setOnFocusChangeListener(this);
    }

    @Override
    protected int getContentId() {
        return R.layout.layout_topic_fill;
    }

    @Override
    protected CharSequence getTitleType() {
        return "填空";
    }

    @Override
    protected void setContent() {
        mEtAnswer.setText(mTopic.getString(TTopic.choice));
    }

    @Override
    protected boolean getButtonVisible() {
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        topicFinish(s.toString());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && KeyboardUtil.isActive()) {
            KeyboardUtil.hideFromView(v);
        }
    }
}
