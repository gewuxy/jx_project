package jx.csp.adapter;

import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import jx.csp.adapter.VH.PlatformVH;
import jx.csp.model.contribute.Platform;
import jx.csp.model.contribute.Platform.TPlatformDetail;
import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CircleRenderer;

/**
 * @auther Huoxuyu
 * @since 2017/9/28
 */

public class PlatformAdapter extends AdapterEx<Platform, PlatformVH> {

    private OnPlatformCheckedListener mListener;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_platform_item;
    }

    @Override
    protected void refreshView(int position, PlatformVH holder) {
        Platform item = getItem(position);
        holder.getTvName().setText(item.getString(TPlatformDetail.nickname));
        holder.getTvText().setText(item.getString(TPlatformDetail.info));
        holder.getIv()
                .placeHolder(R.drawable.ic_default_user_header)
                .renderer(new CircleRenderer())
                .url(item.getString(TPlatformDetail.avatar))
                .load();

        setOnViewClickListener(position, holder.getItemLayout());

    }

    @Override
    protected void onViewClick(int position, View v) {
        ImageView cb = getCacheVH(position).getIvSelect();
        boolean selected = cb.isSelected();
        if (mListener != null) {
            mListener.onPlatformChecked(position, !selected);
            cb.setSelected(!selected);
        }
    }

    public void setListener(OnPlatformCheckedListener l) {
        mListener = l;
    }

    public interface OnPlatformCheckedListener {
        void onPlatformChecked(int position, boolean isSelected);
    }
}
