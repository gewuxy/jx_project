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

    public TextView getTvTitle() {
        return getView(R.id.meeting_item_tv_title);
    }

    public TextView getTvState() {
        return getView(R.id.meeting_item_tv_state);
    }

    public TextView getTvDepartments() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvTime() {
        return getView(R.id.meeting_item_tv_time);
    }

    public TextView getTvDuration() {
        return getView(R.id.meeting_item_tv_data);
    }

    public NetworkImageView getIvUnitNum() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvUnitNum() {
        return getView(R.id.meeting_item_tv_unit_num);
    }
}
