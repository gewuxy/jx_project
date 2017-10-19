package jx.csp.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class HistoryVH extends ViewHolderEx {

    public HistoryVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.id.history_item_layout);
    }

    public NetworkImageView getIvHead() {
        return getView(R.id.hkmovie_head);
    }

    public TextView getTvName() {
        return getView(R.id.hkmovie_name);
    }

    public TextView getTvInfo() {
        return getView(R.id.hkmovie_intro);
    }
}
