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
        return getView(R.id.main_square_layout);
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

    public TextView getTvCurrentPage() {
        return getView(R.id.current_page);
    }

    public TextView getTvTotalPage() {
        return getView(R.id.total_page);
    }

    public TextView getTvPlayState() {
        return getView(R.id.current_state);
    }

    public ImageView getIvLive(){
        return getView(R.id.iv_square_live);
    }

    public ImageView getIvShare(){
        return getView(R.id.iv_square_share);
    }

    public View getVDivider(){
        return getView(R.id.square_divider);
    }
}
