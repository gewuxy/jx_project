package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpnDetailsVH extends ViewHolderEx {

    public EpnDetailsVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView geTvTime() {
        return getView(R.id.epn_details_item_tv_time);
    }

    public TextView geTvType() {
        return getView(R.id.epn_details_item_tv_status);
    }

    public TextView geTvNum() {return getView(R.id.epn_details_item_tv_num);}

    public TextView geTvContent() {
        return getView(R.id.epn_details_item_tv_content);
    }


}
