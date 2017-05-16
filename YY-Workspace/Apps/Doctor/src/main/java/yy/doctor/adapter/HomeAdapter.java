package yy.doctor.adapter;

import android.support.annotation.IntDef;

import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.HomeVH;


/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class HomeAdapter extends MultiRecyclerAdapterEx<String, HomeVH> {

    @IntDef({
            HomeType.meeting,
            HomeType.unit_num_header,
            HomeType.unit_num,
            HomeType.unit_num_footer,
    })
    public @interface HomeType {
        int meeting = 0;
        int unit_num_header = 1;
        int unit_num = 2;
        int unit_num_footer = 3;
    }

    @Override
    protected void refreshView(int position, HomeVH holder, int itemType) {

    }

    @Override
    public int getConvertViewResId(int itemType) {
        int id = 0;

        switch (itemType) {
            case HomeType.meeting: {
                id = R.layout.layout_home_meeting_item;
            }
            break;
            case HomeType.unit_num_header: {
                id = R.layout.layout_home_unit_num_header;
            }
            break;
            case HomeType.unit_num: {
                id = R.layout.layout_home_unit_num_item;
            }
            break;
            case HomeType.unit_num_footer: {
                id = R.layout.layout_home_unit_num_footer;
            }
            break;
        }

        return id;
    }

    @Override
    public int getItemViewType(int position) {
        //return getItem(position).getInt(THome.re_type);
        return 0;
    }

}
