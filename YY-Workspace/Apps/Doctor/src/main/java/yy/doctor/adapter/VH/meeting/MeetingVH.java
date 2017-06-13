package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingVH extends ViewHolderEx {

    public MeetingVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public TextView getTvState() {
        return getView(R.id.meeting_item_tv_state);
    }

    public TextView getTvSection() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvDate() {
        return getView(R.id.meeting_item_tv_date);
    }

    public TextView getTvDuration() {
        return getView(R.id.meeting_item_tv_duration);
    }

    public NetworkImageView getIvUnitNum() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvUnitNum() {
        return getView(R.id.meeting_item_tv_unit_num);
    }
}
