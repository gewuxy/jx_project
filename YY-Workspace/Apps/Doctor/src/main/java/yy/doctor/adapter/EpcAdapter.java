package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CornerRenderer;
import yy.doctor.R;
import yy.doctor.adapter.VH.UpdateLogVH;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcAdapter extends AdapterEx<String,UpdateLogVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_epc_item;
    }

    @Override
    protected void refreshView(int position, UpdateLogVH holder) {

        holder.getIv()
                .placeHolder(R.mipmap.epc_ic_default)
                .renderer(new CornerRenderer(fitDp(3)))
                .load();

    }


}
