package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetGridVH;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MeetGridAdapter extends RecyclerAdapterEx<Meet, MeetGridVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_item;
    }

    @Override
    protected void refreshView(int position, MeetGridVH holder) {
        Meet item = getItem(position);
        holder.getIvHead()
                .placeHolder(R.drawable.main_grid_ic_default)
                .url(item.getString(TMeet.coverUrl))
                .load();
        holder.getTvTotalPage().setText(item.getString(TMeet.pageCount));
        holder.getTvTitle().setText(item.getString(TMeet.title));
        switch (item.getInt(TMeet.playType)) {
            case PlayType.reb: {
                holder.getTvTime().setText(item.getString(TMeet.playTime));
                holder.getTvCurrentPage().setText(item.getString(TMeet.playPage));
                holder.getTvPlayState().setText(R.string.on_record);
                goneView(holder.getIvLive());
            }
            break;
            case PlayType.live: {
                goneView(holder.getIvLive());
                liveState(holder, item);
            }
            break;
            case PlayType.video: {
                showView(holder.getIvLive());
                liveState(holder, item);
            }
            break;
        }
        setOnViewClickListener(position, holder.getItemLayout());
        setOnViewClickListener(position, holder.getIvShare());
        setOnViewClickListener(position, holder.getIvLive());
    }

    private void liveState(MeetGridVH holder, Meet item) {
        holder.getTvCurrentPage().setText(item.getString(TMeet.livePage));
        long startTime = item.getLong(TMeet.startTime);
        long stopTime = item.getLong(TMeet.endTime);
        if (startTime > System.currentTimeMillis()) {
            holder.getTvPlayState().setText(R.string.solive);
            //直播未开始状态的开始时间转换
            holder.getTvTime().setText(TimeFormatter.milli(item.getString(TMeet.startTime), TimeFormat.form_MM_dd_24));
        } else if (startTime < System.currentTimeMillis() && stopTime > System.currentTimeMillis()) {
            holder.getTvPlayState().setText(R.string.live);
            holder.getTvTime().setText(item.getString(TMeet.playTime));
        } else {
            goneView(holder.getTvCurrentPage());
            goneView(holder.getVDivider());
            goneView(holder.getTvPlayState());
            goneView(holder.getIvLive());
            holder.getTvTime().setText(item.getString(TMeet.playTime));
        }
    }
}
