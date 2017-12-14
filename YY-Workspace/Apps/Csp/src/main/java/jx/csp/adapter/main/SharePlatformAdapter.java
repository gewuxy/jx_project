package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.ShareVH;
import jx.csp.constant.SharePlatform;
import jx.csp.constant.ShareType;
import lib.ys.adapter.MultiAdapterEx;

/**
 * @auther WangLan
 * @since 2017/11/8
 */

public class SharePlatformAdapter extends MultiAdapterEx<SharePlatform, ShareVH> {

    @Override
    protected void refreshView(int position, ShareVH holder, int itemType) {
        SharePlatform item = getItem(position);
        holder.getIvIcon().setImageResource(item.icon());
        holder.getTvName().setText(item.platformName());
    }

    @Override
    public int getConvertViewResId(int itemType) {
        return R.layout.layout_dialog_share_pltatform_item;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type();
    }

    @Override
    public int getViewTypeCount() {
        return ShareType.class.getDeclaredFields().length;
    }
}
