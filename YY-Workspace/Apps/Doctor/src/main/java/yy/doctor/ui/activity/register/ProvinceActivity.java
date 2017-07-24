package yy.doctor.ui.activity.register;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.TextView;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class ProvinceActivity extends BasePcdActivity {

    private final int KPermissionCodeLocation = 10;


    private AnimationDrawable mAnimation;

    private BaseHintDialog mDialog;
    private TextView mProvinceTvChange;
    private OnLocationNotify mObserver;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.province, this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mProvinceTvChange = findView(R.id.province_tv_change);
    }

    @Override
    public void setViews() {
        super.setViews();

        //显示定位中布局
        showView(getLayoutLocation());
        getIvLocation().setImageResource(R.drawable.province_location_anim);
        mAnimation = (AnimationDrawable) getIvLocation().getDrawable();
        mAnimation.start();

        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(KPermissionCodeLocation, Permission.location)) {
            location();
        } else {
            YSLog.d("www", "location fail");
            //停止动画 隐藏定位中布局  显示无法定位布局  显示dialog
            mAnimation.stop();
            goneView(getLayoutLocation());
            setLocation(null);
            showLocDialog();
        }

        //item点击事件
        setOnAdapterClickListener((position, v) -> {
            Pcd province = getItem(position);
            CityActivity.nav(this, province.getString(TPcd.id), province.getString(TPcd.name), getLocation());
        });
        setOnClickListener(mProvinceTvChange);
    }

    private void showLocDialog() {
        mDialog = new BaseHintDialog(ProvinceActivity.this);
        mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
        mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
        mDialog.show();
    }

    //定位
    private void location() {
        YSLog.d("www", "location");
        mObserver = (isSuccess, gps) -> {
            //返回主线程更新ui
            runOnUIThread(() -> {
                //停止动画 隐藏定位中布局
                mAnimation.stop();
                goneView(getLayoutLocation());
                if (isSuccess) {
                    //定位成功
                    Place place = gps.getEv(TGps.place);
                    setLocation(Util.generatePcd(place.getString(TPlace.province), place.getString(TPlace.city), place.getString(TPlace.district)));
                } else {
                    //定位失败  显示dialog
                    //YSLog.d("Gps", "失败");
                    setLocation(null);
                    showLocDialog();
                }
                LocationNotifier.inst().remove(mObserver);
                Location.inst().onDestroy();
            });
        };
        Location.inst().start();
        LocationNotifier.inst().add(mObserver);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.province());
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.province_tv_change:
                startActivity(DistrictActivity.class); //点击更改跳转的页面
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
