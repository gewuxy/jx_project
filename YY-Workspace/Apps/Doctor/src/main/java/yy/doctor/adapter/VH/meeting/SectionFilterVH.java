package yy.doctor.adapter.VH.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.network.image.NetworkImageView;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/28
 */

public class SectionFilterVH extends ViewHolderEx {

    public SectionFilterVH(@NonNull View convertView) {
        super(convertView);
    }

    public NetworkImageView getImageView() {
        return getView(R.id.iv_section_filter);
    }

    public TextView getTvName() {
        return getView(R.id.section_name);
    }

    public TextView getTvNumber() {
        return getView(R.id.section_number);
    }

    public View getDivider() {
        return getView(R.id.section_filter_divider);
    }
}
