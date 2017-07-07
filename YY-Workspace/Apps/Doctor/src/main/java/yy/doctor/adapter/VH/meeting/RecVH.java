package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * 搜索
 *
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RecVH extends ViewHolderEx {

    public RecVH(@NonNull View convertView) {
        super(convertView);
    }

    /**********************************
     * 会议
     *********************************/

    public TextView getTvMeetTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public TextView getTvMeetState() {
        return getView(R.id.meeting_item_tv_state);
    }

    public TextView getTvMeetSection() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvMeetDate() {
        return getView(R.id.meeting_item_tv_date);
    }

    public TextView getTvMeetDuration() {
        return getView(R.id.meeting_item_tv_duration);
    }

    public NetworkImageView getIvMeetUN() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvMeetUN() {
        return getView(R.id.meeting_item_tv_unit_num);
    }

    /**********************************
     * 单位号
     *********************************/

    public NetworkImageView getIvUnitNumUN() {
        return getView(R.id.unit_num_item_iv);
    }

    public TextView getTvUnitNumUN() {
        return getView(R.id.unit_num_item_tv);
    }

    public TextView getTvMore() {
        return getView(R.id.reach_more_tv);
    }
}
