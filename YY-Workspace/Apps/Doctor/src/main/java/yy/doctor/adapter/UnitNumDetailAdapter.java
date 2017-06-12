package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumDetailVH;
import yy.doctor.model.unitnum.UnitNumDetailMeeting;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;
import yy.doctor.util.UISetter;

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
        UnitNumDetailMeeting item = getItem(position);

        holder.getTvTitle().setText(item.getString(TUnitNumDetailMeeting.meetName));

        @MeetsState int state = item.getInt(TUnitNumDetailMeeting.state);
        UISetter.setMeetState(state, holder.getTvState());

        holder.getTvSection().setText(item.getString(TUnitNumDetailMeeting.meetType));

        long startTime = item.getLong(TUnitNumDetailMeeting.startTime);
        long endTime = item.getLong(TUnitNumDetailMeeting.endTime);
        UISetter.setDateDuration(holder.getTvDate(), holder.getTvDuration(), startTime, endTime);

        holder.getIvUnitNum().placeHolder(R.mipmap.ic_default_home_unit_num)
                .renderer(new CircleRenderer())
                .url(item.getString(TUnitNumDetailMeeting.headimg))
                .load();

        holder.getTvUnitNum().setText(item.getString(TUnitNumDetailMeeting.nickname));
    }

}
