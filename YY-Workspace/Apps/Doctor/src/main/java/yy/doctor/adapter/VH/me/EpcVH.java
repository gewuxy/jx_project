package yy.doctor.adapter.VH.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcVH extends ViewHolderEx {

    public EpcVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.id.epc_item_layout);
    }

    public NetworkImageView getIv() {
        return getView(R.id.epc_item_iv);
    }

    public TextView getTvName() {
        return getView(R.id.epc_item_tv_name);
    }

    public TextView getTvEpn() {
        return getView(R.id.epc_item_tv_epn);
    }

}
