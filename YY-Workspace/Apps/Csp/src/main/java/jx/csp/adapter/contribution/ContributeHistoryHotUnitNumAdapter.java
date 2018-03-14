package jx.csp.adapter.contribution;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.ContributeHistoryHotUnitNumVH;
import jx.csp.adapter.VH.contribution.HotUnitNumVH;
import jx.csp.model.contribution.ContributeHistories;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.contribution.HotUnitNum.THotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum.ContributeHistoryHotUnitNumType;
import jx.csp.model.contribution.ListTitle;
import jx.csp.model.contribution.ListTitle.TListTitle;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.ui.activity.contribution.ContributeActivityRouter;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class ContributeHistoryHotUnitNumAdapter extends MultiAdapterEx<IContributeHistoryHotUnitNum, ContributeHistoryHotUnitNumVH> {

    private Meet mMeet;
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
                mContributeHistoryAdapter.setMeetData(mMeet);
                mContributeHistoryAdapter.setData(list.getData());
            }
            break;
            case ContributeHistoryHotUnitNumType.hot_unit_num: {
                HotUnitNum item = (HotUnitNum) getItem(position);

                HotUnitNumVH hotUnitNumVH = holder.getHotUnitNumVH();
                hotUnitNumVH.getTv().setText(item.getString(THotUnitNum.acceptName));
                hotUnitNumVH.getIv()
                        .placeHolder(R.drawable.ic_default_unit_num)
                        .url(item.getString(THotUnitNum.headimg))
                        .renderer(new CircleRenderer())
                        .load();
                setOnViewClickListener(position, hotUnitNumVH.getItemLayout());
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

    @Override
    protected void onViewClick(int position, View v) {
        HotUnitNum item = (HotUnitNum) getItem(position);
        if (item.getType() == ContributeHistoryHotUnitNumType.hot_unit_num) {
            UnitNum unitNum = new UnitNum();
            unitNum.put(TUnitNum.id, 1); // 平台id  目前只有YaYa医师单位号 是 1
            unitNum.put(TUnitNum.unitNumId, item.getInt(THotUnitNum.acceptId));
            unitNum.put(TUnitNum.platformName, item.getString(THotUnitNum.acceptName));
            unitNum.put(TUnitNum.imgUrl, item.getString(THotUnitNum.headimg));
            ContributeActivityRouter.create(mMeet, unitNum).route(getContext());
        }
    }

    public void setMeetData(Meet meet) {
        mMeet = meet;
    }
}
