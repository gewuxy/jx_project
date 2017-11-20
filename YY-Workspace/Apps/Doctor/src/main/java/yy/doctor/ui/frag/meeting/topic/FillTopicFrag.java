package yy.doctor.ui.frag.meeting.topic;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;

import inject.annotation.router.Route;
import lib.ys.util.KeyboardUtil;
import yy.doctor.adapter.VH.meeting.SubjectVH;
import yy.doctor.model.meet.exam.ISubject;
import yy.doctor.model.meet.exam.TopicFill;

/**
 * 填空题
 *
 * @auther : GuoXuan
 * @since : 2017/9/12
 */
@Route
public class FillTopicFrag extends BaseTopicFrag implements TextWatcher {

    private EditText mEtFill;

    @Override
    public void setViews() {
        super.setViews();

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                setEditTextListener();
                removeOnGlobalLayoutListener(this);
            }

        });
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

    private void setEditTextListener() {
        for (int i = 0; i < getData().size(); i++) {
            ISubject subject = getItem(i);
            if (subject instanceof TopicFill) {
                SubjectVH holder = getAdapter().getCacheVH(i);
                if (holder != null) {
                    mEtFill = holder.getEtFill();
                    mEtFill.addTextChangedListener(this);
                }
                break;
            }
        }
    }

    @Override
    protected void onInvisible() {
        // fixme：点击输入框以外的地方也要隐藏键盘
        if (KeyboardUtil.isActive() && mEtFill != null) {
            KeyboardUtil.hideFromView(mEtFill);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        onInvisible();
    }
}
