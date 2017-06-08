package yy.doctor.adapter.meeting;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.RecVH;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.IRec.RecType;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RecAdapter extends MultiAdapterEx<IRec, RecVH> {
    @Override
    protected void refreshView(int position, RecVH holder, int itemType) {
        switch (itemType) {
            case RecType.meeting:
                break;
            case RecType.unit_num:
                break;
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        switch (itemType) {
            case RecType.meeting:
                return R.layout.layout_meeting_item;
            case RecType.unit_num:
                return R.layout.layout_unit_num_item;
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return RecType.class.getDeclaredFields().length;
    }
}
