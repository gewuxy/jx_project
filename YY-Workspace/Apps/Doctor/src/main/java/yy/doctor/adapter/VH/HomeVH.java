package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.view.recycler.WrapRecyclerView;
import yy.doctor.R;

public class HomeVH extends ViewHolderEx {

    public HomeVH(@NonNull View convertView) {
        super(convertView);
    }

    public RelativeLayout getMeetingItemLayout() {
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

    public TextView getTvDuration() {
        return getView(R.id.home_meeting_item_tv_duration);
    }

    public TextView getTvCollection() {
        return getView(R.id.home_meeting_item_tv_collection);
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

    public TextView getTvHospital() {
        return getView(R.id.home_meeting_item_tv_hospital);
    }

    public NetworkImageView getIvUnit() {
        return getView(R.id.home_meeting_item_iv_unit);
    }

    public WrapRecyclerView getRecyclerView() {
        return getView(R.id.home_recycler_view);
    }

}