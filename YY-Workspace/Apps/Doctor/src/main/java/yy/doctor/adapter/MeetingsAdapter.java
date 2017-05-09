package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.MeetingsVH;
import yy.doctor.model.meet.MeetRec;
import yy.doctor.model.meet.MeetRec.TMeetRec;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingsAdapter extends AdapterEx<MeetRec, MeetingsVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, MeetingsVH holder) {
        holder.getTvTitle().setText(getItem(position).getString(TMeetRec.meetName));
        holder.getTvSection().setText(getItem(position).getString(TMeetRec.meetType));
        holder.getTvState().setText(getItem(position).getString(TMeetRec.state));
//        holder.getTvData().setText(getItem(position).getString(TMeetRec.));

    }
}
