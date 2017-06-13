package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;
import yy.doctor.Constants.MeetsState;
import yy.doctor.R;
import yy.doctor.adapter.VH.CollectionMeetingVH;
import yy.doctor.model.me.CollectionMeetings;
import yy.doctor.model.me.CollectionMeetings.TCollectionMeetings;
import yy.doctor.util.UISetter;

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

        holder.getTvSection().setText(item.getString(TCollectionMeetings.meetType));

        long startTime = getItem(position).getLong(TCollectionMeetings.startTime);
        long endTime = getItem(position).getLong(TCollectionMeetings.endTime);
        UISetter.setDateDuration(holder.getTvDate(), holder.getTvDuration(), startTime, endTime);

        holder.getIvUnitNum().placeHolder(R.mipmap.ic_default_home_unit_num)
                .url(item.getString(TCollectionMeetings.headimg))
                .renderer(new CircleRenderer())
                .load();

        holder.getTvUnitNum().setText(item.getString(TCollectionMeetings.nickname));
    }

}
