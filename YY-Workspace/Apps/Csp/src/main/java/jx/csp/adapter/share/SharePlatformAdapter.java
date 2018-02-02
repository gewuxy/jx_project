package jx.csp.adapter.share;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.share.ShareVH;
import jx.csp.constant.SharePlatform;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.util.res.ResLoader;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class SharePlatformAdapter extends RecyclerAdapterEx<SharePlatform, ShareVH> {

    private OnItemClickListener mListener;

    @Override
    protected void refreshView(int position, ShareVH holder) {
        SharePlatform item = getItem(position);
        setOnViewClickListener(position, holder.getItemLayout());
        if (item.isClick()) {
            holder.getIvIcon().setImageResource(item.icon());
            holder.getTvName().setText(item.platformName());
        } else {
            holder.getIvIcon().setImageResource(R.drawable.share_ic_un_contribute);
            holder.getTvName().setText(R.string.contribute);
            holder.getTvName().setTextColor(ResLoader.getColor(R.color.text_c6c5cb));
            holder.getItemLayout().setEnabled(false);
        }
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_dialog_share_pltatform_item;
    }

    @Override
    protected void onViewClick(int position, View v) {
        if (mListener != null) {
            mListener.onClick(position);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
