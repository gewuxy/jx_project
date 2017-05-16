package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/4/28
 */

public class MeetingsVH extends ViewHolderEx {
    public MeetingsVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public ImageView getIvState() {
        return getView(R.id.meeting_item_iv_state);
    }

    public TextView getTvState() {
        return getView(R.id.meeting_item_tv_state);
    }

    public TextView getTvSection() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvTime() {
        return getView(R.id.meeting_item_tv_time);
    }

    public TextView getTvData() {
        return getView(R.id.meeting_item_tv_data);
    }

    public NetworkImageView getIvNum() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvNum() {
        return getView(R.id.meeting_item_tv_unit_num);
    }
}
