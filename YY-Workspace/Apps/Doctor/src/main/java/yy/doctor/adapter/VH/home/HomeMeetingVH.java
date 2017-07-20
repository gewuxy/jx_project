package yy.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/7/20
 */

public class HomeMeetingVH extends ViewHolderEx {

    public HomeMeetingVH(@NonNull View convertView) {
        super(convertView);
    }

    public LinearLayout getMeetingItemLayout() {
        return getView(R.id.home_meeting_item_layout);
    }

    public TextView getTvTitle() {
        return getView(R.id.home_meeting_item_tv_title);
    }

    public TextView getTvStatus() {
        return getView(R.id.home_meeting_item_tv_status);
    }

    public TextView getTvSection() {
        return getView(R.id.home_meeting_item_tv_section);
    }

    public TextView getTvData() {
        return getView(R.id.home_meeting_item_tv_date);
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

    public ImageView getIvCme() {
        return getView(R.id.home_meeting_item_iv_cme);
    }

    public ImageView getIvEpn() {
        return getView(R.id.home_meeting_item_iv_epn);
    }

    public TextView getTvUnitNum() {
        return getView(R.id.home_meeting_item_tv_unit_num);
    }

}
