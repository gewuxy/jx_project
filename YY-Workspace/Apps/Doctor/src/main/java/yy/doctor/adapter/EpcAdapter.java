package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.network.image.renderer.CornerRenderer;
import yy.doctor.R;
import yy.doctor.adapter.VH.EpcVH;
import yy.doctor.model.me.Epc;
import yy.doctor.model.me.Epc.TEpc;

/**
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

        List<Epc> list = getData();

        Epc item = list.get(position);
        holder.getTvName().setText(item.getString(TEpc.name));
        holder.getTvEpn().setText(item.getString(TEpc.price) + "象数");

        holder.getIv()
                .placeHolder(R.mipmap.ic_default_epc)
                .renderer(new CornerRenderer(fitDp(3)))
                .load();

    }

}
