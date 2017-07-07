package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class PcdVH extends ViewHolderEx {

    public PcdVH(@NonNull View convertView) {
        super(convertView);
    }

    public RelativeLayout getLayout() {
        return getView(R.id.pcd_item_layout);
    }

    public TextView getTv() {
        return getView(R.id.pcd_item_tv);
    }

    public ImageView getIvArrow() {
        return getView(R.id.pcd_item_iv);
    }

}
