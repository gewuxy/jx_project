package jx.csp.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther WangLan
 * @since 2017/10/16
 */

public class HistoryDetailVH extends ViewHolderEx {

    public HistoryDetailVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getIvHead() {
        return getView(R.id.detail_head);
    }

    public TextView getTvTitle() {
        return getView(R.id.detail_title);
    }

    public TextView getTvState() {
        return getView(R.id.play_state);
    }

    public TextView getTvTime() {
        return getView(R.id.play_time);
    }

    public TextView getTvPaper() {
        return getView(R.id.play_paper);
    }
}
