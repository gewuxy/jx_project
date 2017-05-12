package yy.doctor.adapter;

import android.support.annotation.IntDef;

import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HomeVH;
import yy.doctor.model.home.Home;
import yy.doctor.model.home.Home.THome;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiRecyclerAdapterEx<Home, HomeVH> {

    @IntDef({
            HomeType.meeting,
            HomeType.tv_divider,
            HomeType.public_num,
    })
    public @interface HomeType {
        int meeting = 0;
        int tv_divider = 1;
        int public_num = 2;
    }

    @Override
    protected void refreshView(int position, HomeVH holder, int itemType) {
        getItem(position).refresh(holder);
    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = 0;

        switch (itemType) {
            case HomeType.meeting: {
                id = R.layout.layout_home_meeting_item;
            }
            break;
            case HomeType.tv_divider: {
                id = R.layout.layout_home_unit_num_header;
            }
            break;
            case HomeType.public_num: {
                id = R.layout.layout_home_unit_num;
            }
            break;
        }

        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getInt(THome.re_type);
    }

}
