package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.TimeUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.UnitNumDetailVH;
import yy.doctor.model.unitnum.UnitNumDetailMeeting;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

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

        @MeetsState int state = item.getInt(TUnitNumDetailMeeting.state);
        UISetter.setMeetState(state, holder.getTvState());

        holder.getTvDepartments().setText(item.getString(TUnitNumDetailMeeting.meetType));
        holder.getTvTime().setText(TimeUtil.formatMilli(item.getLong(TUnitNumDetailMeeting.startTime), "MM月dd日 HH:mm"));
        holder.getTvDuration().setText("时长:" + Util.timeParse(item.getLong(TUnitNumDetailMeeting.endTime) - item.getLong(TUnitNumDetailMeeting.startTime)));

        holder.getIvUnitNum().placeHolder(R.mipmap.ic_default_home_unit_num)
                .renderer(new CircleRenderer())
                .url(item.getString(TUnitNumDetailMeeting.headimg))
                .load();

        holder.getTvUnitNum().setText(item.getString(TUnitNumDetailMeeting.nickname));
    }

}
