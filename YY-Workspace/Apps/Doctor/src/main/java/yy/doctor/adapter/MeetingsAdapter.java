package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.MeetingsVH;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingsAdapter extends AdapterEx<String, MeetingsVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, MeetingsVH holder) {

    }
}
