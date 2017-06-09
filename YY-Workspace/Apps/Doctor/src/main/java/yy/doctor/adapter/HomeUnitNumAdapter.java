package yy.doctor.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import lib.ys.adapter.recycler.RecyclerAdapterEx;
import yy.doctor.R;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.adapter.VH.HomeUnitNumVH;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;

/**
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

        List<RecUnitNum> list = getData();
        RecUnitNum unitNum = list.get(position);

        holder.getTvName().setText(unitNum.getString(TRecUnitNum.nickname));
        holder.getIv().placeHolder(R.mipmap.ic_default_home_unit_num)
                .url(unitNum.getString(TRecUnitNum.headimg))
                .load();

        holder.getIv().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UnitNumDetailActivity.nav(getContext(), unitNum.getInt(TRecUnitNum.id));
            }
        });

        //判断用户是否已经关注过这个单位号
        TextView tvAttention = holder.getTvAttention();
        if (unitNum.getInt(TRecUnitNum.attention) == 1) {
            tvAttention.setSelected(true);
            tvAttention.setClickable(false);
            tvAttention.setText("已关注");
        }

        //关注的点击事件
        tvAttention.setOnClickListener(v -> {
            if (mListener != null) {
                if (tvAttention.isClickable()) {
                    mListener.onAttentionClick(unitNum.getInt(TRecUnitNum.attention), unitNum.getInt(TRecUnitNum.id), tvAttention);
                }
            }
        });
    }

    public interface onAttentionListener {
        void onAttentionClick(int attention, int unitNumId, TextView tv);
    }

    public void setAttentionListener(onAttentionListener l) {
        mListener = l;
    }
}
