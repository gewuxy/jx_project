package jx.doctor.adapter.home;

import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import lib.ys.network.image.shape.CircleRenderer;
import jx.doctor.R;
import jx.doctor.adapter.VH.home.HomeUnitNumVH;
import jx.doctor.model.home.RecUnitNum;
import jx.doctor.model.home.RecUnitNum.Attention;
import jx.doctor.model.home.RecUnitNum.TRecUnitNum;
import jx.doctor.ui.activity.me.unitnum.UnitNumDetailActivityRouter;
import jx.doctor.util.UISetter;
import jx.doctor.util.Util;

/**
 * 首页单位号的adapter
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class HomeUnitNumAdapter extends RecyclerAdapterEx<RecUnitNum, HomeUnitNumVH> {

    private onAttentionListener mListener;

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_home_unit_num_item;
    }

    @Override
    protected void refreshView(int position, HomeUnitNumVH holder) {
        RecUnitNum item = getItem(position);

        holder.getTvName().setText(item.getString(TRecUnitNum.nickname));
        holder.getIv().placeHolder(R.drawable.ic_default_home_unit_num)
                .renderer(new CircleRenderer())
                .url(item.getString(TRecUnitNum.headimg))
                .load();

        //判断用户是否已经关注过这个单位号
        UISetter.setAttention(holder.getTvAttention(), item.getInt(TRecUnitNum.attention));

        //关注的点击事件
        setOnViewClickListener(position, holder.getTvAttention());
        setOnViewClickListener(position, holder.getLayout());
    }

    @Override
    protected void onViewClick(int position, View v) {

        int id = v.getId();
        RecUnitNum item = getItem(position);
        switch (id) {
            case R.id.home_unit_num_layout: {
                UnitNumDetailActivityRouter.create(item.getInt(TRecUnitNum.id)).route(getContext());
            }
            break;
            case R.id.home_unit_num_item_tv_attention: {
                if (mListener != null) {
                    int attention = item.getInt(TRecUnitNum.attention);
                    if (attention == Attention.no) {
                        // 无网
                        if (Util.noNetwork()) {
                            return;
                        }
                        setTvAttention(position, Attention.yes);
                        //改变源数据
                        item.put(TRecUnitNum.attention, Attention.yes);
                        mListener.onAttentionChanged(attention, item.getInt(TRecUnitNum.id));
                        showToast(R.string.attention_success);
                    }
                }
            }
            break;
        }
    }

    public interface onAttentionListener {
        void onAttentionChanged(int attention, int unitNumId);
    }

    public void setAttentionListener(onAttentionListener l) {
        mListener = l;
    }

    public void setTvAttention(int pos, int attention) {
        if (getCacheVH(pos) == null) {
            return;
        }

        TextView tv = getCacheVH(pos).getTvAttention();
        UISetter.setAttention(tv, attention);
    }
}
