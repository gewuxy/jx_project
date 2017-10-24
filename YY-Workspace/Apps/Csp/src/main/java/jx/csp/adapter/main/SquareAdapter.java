package jx.csp.adapter.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import jx.csp.R;
import jx.csp.adapter.VH.main.SquareVH;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class SquareAdapter extends RecyclerAdapterEx<Square, SquareVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_item;
    }

    @Override
    protected void refreshView(int position, SquareVH holder) {
        Square mItem = getItem(position);
        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_record)
                .url(mItem.getString(TSquare.coverUrl))
                .load();
        holder.getTvTotalPage().setText(mItem.getString(TSquare.pageCount));
        holder.getTvTitle().setText(mItem.getString(TSquare.title));
        if (mItem.getInt(TSquare.playType) == PlayType.reb) {
            holder.getTvTime().setText(mItem.getString(TSquare.playTime));
            holder.getTvCurrentPage().setText(mItem.getString(TSquare.playPage));

            if (mItem.getInt(TSquare.playState) == PlayState.un_start) {
                holder.getTvPlayState().setText(R.string.record);
            } else if (mItem.getInt(TSquare.playState) == PlayState.record) {
                holder.getTvPlayState().setText("录播中");
            } else {
                goneView(holder.getTvCurrentPage());
                goneView(holder.getVDivider());
                goneView(holder.getTvPlayState());
            }
            goneView(holder.getIvLive());
        } else {
            holder.getTvCurrentPage().setText(mItem.getString(TSquare.livePage));

            if (mItem.getInt(TSquare.liveState) == LiveState.un_start) {
                holder.getTvPlayState().setText(R.string.solive);
            } else if (mItem.getInt(TSquare.liveState) == LiveState.live) {
                holder.getTvPlayState().setText(R.string.live);
            } else {
                goneView(holder.getTvCurrentPage());
                goneView(holder.getVDivider());
                goneView(holder.getTvPlayState());
                goneView(holder.getIvLive());
            }

            //直播的开始时间转换
            Date d = new Date(Long.parseLong(mItem.getString(TSquare.startTime)));
            SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
            holder.getTvTime().setText(data.format(d));
        }

        setOnViewClickListener(position, holder.getItemLayout());
        setOnViewClickListener(position, holder.getIvShare());
    }
}
