package yy.doctor.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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

    @Override
    protected void refreshView(int position, HomeUnitNumVH holder) {

        List<RecUnitNum> list = getData();
        holder.getTvName().setText(list.get(position).getString(TRecUnitNum.nickname));
        holder.getIv().placeHolder(R.mipmap.ic_default_home_unit_num).load();
        holder.getIv().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UnitNumDetailActivity.nav(getContext(), 8);
            }
        });

        holder.getNestCheckBox().setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.getNestCheckBox().getRealCheckBox().setText("已关注");
                } else {
                    holder.getNestCheckBox().getRealCheckBox().setText("关注");
                }
            }
        });
    }

    @Override
    protected int getConvertViewResId() {
        return R.layout.layout_home_unit_num_item;
    }
}
