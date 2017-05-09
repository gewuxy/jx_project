package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import lib.ys.view.photoViewer.NetworkPhotoView;
import yy.doctor.R;

/**
 * @auther yuansui
 * @since 2017/4/25
 */

public class UnitNumDetailVH extends ViewHolderEx {

    public UnitNumDetailVH(@NonNull View convertView) {
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

    public TextView getTvDepartments() {
        return getView(R.id.meeting_item_tv_section);
    }

    public TextView getTvTime() {
        return getView(R.id.meeting_item_tv_time);
    }

    public TextView getTvDuration() {
        return getView(R.id.meeting_item_tv_data);
    }

    public NetworkPhotoView getIvUnitNum() {
        return getView(R.id.meeting_item_iv_unit_num);
    }

    public TextView getTvUnitNum() {
        return getView(R.id.meeting_item_tv_unit_num);
    }

}
