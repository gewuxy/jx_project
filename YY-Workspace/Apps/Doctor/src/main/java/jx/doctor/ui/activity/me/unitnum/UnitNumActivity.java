package jx.doctor.ui.activity.me.unitnum;

import android.view.View;
import android.widget.ExpandableListView;

import org.json.JSONException;

import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.R;
import jx.doctor.adapter.me.UnitNumAdapter;
import jx.doctor.model.unitnum.GroupUnitNum;
import jx.doctor.model.unitnum.UnitNum;
import jx.doctor.model.unitnum.UnitNum.TUnitNum;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.UnitNumAPI;
import jx.doctor.ui.activity.BaseGroupIndexActivity;
import jx.doctor.ui.activity.search.SearchActivity;
import jx.doctor.util.Util;

/**
 * 单位号列表
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class UnitNumActivity extends BaseGroupIndexActivity<GroupUnitNum, UnitNum, UnitNumAdapter> {

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.unit_num, this);
        bar.addViewRight(R.drawable.nav_bar_ic_add, v -> startActivity(SearchActivity.class));
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(UnitNumAPI.attentionUnitNum().build());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return true;
    }

    @Override
    public IResult<GroupUnitNum> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.groupIndex(text, GroupUnitNum.class, TUnitNum.alpha);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.empty_attention_unit_num);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消关注后，单位号列表也要删除对应单位号
        if (type == NotifyType.cancel_attention) {
            int unitNumId = (int) data;
            for (int i = 0; i < getGroupCount(); i++) {
                int childCount = getGroup(i).getChildrenCount();
                YSLog.d(TAG, "childCount = " + childCount);
                for (int j = 0; j < childCount; ++j) {
                    UnitNum item = getChild(i, j);
                    if (item.getInt(TUnitNum.id) == unitNumId) {
                        //如果childCount == 1, 整个组都移除
                        if (childCount == 1) {
                            YSLog.d(TAG, "remove group " + i);
                            remove(i);
                        } else {
                            getGroup(i).removeChild(j);
                        }
                        invalidate();
                        return;
                    }
                }
            }
        }

    }

}
