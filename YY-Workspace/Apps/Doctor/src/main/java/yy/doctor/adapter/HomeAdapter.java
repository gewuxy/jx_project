package yy.doctor.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.adapter.MultiAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.IHome.HomeType;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecMeeting.TRecMeeting;
import yy.doctor.model.home.RecUnitNums;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiAdapterEx<IHome, HomeVH> {

    @Override
    protected void refreshView(int position, HomeVH holder, int itemType) {
        if (itemType == HomeType.meeting) {
            RecMeeting meeting = (RecMeeting)getItem(position);
            holder.getTvTitle().setText(meeting.getString(TRecMeeting.meetName));
        } else {
            RecUnitNums UnitNums = (RecUnitNums) getItem(position);
            HomeUnitNumAdapter mHomeUnitNumAdapter = new HomeUnitNumAdapter();
            mHomeUnitNumAdapter.setData(UnitNums.getData());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.getRecyclerView().setLayoutManager(linearLayoutManager);
            holder.getRecyclerView().setAdapter(mHomeUnitNumAdapter);
            holder.getRecyclerViewFooter().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showToast("8525");
                }
            });
        }
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = 0;
        switch (itemType) {
            case HomeType.meeting: {
                id = R.layout.layout_home_meeting_item;
            }
            break;
            case HomeType.unit_num: {
                id = R.layout.layout_home_recycler_view;
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
        return HomeType.class.getDeclaredFields().length;
    }

}
