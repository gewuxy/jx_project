package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
import yy.doctor.adapter.UnitNumDataAdapter;
import yy.doctor.model.unitnum.UnitNumDetailData;
import yy.doctor.model.unitnum.UnitNumDetailData.TUnitNumDetailData;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 单位号资料
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class UnitNumDataActivity extends BaseListActivity<UnitNumDetailData, UnitNumDataAdapter> {

    private int mId;

    public static void nav(Context context, int id) {
        Intent i = new Intent(context, UnitNumDataActivity.class)
                .putExtra(Extra.KData, id);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mId = getIntent().getIntExtra(Extra.KData, 10);
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "资料", this);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.unitNumData(mId, 1, 15));
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        UnitNumDetailData item = getItem(position);
        DownloadDataActivity.nav(this, mId, item.getString(TUnitNumDetailData.materialName),
                item.getString(TUnitNumDetailData.materialUrl), item.getString(TUnitNumDetailData.materialType),
                item.getLong(TUnitNumDetailData.fileSize));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.evs(r.getText(), UnitNumDetailData.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        stopRefresh();
        ListResult<UnitNumDetailData> r = (ListResult<UnitNumDetailData>) result;
        if (r.isSucceed()) {
            setData(r.getData());
            invalidate();
        }
    }

}
