package yy.doctor.adapter.VH;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeVH extends RecyclerViewHolderEx {

    private HomeMeetingVH mHolderMeeting;
    private HomeUnitNumVH mHolderUnitNum;

    public HomeVH(View itemView) {
        super(itemView);

        mHolderMeeting = new HomeMeetingVH(itemView);
        mHolderUnitNum = new HomeUnitNumVH(itemView);
    }

    public HomeMeetingVH getHolderMeeting() {
        return mHolderMeeting;
    }

    public HomeUnitNumVH getHolderUnitNum() {
        return mHolderUnitNum;
    }

    public class HomeMeetingVH extends RecyclerViewHolderEx {

        public HomeMeetingVH(View itemView) {
            super(itemView);
        }

        public TextView getTvTitle() {
            return getView(R.id.home_meeting_item_tv_title);
        }

        public ImageView getIvStatus() {
            return getView(R.id.home_meeting_item_iv_status);
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

    }

    public class HomeUnitNumVH extends RecyclerViewHolderEx {

        public HomeUnitNumVH(View itemView) {
            super(itemView);
        }

        public NetworkImageView getIv() {
            return getView(R.id.home_unit_num_item_iv);
        }

        public TextView getTvName() {
            return getView(R.id.home_unit_num_item_tv_name);
        }


    }

}
