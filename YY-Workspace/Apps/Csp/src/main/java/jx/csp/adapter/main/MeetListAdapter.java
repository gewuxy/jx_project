package jx.csp.adapter.main;

import android.widget.ImageView;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetVH;
import jx.csp.model.main.Meet;
import lib.ys.network.image.shape.CornerRenderer;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
public class MeetListAdapter extends MeetAdapter {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_list_item;
    }

    @Override
    protected void meetItemRefresh(int position, MeetVH holder) {
        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_main_grid)
                .url(getItem(position).getString(Meet.TMeet.coverUrl))
                .renderer(new CornerRenderer(fit(6)))
                .resize(fit(60), fit(60))
                .load();
    }
}
