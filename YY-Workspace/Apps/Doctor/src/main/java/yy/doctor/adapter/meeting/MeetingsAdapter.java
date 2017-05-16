package yy.doctor.adapter.meeting;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.res.ResLoader;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.meeting.MeetingsVH;
import yy.doctor.model.meet.MeetRec;
import yy.doctor.model.meet.MeetRec.TMeetRec;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingsAdapter extends AdapterEx<MeetRec, MeetingsVH> {
    @Override
    protected void initView(int position, MeetingsVH holder) {
        holder.getIvNum().placeHolder(R.mipmap.ic_default_unit_num).load();
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_meeting_item;
    }

    @Override
    protected void refreshView(int position, MeetingsVH holder) {
        holder.getTvTitle().setText(getItem(position).getString(TMeetRec.meetName));
        holder.getTvSection().setText(getItem(position).getString(TMeetRec.meetType));

        setState(position, holder);
    }

    /**
     * 设置会议状态
     *
     * @param position
     * @param holder
     */
    private void setState(int position, MeetingsVH holder) {
        int state = getItem(position).getInt(TMeetRec.state);
        String strState = null;
        int resId = 0;
        int colorId = 0;
        switch (state) {
            case MeetsState.not_started: {
                strState = "未开始";
                resId = R.mipmap.meeting_ic_not_started;
                colorId = R.color.text_01b557;
            }
            break;
            case MeetsState.under_way: {
                strState = "进行中";
                resId = R.mipmap.meeting_ic_under_way;
                colorId = R.color.text_e6600e;
            }
            break;
            case MeetsState.retrospect: {
                strState = "精彩回顾";
                resId = R.mipmap.meeting_ic_retrospect;
                colorId = R.color.text_5cb0de;
            }
            break;
        }
        holder.getTvState().setText(strState);
        holder.getTvState().setTextColor(ResLoader.getColor(colorId));
        holder.getIvState().setImageDrawable(ResLoader.getDrawable(resId));
    }

}
