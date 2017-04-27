package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalVH extends ViewHolderEx {

    public HospitalVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvGroup() {
        return getView(R.id.hospital_group);
    }

    public TextView getTvChild() {
        return getView(R.id.hospital_child);
    }
}
