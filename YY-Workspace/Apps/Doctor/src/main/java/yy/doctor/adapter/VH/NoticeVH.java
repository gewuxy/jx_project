package yy.doctor.adapter.VH;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeVH extends ViewHolderEx {

    public NoticeVH(@NonNull View convertView) {
        super(convertView);
    }

    public TextView getTvContent() {
        return getView(R.id.notice_item_tv_content);
    }

    public TextView getTvTime() {
        return getView(R.id.notice_item_tv_time);
    }

    public TextView getTvUnit() {
        return getView(R.id.notice_item_tv_unit);
    }

}
