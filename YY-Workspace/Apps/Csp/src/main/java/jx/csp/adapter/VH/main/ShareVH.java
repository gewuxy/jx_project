package jx.csp.adapter.VH.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;

/**
 * @auther WangLan
 * @since 2017/11/8
 */

public class ShareVH extends ViewHolderEx {

    public ShareVH(@NonNull View convertView) {
        super(convertView);
    }

    public View getItemLayout() {
        return getView(R.layout.layout_dialog_share_pltatform_item);
    }

    public ImageView getIvIcon() {
        return getView(R.id.dialog_share_iv_wechat);
    }

    public TextView getTvName() {
        return getView(R.id.dialog_share_tv_wechat);
    }
}
