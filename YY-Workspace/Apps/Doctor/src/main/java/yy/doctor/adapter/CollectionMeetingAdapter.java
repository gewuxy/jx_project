package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.CollectionMeetingVH;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class CollectionMeetingAdapter extends AdapterEx<String,CollectionMeetingVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.form_item_meeting;
    }

    @Override
    protected void refreshView(int position, CollectionMeetingVH holder) {

    }

}
