package yy.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/7/20
 */

public class HomeMeetingFolderVH extends ViewHolderEx {

    public HomeMeetingFolderVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getFolderItemLayout() {
        return getView(R.id.home_meeting_folder_item_layout);
    }

    public TextView getTvFolderName() {
        return getView(R.id.home_meeting_folder_item_tv_folder_name);
    }

    public TextView getTvFolderUnitNum() {
        return getView(R.id.home_meeting_folder_item_tv_unit_num_name);
    }

    public TextView getTvFolderMeetingNum() {
        return getView(R.id.home_meeting_folder_item_tv_meeting_num);
    }

    public TextView getTvFolderLeftSpeakerName() {
        return getView(R.id.home_meeting_folder_item_tv_left_meeting_speaker_name);
    }

    public TextView getTvFolderLeftSpeakerTitle() {
        return getView(R.id.home_meeting_folder_item_tv_left_meeting_speaker_title);
    }

    public TextView getTvFolderMiddleSpeakerName() {
        return getView(R.id.home_meeting_folder_item_tv_middle_meeting_speaker_name);
    }

    public TextView getTvFolderMiddleSpeakerTitle() {
        return getView(R.id.home_meeting_folder_item_tv_middle_meeting_speaker_title);
    }

    public TextView getTvFolderRightSpeakerName() {
        return getView(R.id.home_meeting_folder_item_tv_right_meeting_speaker_name);
    }

    public TextView getTvFolderRightSpeakerTitle() {
        return getView(R.id.home_meeting_folder_item_tv_right_meeting_speaker_title);
    }

}
