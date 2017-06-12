package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/6/8
 */

public class RecVH extends ViewHolderEx {
    public RecVH(@NonNull View convertView) {
        super(convertView);
    }

    /**
     * 会议
     */

    public TextView getTvMeetTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public TextView getTvMeetState() {
        return getView(R.id.meeting_item_tv_state);
    }

    public TextView getTvMeetSection() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvMeetTime() {
        return getView(R.id.meeting_item_tv_time);
    }

    public TextView getTvMeetData() {
        return getView(R.id.meeting_item_tv_data);
    }

    public NetworkImageView getIvMeetUN() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvMeetUN() {
        return getView(R.id.meeting_item_tv_unit_num);
    }

    /**
     * 单位号
     */

    public NetworkImageView getIvUnitNumUN() {
        return getView(R.id.unit_num_iv);
    }

    public TextView getTvUnitNumUN() {
        return getView(R.id.unit_num_tv);
    }
}
