package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.res.ResLoader;
import yy.doctor.Constants.MeetsState;
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
        holder.getTvDepartments().setText(item.getString(TUnitNumDetailMeeting.meetType));
        //判断会议状态
        if (item.getInt(TUnitNumDetailMeeting.state) == MeetsState.not_started) {
            holder.getTvState().setText("未开始");
            holder.getTvState().setTextColor(ResLoader.getColor(R.color.text_01b557));
            holder.getIvState().setImageResource(R.mipmap.meeting_ic_not_started);
        } else if (item.getInt(TUnitNumDetailMeeting.state) == MeetsState.under_way) {
            holder.getTvState().setText("进行中");
            holder.getTvState().setTextColor(ResLoader.getColor(R.color.text_e6600e));
            holder.getIvState().setImageResource(R.mipmap.meeting_ic_under_way);
        } else if (item.getInt(TUnitNumDetailMeeting.state) == MeetsState.retrospect) {
            holder.getTvState().setText("精彩回顾");
            holder.getTvState().setTextColor(ResLoader.getColor(R.color.text_5cb0de));
            holder.getIvState().setImageResource(R.mipmap.meeting_ic_retrospect);
        } else {
            //do nothing
        }


    }
}
