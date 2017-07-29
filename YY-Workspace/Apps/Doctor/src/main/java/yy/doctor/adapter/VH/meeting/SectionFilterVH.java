package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/28
 */

public class SectionFilterVH extends ViewHolderEx {

    public SectionFilterVH(@NonNull View convertView) {
        super(convertView);
    }

    public ImageView getImageView(){
        return getView(R.id.iv_section_filter);
    }

    public TextView getName(){
        return getView(R.id.section_name);
    }

     public TextView getNumber(){
        return getView(R.id.section_number);
    }

    public View getDivider(){
        return getView(R.id.section_filter_divider);
    }
}
