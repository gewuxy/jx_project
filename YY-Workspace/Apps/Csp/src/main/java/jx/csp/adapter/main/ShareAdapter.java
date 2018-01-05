package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.ShareVH;
import jx.csp.constant.SharePreview;
import jx.csp.constant.SharePreviewType;
import lib.ys.adapter.MultiAdapterEx;

/**
 * @auther ${HuoXuYu}
 * @since 2018/1/3
 */

public class ShareAdapter extends MultiAdapterEx<SharePreview, ShareVH> {
    @Override
    protected void refreshView(int position, ShareVH holder, int itemType) {
        SharePreview item = getItem(position);
        holder.getIvIcon().setImageResource(item.icon());
        holder.getTvName().setText(item.previewName());
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
        return SharePreviewType.class.getDeclaredFields().length;
    }
}
