package yy.doctor.ui.frag.meeting.topic;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.router.Route;
import lib.ys.YSLog;
import yy.doctor.model.meet.exam.Fill;
import yy.doctor.model.meet.exam.IAnswer;

/**
 * 填空题
 *
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
@Route
public class FillTopicFrag extends BaseTopicFrag implements TextWatcher {

    @Override
    public void setViews() {
        super.setViews();

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getAdapter().getEditText().addTextChangedListener(FillTopicFrag.this);
                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected CharSequence getTitleType() {
        return "填空";
    }

    @Override
    protected void setContent() {
        // 添加输入框
        List<IAnswer> d = new ArrayList<>();
        d.add(new Fill());
        setData(d);
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
        YSLog.d(TAG, "afterTextChanged:" + s);
    }
}
