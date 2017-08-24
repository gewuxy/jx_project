package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class SectionVH extends ViewHolderEx {

    public SectionVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvProvince() {
        return getView(R.id.category_tv);
    }

    public View getV() {
        return getView(R.id.category_indicator);
    }

}
