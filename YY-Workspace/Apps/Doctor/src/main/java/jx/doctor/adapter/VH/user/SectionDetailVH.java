package jx.doctor.adapter.VH.user;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import jx.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/2
 */
public class SectionDetailVH extends ViewHolderEx {

    public SectionDetailVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvCity() {
        return getView(R.id.grade_tv);
    }

}
