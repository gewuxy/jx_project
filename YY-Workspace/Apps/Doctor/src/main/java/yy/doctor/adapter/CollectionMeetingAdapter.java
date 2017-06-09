package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.util.TimeUtil;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.CollectionMeetingVH;
import yy.doctor.model.me.CollectionMeetings;
import yy.doctor.model.me.CollectionMeetings.TCollectionMeetings;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

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

        @MeetsState int state = item.getInt(TCollectionMeetings.state);
        UISetter.setMeetState(state, holder.getTvState());

        holder.getTvDepartments().setText(item.getString(TCollectionMeetings.meetType));
        holder.getTvTime().setText(TimeUtil.formatMilli(item.getLong(TCollectionMeetings.startTime), "MM月dd日 HH:mm"));
        holder.getTvDuration().setText("时长:" + Util.timeParse(item.getLong(TCollectionMeetings.endTime) - item.getLong(TCollectionMeetings.startTime)));
        holder.getIvUnitNum().placeHolder(R.mipmap.ic_default_home_unit_num)
                .url(item.getString(TCollectionMeetings.headimg))
                .renderer(new CircleRenderer())
                .load();
        holder.getTvUnitNum().setText(item.getString(TCollectionMeetings.nickname));
    }

}
