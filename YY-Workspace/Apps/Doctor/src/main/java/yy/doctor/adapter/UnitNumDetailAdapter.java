package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumDetailVH;
import yy.doctor.model.unitnum.UnitNumDetailMeeting;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;

/**
 * @auther yuansui
 * @since 2017/4/25
 */

public class UnitNumDetailAdapter extends AdapterEx<UnitNumDetailMeeting, UnitNumDetailVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, UnitNumDetailVH holder) {

        List<UnitNumDetailMeeting> list = getData();
        UnitNumDetailMeeting item = list.get(position);
        holder.getTvTitle().setText(item.getString(TUnitNumDetailMeeting.meetName));

    }
}
