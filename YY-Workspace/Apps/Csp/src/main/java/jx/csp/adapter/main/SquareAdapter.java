package jx.csp.adapter.main;

import jx.csp.R;
import jx.csp.adapter.VH.main.SquareVH;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import lib.ys.adapter.recycler.RecyclerAdapterEx;

/**
 * @auther WangLan
 * @since 2017/10/18
 */

public class SquareAdapter extends RecyclerAdapterEx<Square, SquareVH> {
    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_item;
    }

    @Override
    protected void refreshView(int position, SquareVH holder) {
        Square item = getItem(position);
        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_record)
                .url(item.getString(TSquare.coverUrl))
                .load();
        holder.getTvTotalPage().setText(item.getString(TSquare.pageCount));
        holder.getTvTitle().setText(item.getString(TSquare.title));
        if (item.getString(TSquare.playType) == "0") {
            holder.getTvTime().setText(item.getString(TSquare.playTime));
            holder.getTvCurrentPage().setText(item.getString(TSquare.playPage));
//            holder.getTvPlayState().setText(item.getString(TSquare.playState));
        } else {
            holder.getTvTime().setText(item.getString(TSquare.startTime));
            holder.getTvCurrentPage().setText(item.getString(TSquare.livePage));
//            holder.getTvPlayState().setText(item.getString(TSquare.liveState));
        }
    }
}
