package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class CollectionMeetingVH extends ViewHolderEx {

    public CollectionMeetingVH(@NonNull View convertView) {
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
