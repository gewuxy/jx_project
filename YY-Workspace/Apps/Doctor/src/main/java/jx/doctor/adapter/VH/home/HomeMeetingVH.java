package jx.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import jx.doctor.R;
import jx.doctor.adapter.VH.meeting.MeetingVH;

/**
 * @author CaiXiang
 * @since 2017/7/20
 */

public class HomeMeetingVH extends ViewHolderEx {

    private MeetingVH mMeetingVH;

    public HomeMeetingVH(@NonNull View convertView) {
        super(convertView);
        mMeetingVH = new MeetingVH(convertView);
    }

    public MeetingVH getMeetingVH() {
        return mMeetingVH;
    }

    public View getMeetingItemLayout() {
        return getView(R.id.home_meeting_item_layout);
    }

    public TextView getTvStatus() {
        return getView(R.id.home_meeting_item_tv_status);
    }

    public NetworkImageView getIvSpeaker() {
        return getView(R.id.home_meeting_item_iv_speaker);
    }

    public TextView getTvSpeakerName() {
        return getView(R.id.home_meeting_item_tv_speaker_name);
    }

    public TextView getTvSpeakerRank() {
        return getView(R.id.home_meeting_item_tv_speaker_rank);
    }


}
