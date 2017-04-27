package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogVH extends ViewHolderEx {

    public UpdateLogVH(@NonNull View convertView) {
        super(convertView);

    }

    public NetworkImageView getIv() {
        return getView(R.id.epc_iv);
    }

    public TextView getTvName() {
        return getView(R.id.epc_tv_name);
    }

    public TextView getTvEpn() {
        return getView(R.id.epc_tv_epn);
    }


}
