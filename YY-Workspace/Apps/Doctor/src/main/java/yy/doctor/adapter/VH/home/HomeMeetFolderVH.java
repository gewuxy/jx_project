package yy.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/7/20
 */

public class HomeMeetFolderVH extends ViewHolderEx {

    public HomeMeetFolderVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getFolderItemLayout() {
        return getView(R.id.home_meet_folder_item_layout);
    }

    public TextView getTvFolderName() {
        return getView(R.id.home_meet_folder_item_tv_folder_name);
    }

    public TextView getTvFolderUnitNum() {
        return getView(R.id.home_meet_folder_item_tv_unit_num_name);
    }

    public TextView getTvFolderMeetNum() {
        return getView(R.id.home_meet_folder_item_tv_meet_num);
    }

    public View getSpeakers() {
        return getView(R.id.home_meet_folder_item_layout_speakers);
    }

}
