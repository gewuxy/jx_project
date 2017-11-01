package jx.csp.adapter.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetGridVH;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

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
        Meet mItem = getItem(position);
        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_record)
                .url(mItem.getString(TMeet.coverUrl))
                .load();
        holder.getTvTotalPage().setText(mItem.getString(TMeet.pageCount));
        holder.getTvTitle().setText(mItem.getString(TMeet.title));
        if (mItem.getInt(TMeet.playType) == PlayType.reb) {
            holder.getTvTime().setText(mItem.getString(TMeet.playTime));
            holder.getTvCurrentPage().setText(mItem.getString(TMeet.playPage));

            if (mItem.getInt(TMeet.playState) == PlayState.un_start) {
                holder.getTvPlayState().setText(R.string.record);
            } else if (mItem.getInt(TMeet.playState) == PlayState.record) {
                holder.getTvPlayState().setText("录播中");

            } else {
                goneView(holder.getTvCurrentPage());
                goneView(holder.getVDivider());
                goneView(holder.getTvPlayState());
            }
            goneView(holder.getIvLive());
        } else {
            holder.getTvCurrentPage().setText(mItem.getString(TMeet.livePage));

            if (mItem.getInt(TMeet.liveState) == LiveState.un_start) {
                holder.getTvPlayState().setText(R.string.solive);

                //直播未开始状态的开始时间转换
                Date d = new Date(Long.parseLong(mItem.getString(TMeet.startTime)));
                SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
                holder.getTvTime().setText(data.format(d));
            } else if (mItem.getInt(TMeet.liveState) == LiveState.live) {
                holder.getTvPlayState().setText(R.string.live);
                holder.getTvTime().setText(mItem.getString(TMeet.playTime));
            } else {
                goneView(holder.getTvCurrentPage());
                goneView(holder.getVDivider());
                goneView(holder.getTvPlayState());
                goneView(holder.getIvLive());
                holder.getTvTime().setText(mItem.getString(TMeet.playTime));
            }


        }

        setOnViewClickListener(position, holder.getItemLayout());
        setOnViewClickListener(position, holder.getIvShare());
        setOnViewClickListener(position, holder.getIvLive());
    }
}
