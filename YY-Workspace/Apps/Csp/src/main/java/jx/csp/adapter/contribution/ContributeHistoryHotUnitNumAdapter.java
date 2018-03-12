package jx.csp.adapter.contribution;

import android.support.v7.widget.LinearLayoutManager;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.ContributeHistoryHotUnitNumVH;
import jx.csp.adapter.VH.contribution.HotUnitNumVH;
import jx.csp.model.contribution.ContributeHistories;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum.ContributeHistoryHotUnitNumType;
import jx.csp.model.contribution.ListTitle;
import jx.csp.model.contribution.ListTitle.TListTitle;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class ContributeHistoryHotUnitNumAdapter extends MultiAdapterEx<IContributeHistoryHotUnitNum, ContributeHistoryHotUnitNumVH> {

    private ContributeHistoryAdapter mContributeHistoryAdapter;

    @Override
    protected void initView(int position, ContributeHistoryHotUnitNumVH holder, int itemType) {
        if (itemType == ContributeHistoryHotUnitNumType.contribution_history) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mContributeHistoryAdapter = new ContributeHistoryAdapter();
            holder.getRecyclerView().setLayoutManager(linearLayoutManager);
            holder.getRecyclerView().setAdapter(mContributeHistoryAdapter);
        }
    }

    @Override
    protected void refreshView(int position, ContributeHistoryHotUnitNumVH holder, int itemType) {
        switch (itemType) {
            case ContributeHistoryHotUnitNumType.list_title: {
                ListTitle item = (ListTitle) getItem(position);
                holder.getTvListTitle().setText(item.getString(TListTitle.title));
            }
            break;
            case ContributeHistoryHotUnitNumType.contribution_history: {
                ContributeHistories list = (ContributeHistories) getItem(position);
                mContributeHistoryAdapter.setData(list.getData());
            }
            break;
            case ContributeHistoryHotUnitNumType.hot_unit_num: {
                HotUnitNum item = (HotUnitNum) getItem(position);

                HotUnitNumVH hotUnitNumVH = holder.getHotUnitNumVH();
                hotUnitNumVH.getIv()
                        .placeHolder(R.drawable.ic_default_unit_num)
                        .renderer(new CircleRenderer())
                        .load();
            }
            break;
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = R.layout.layout_contribute_history_hot_unit_num_list_title;
        switch (itemType) {
            case ContributeHistoryHotUnitNumType.list_title: {
                id = R.layout.layout_contribute_history_hot_unit_num_list_title;
            }
            break;
            case ContributeHistoryHotUnitNumType.contribution_history: {
                id = R.layout.layout_contribute_history_hot_unit_num_history;
            }
            break;
            case ContributeHistoryHotUnitNumType.contribution_history_empty: {
                id = R.layout.layout_contribute_history_hot_unit_num_history_empty_view;
            }
            break;
            case ContributeHistoryHotUnitNumType.large_divider: {
                id = R.layout.layout_contribute_history_hot_unit_num_large_divider;
            }
            break;
            case ContributeHistoryHotUnitNumType.hot_unit_num: {
                id = R.layout.layout_contribute_history_hot_unit_num_hot_unit_num_item;
            }
            break;
        }
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ContributeHistoryHotUnitNumType.class.getDeclaredFields().length;
    }
}
