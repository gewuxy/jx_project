package yy.doctor.activity.me.unitnum;

import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONException;

import lib.network.model.interfaces.IListResult;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import yy.doctor.R;
import yy.doctor.activity.BaseGroupIndexActivity;
import yy.doctor.activity.search.SearchActivity;
import yy.doctor.adapter.UnitNumAdapter;
import yy.doctor.model.unitnum.GroupUnitNum;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 单位号列表
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumActivity extends BaseGroupIndexActivity<GroupUnitNum, UnitNumAdapter> {

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.unit_num, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_add, v -> startActivity(SearchActivity.class));
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.unitNum());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        UnitNum item = (UnitNum) getChild(groupPosition, childPosition);
        YSLog.d(TAG, " item.getInt(TUnitNum.id) = " + item.getInt(TUnitNum.id));
        UnitNumDetailActivity.nav(this, item.getInt(TUnitNum.id));
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    @Override
    public IListResult<GroupUnitNum> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.groupIndex(text, GroupUnitNum.class, TUnitNum.alpha);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.attention_unit_num_empty);
    }
}
