package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.CollectionMeetingVH;
import yy.doctor.model.me.CollectionMeetings;
import yy.doctor.model.me.CollectionMeetings.TCollectionMeetings;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class CollectionMeetingAdapter extends AdapterEx<CollectionMeetings, CollectionMeetingVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, CollectionMeetingVH holder) {

        List<CollectionMeetings> list = getData();
        CollectionMeetings item = list.get(position);
        holder.getTvTitle().setText(item.getString(TCollectionMeetings.meetName));
    }

}
