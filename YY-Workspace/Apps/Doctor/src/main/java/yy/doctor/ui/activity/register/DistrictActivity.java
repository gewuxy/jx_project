package yy.doctor.ui.activity.register;

import android.content.Context;
import android.content.Intent;

import lib.processor.annotation.AutoIntent;
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
@AutoIntent
public class DistrictActivity extends BasePcdLevel2Activity {

    /**
     * @param context
     * @param id       城市id
     * @param province
     * @param city
     */
//    public static void nav(Context context, String id, String province, String city, String pcdDesc) {
//        Intent i = new Intent(context, DistrictActivity.class)
//                .putExtra(Extra.KPcdDesc, pcdDesc)
//                .putExtra(Extra.KCityId, id)
//                .putExtra(Extra.KCity, city)
//                .putExtra(Extra.KProvince, province);
//        LaunchUtil.startActivity(context, i);
//    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.district, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.city(mCityId));
    }
}
