package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogVH extends ViewHolderEx {

    public UpdateLogVH(@NonNull View convertView) {
        super(convertView);

    }

    public TextView getTvTime() {
        return getView(R.id.update_log_item_tv_time);
    }

    public TextView getTvContent() {
        return getView(R.id.update_log_item_tv_content);
    }

}
