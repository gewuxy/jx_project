package jx.doctor.adapter.VH.data;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugDetailVH extends ViewHolderEx {

    public DrugDetailVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvName() {
        return getView(R.id.drug_detail_item_tv_name);
    }

    public ImageView getIv() {
        return getView(R.id.drug_detail_item_iv);
    }

    public TextView getTvDetail() {
        return getView(R.id.drug_detail_item_tv_detail);
    }

}
