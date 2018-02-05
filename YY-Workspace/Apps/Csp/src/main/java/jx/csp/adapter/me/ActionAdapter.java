package jx.csp.adapter.me;

import jx.csp.R;
import jx.csp.adapter.VH.me.JoinGreenHandsVH;
import jx.csp.model.me.Action;
import jx.csp.model.me.Action.TAction;
import jx.csp.util.Util;
import lib.ys.adapter.AdapterEx;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

public class ActionAdapter extends AdapterEx<Action, JoinGreenHandsVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_card_item;
    }

    @Override
    protected void refreshView(int position, JoinGreenHandsVH holder) {
        showView(holder.getDividerTop());
        goneView(holder.getIvLive());
        goneView(holder.getIvPpt());
        Action item = getItem(position);
        holder.getTvTime().setText(Util.getSpecialTimeFormat(item.getInt(TAction.duration), "'", "''"));
        holder.getTvTitle().setText(item.getString(TAction.title));
        holder.getIvHead().placeHolder(R.drawable.ic_default_record)
                .url(item.getString(TAction.coverUrl))
                .load();

        setOnViewClickListener(position, holder.getIvShare());
    }
}
