package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonLibVH extends ViewHolderEx {

    public ThomsonLibVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTv() {
        return getView(R.id.thomson_lib_tv);
    }

}
