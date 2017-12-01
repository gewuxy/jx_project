package jx.doctor.adapter.VH.home;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import jx.doctor.R;

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

    public ImageView getIvDot() {
        return getView(R.id.notice_item_iv_dot);
    }

    public TextView getTvUnitUnm() {
        return getView(R.id.notice_item_tv_unit);
    }

    public TextView getTvTime() {
        return getView(R.id.notice_item_tv_time);
    }

    public ImageView getIvArrow() {
        return getView(R.id.notice_item_iv_arrow);
    }

}
