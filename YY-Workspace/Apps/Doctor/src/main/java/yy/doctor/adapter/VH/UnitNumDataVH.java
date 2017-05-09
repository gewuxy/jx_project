package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class UnitNumDataVH extends ViewHolderEx {

    public UnitNumDataVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTv(){
        return getView(R.id.unit_num_data_item_tv);
    }

}
