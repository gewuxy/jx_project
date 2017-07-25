package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;
import yy.doctor.view.CircleProgressView;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingVH extends ViewHolderEx {

    public MeetingVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.id.meeting_item_layout);
    }

    public ImageView getIvState() {
        return getView(R.id.meeting_item_iv_state);
    }

    public TextView getTvTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public TextView getTvSection() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvTime() {
        return getView(R.id.meeting_item_tv_time);
    }

    public ImageView getIvCme() {
        return getView(R.id.meeting_item_iv_cme);
    }

    public ImageView getIvEpn() {
        return getView(R.id.meeting_item_iv_epn);
    }

    public TextView getTvUnitNum() {
        return getView(R.id.meeting_item_tv_unit_num);
    }

    public TextView getTvMeetingNum() {
        return getView(R.id.meeting_item_tv_meeting_num);
    }

    public View getLayoutProgress() {
        return getView(R.id.meeting_item_layout_progress);
    }

    public CircleProgressView getCpvProgress() {
        return getView(R.id.meeting_item_v_progress);
    }

    public TextView getTvProgress() {
        return getView(R.id.meeting_item_tv_progress);
    }
}
