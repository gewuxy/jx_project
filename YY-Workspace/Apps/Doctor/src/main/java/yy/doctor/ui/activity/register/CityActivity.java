package yy.doctor.ui.activity.register;

import android.content.Context;
import android.content.Intent;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class CityActivity extends BasePcdLevel2Activity {

    /**
     * @param context
     * @param pId      省份id
     * @param province
     * @param pcdDesc
     */
    public static void nav(Context context, String pId, String province, String pcdDesc) {
        Intent i = new Intent(context, CityActivity.class)
                .putExtra(Extra.KPcdDesc, pcdDesc)
                .putExtra(Extra.KProvinceId, pId)
                .putExtra(Extra.KProvince, province);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.city, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mProvinceId));
    }
}
