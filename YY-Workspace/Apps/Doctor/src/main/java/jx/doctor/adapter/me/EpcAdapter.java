package jx.doctor.adapter.me;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.shape.CornerRenderer;
import jx.doctor.R;
import jx.doctor.adapter.VH.me.EpcVH;
import jx.doctor.model.me.Epc;
import jx.doctor.model.me.Epc.TEpc;
import jx.doctor.ui.activity.me.epc.EpcDetailActivityRouter;

import static lib.ys.util.res.ResLoader.getString;

/**
 * 象城的adapter
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcAdapter extends AdapterEx<Epc, EpcVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_epc_item;
    }

    @Override
    protected void refreshView(int position, EpcVH holder) {

        Epc item = getItem(position);
        holder.getTvName().setText(item.getString(TEpc.name));
        holder.getTvEpn().setText(item.getString(TEpc.price) + getString(R.string.epn));
        holder.getIv()
                .placeHolder(R.drawable.ic_default_epc)
                .renderer(new CornerRenderer(fit(3)))
                .url(item.getString(TEpc.picture))
                .load();
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    protected void onViewClick(int position, View v) {
        Epc item = getItem(position);
        EpcDetailActivityRouter.create()
                .goodId(item.getInt(TEpc.id))
                .goodName(item.getString(TEpc.name))
                .smallImgUrl(item.getString(TEpc.picture))
                .route(getContext());
    }

}
