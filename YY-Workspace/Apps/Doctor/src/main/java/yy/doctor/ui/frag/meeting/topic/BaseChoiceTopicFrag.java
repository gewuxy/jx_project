package yy.doctor.ui.frag.meeting.topic;

import android.support.annotation.NonNull;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.List;

import yy.doctor.R;
import yy.doctor.adapter.meeting.ChoiceAdapter;
import yy.doctor.model.meet.exam.Choice;
import yy.doctor.model.meet.exam.Topic.TTopic;

/**
 * @auther : GuoXuan
 * @since : 2017/9/12
 */

abstract public class BaseChoiceTopicFrag extends BaseTopicFrag implements OnItemClickListener {

    private ListView mLv;
    private ChoiceAdapter mAdapter;

    @Override
    public void findViews() {
        super.findViews();

        mLv = findView(R.id.list);
    }

    @NonNull
    @Override
    public final int getContentId() {
        return R.layout.layout_topic_choice;
    }

    @Override
    protected void setContent() {
        mAdapter = new ChoiceAdapter();
        mAdapter.setData(getData());
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(this);
    }

    protected List<Choice> getData() {
        return mTopic.getList(TTopic.options);
    }

    protected Choice getItem(int position) {
        return getData().get(position);
    }

    protected void invalidate(int position) {
        mAdapter.invalidate(position);
    }

}
