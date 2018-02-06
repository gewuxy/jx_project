package jx.csp.adapter.main;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetVH;
import jx.csp.constant.FiltrateType;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Live.LiveState;
import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.res.ResLoader;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MeetAdapter extends AdapterEx<Meet, MeetVH> {

    private OnAdapterLongClickListener mLongClickListener;

    public interface OnAdapterLongClickListener {
        void onLongClick(int position, View v);
    }

    public void setLongClickListener(OnAdapterLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_card_item;
    }

    @Override
    protected void refreshView(int position, MeetVH holder) {
        meetItemRefresh(position, holder);
        Meet item = getItem(position);

        holder.getTvTitle().setText(item.getString(TMeet.title));

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

        if (item.getType() == FiltrateType.ppt) {
            showView(holder.getIvPpt());
        } else {
            goneView(holder.getIvPpt());
        }

        switch (item.getInt(TMeet.playType)) {
            case CourseType.reb: {
                holder.getTvTime().setText(item.getString(TMeet.playTime));
                holder.getTvTime().setTextColor(ResLoader.getColor(R.color.text_9699a2));
                goneView(holder.getIvLive());
            }
            break;
            case CourseType.ppt_live:
            case CourseType.ppt_video_live: {
                if (item.getInt(TMeet.playType) == CourseType.ppt_video_live) {
                    showView(holder.getIvLive());
                } else {
                    goneView(holder.getIvLive());
                }

                long startTime = item.getLong(TMeet.startTime);
                int liveState = item.getInt(TMeet.liveState);
                if (liveState == LiveState.un_start) {
                    //直播未开始状态的开始时间转换
                    holder.getTvTime().setText(TimeFormatter.milli(startTime, TimeFormat.form_MM_dd_24));
                } else {
                    holder.getTvTime().setText(item.getString(TMeet.playTime));
                }
                holder.getTvTime().setTextColor(ResLoader.getColor(R.color.text_1fbedd));
            }
            break;
        }
    }

    protected void meetItemRefresh(int position, MeetVH holder) {
    }

}
