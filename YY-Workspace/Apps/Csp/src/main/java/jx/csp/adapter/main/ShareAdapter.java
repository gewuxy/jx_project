package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.ShareVH;
import jx.csp.constant.ShareType;
import jx.csp.model.main.Share;
import jx.csp.model.main.Share.TShare;
import lib.ys.adapter.MultiAdapterEx;

/**
 * @auther WangLan
 * @since 2017/11/8
 */

public class ShareAdapter extends MultiAdapterEx<Share, ShareVH> {

    @Override
    protected void refreshView(int position, ShareVH holder, int itemType) {
        Share item = getItem(position);
        holder.getIvIcon().setImageResource(item.getInt(TShare.icon));
        holder.getTvName().setText(item.getString(TShare.name));
    }

    @Override
    public int getConvertViewResId(int itemType) {
        return R.layout.layout_dialog_share_pltatform_item;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getInt(TShare.type);
    }

    @Override
    public int getViewTypeCount() {
        return ShareType.class.getDeclaredFields().length;
    }

}
