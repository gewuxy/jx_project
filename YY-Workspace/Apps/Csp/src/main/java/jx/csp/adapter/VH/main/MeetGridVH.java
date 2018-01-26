package jx.csp.adapter.VH.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.RecyclerViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class MeetGridVH extends RecyclerViewHolderEx {

    public MeetGridVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.id.main_meet_layout);
    }

    public View getDividerTop() {
        return getView(R.id.main_meet_divider_top);
    }

    public NetworkImageView getIvHead() {
        return getView(R.id.iv_head);
    }

    public TextView getTvTitle() {
        return getView(R.id.tv_title);
    }

    public TextView getTvTime() {
        return getView(R.id.main_play_time);
    }

    public ImageView getIvLive() {
        return getView(R.id.main_meet_iv_live);
    }

    public ImageView getIvShare() {
        return getView(R.id.main_meet_iv_share);
    }

    public TextView getTvSharePlayback() {
        return getView(R.id.frag_main_meet_vp_tv_share_playback);
    }
}
