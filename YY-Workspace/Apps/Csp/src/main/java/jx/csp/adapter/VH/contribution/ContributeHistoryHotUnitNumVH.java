package jx.csp.adapter.VH.contribution;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.adapter.VH.ViewHolderEx;
import lib.ys.view.recycler.WrapRecyclerView;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class ContributeHistoryHotUnitNumVH extends ViewHolderEx {

    private HotUnitNumVH mHotUnitNumVH;

    public ContributeHistoryHotUnitNumVH(@NonNull View convertView) {
        super(convertView);

        mHotUnitNumVH = new HotUnitNumVH(convertView);
    }

    public HotUnitNumVH getHotUnitNumVH() {
        return mHotUnitNumVH;
    }

    public TextView getTvListTitle() {
        return getView(R.id.contribute_history_hot_unit_num_tv_list_title);
    }

    public View getHistoryEmptyView() {
        return getView(R.id.contribute_history_hot_unit_num_history_empty_layout);
    }

    public WrapRecyclerView getRecyclerView() {
        return getView(R.id.contribute_history_hot_unit_num_recycler_view);
    }

}
