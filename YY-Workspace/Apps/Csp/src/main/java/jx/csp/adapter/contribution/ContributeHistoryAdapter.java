package jx.csp.adapter.contribution;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.ContributeHistoryVH;
import jx.csp.model.contribution.ContributeHistory;
import jx.csp.model.contribution.HotUnitNum.THotUnitNum;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.ui.activity.contribution.ContributeActivityRouter;
import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.util.res.ResLoader;

/**
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeHistoryAdapter extends RecyclerAdapterEx<ContributeHistory, ContributeHistoryVH> {

    private Meet mMeet;

    @Override
    protected void refreshView(int position, ContributeHistoryVH holder) {
        ContributeHistory item = getItem(position);

        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .url(item.getString(THotUnitNum.headimg))
                .renderer(new CircleRenderer())
                .load();
        holder.getTvName().setText(item.getString(THotUnitNum.acceptName));
        holder.getTvNum().setText(String.format(ResLoader.getString(R.string.already_contribution), item.getInt(THotUnitNum.acceptCount)));
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_contribute_history_hot_unit_num_history_item;
    }

    @Override
    protected void onViewClick(int position, View v) {
        ContributeHistory item = getItem(position);
        UnitNum unitNum = new UnitNum();
        unitNum.put(TUnitNum.id, 1); // 平台id  目前只有YaYa医师单位号 是 1
        unitNum.put(TUnitNum.unitNumId, item.getInt(THotUnitNum.acceptId));
        unitNum.put(TUnitNum.platformName, item.getString(THotUnitNum.acceptName));
        unitNum.put(TUnitNum.imgUrl, item.getString(THotUnitNum.headimg));
        ContributeActivityRouter.create(mMeet, unitNum).route(getContext());
    }

    public void setMeetData(Meet meet) {
        mMeet = meet;
    }
}
