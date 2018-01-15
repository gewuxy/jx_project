package jx.csp.adapter.main;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetGridVH;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import lib.ys.ConstantsEx;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MeetGridAdapter extends RecyclerAdapterEx<Meet, MeetGridVH> {

    private OnAdapterLongClickListener mLongClickListener;

    public interface OnAdapterLongClickListener {
        void onLongClick(int position, View v);
    }

    public void setLongClickListener(OnAdapterLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_item;
    }

    @Override
    protected void refreshView(int position, MeetGridVH holder) {
        Meet item = getItem(position);

        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_main_grid)
                .url(item.getString(TMeet.coverUrl))
                .resize(fit(160), fit(110))
                .load();

        holder.getTvTitle().setText(item.getString(TMeet.title));
        holder.getTvTotalPage().setText(item.getString(TMeet.pageCount));

        setOnViewClickListener(position, holder.getItemLayout());
        setOnViewClickListener(position, holder.getIvShare());
        setOnViewClickListener(position, holder.getIvLive());

        holder.getItemLayout().setOnLongClickListener(v -> {
            if (mLongClickListener == null) {
                return false;
            } else {
                mLongClickListener.onLongClick(position, v);
                return true;
            }
        });

        showView(holder.getTvCurrentPage());
        showView(holder.getVDivider());
        switch (item.getInt(TMeet.playType)) {
            case PlayType.reb: {
                holder.getTvTime().setText(item.getString(TMeet.playTime));
                goneView(holder.getIvLive());
                switch (item.getInt(TMeet.playState)) {
                    case PlayState.un_start: {
                        holder.getTvCurrentPage().setText(item.getString(TMeet.playPage));
                        holder.getTvPlayState().setText(R.string.recorded);
                    }
                    break;
                    case PlayState.record:
                    case PlayState.stop: {
                        holder.getTvCurrentPage().setText(item.getString(TMeet.playPage));
                        holder.getTvPlayState().setText(R.string.on_record);
                    }
                    break;
                    case PlayState.end: {
                        holder.getTvPlayState().setText(ConstantsEx.KEmpty);
                        goneView(holder.getTvCurrentPage());
                        goneView(holder.getVDivider());
                    }
                    break;
                    default:{
                        holder.getTvPlayState().setText(ConstantsEx.KEmpty);
                        goneView(holder.getTvCurrentPage());
                        goneView(holder.getVDivider());
                        break;
                    }
                }
            }
            break;
            case PlayType.live:
            case PlayType.video: {
                if (item.getInt(TMeet.playType) == PlayType.video) {
                    showView(holder.getIvLive());
                } else {
                    goneView(holder.getIvLive());
                }

                long startTime = item.getLong(TMeet.startTime);
                long endTime = item.getLong(TMeet.endTime);
                long serverTime = item.getLong(TMeet.serverTime);
                int liveState = item.getInt(TMeet.liveState);
                if (liveState == LiveState.un_start || startTime > serverTime) {
                    holder.getTvCurrentPage().setText(item.getString(TMeet.livePage));
                    holder.getTvPlayState().setText(R.string.solive);
                    //直播未开始状态的开始时间转换
                    holder.getTvTime().setText(TimeFormatter.milli(item.getLong(TMeet.startTime), TimeFormat.form_MM_dd_24));
                } else if ((liveState == LiveState.live || liveState == LiveState.stop) && (startTime < serverTime && endTime > serverTime)) {
                    holder.getTvCurrentPage().setText(item.getString(TMeet.livePage));
                    holder.getTvPlayState().setText(R.string.live);
                    holder.getTvTime().setText(item.getString(TMeet.playTime));
                } else {
                    goneView(holder.getTvCurrentPage());
                    goneView(holder.getVDivider());
                    goneView(holder.getIvLive());
                    holder.getTvPlayState().setText(ConstantsEx.KEmpty);
                    holder.getTvTime().setText(item.getString(TMeet.playTime));
                }
            }
            break;
        }
    }

    public void showSharePlayback(int pos) {
        if (getCacheVH(pos) == null) {
            return;
        }
        showView(getCacheVH(pos).getTvSharePlayback());
        goneView(getCacheVH(pos).getTvPlayState());
    }

    public void goneSharePlayback(int pos) {
        if (getCacheVH(pos) == null) {
            return;
        }
        goneView(getCacheVH(pos).getTvSharePlayback());
    }
}
