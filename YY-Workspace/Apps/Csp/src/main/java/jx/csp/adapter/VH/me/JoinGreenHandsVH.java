package jx.csp.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

public class JoinGreenHandsVH extends ViewHolderEx {

    public JoinGreenHandsVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItem() {
        return getView(R.id.meet_item);
    }

    public View getItemLayout() {
        return getView(R.id.meet_item_no_divider);
    }

    public View getDividerTop() {
        return getView(R.id.meet_item_divider_top);
    }

    public NetworkImageView getIvHead() {
        return getView(R.id.meet_item_iv_head);
    }

    public TextView getTvTitle() {
        return getView(R.id.meet_item_tv_title);
    }

    public TextView getTvTime() {
        return getView(R.id.meet_item_play_time);
    }

    public ImageView getIvLive() {
        return getView(R.id.meet_item_iv_live);
    }

    public ImageView getIvPpt() {
        return getView(R.id.meet_item_iv_ppt);
    }

    public ImageView getIvShare() {
        return getView(R.id.meet_item_iv_share);
    }

}
