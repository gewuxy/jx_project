package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.LinearLayout;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/5/23
 */

public class ProvinceActivity extends BasePcdActivity {

    private final int KIdCommit = 1;

    private final int KPermissionCodeLocation = 10;

    private AnimationDrawable mAnimation;
    private BaseHintDialog mDialog;
    private LinearLayout mLinearLayout;
    private OnLocationNotify mObserver;
    private Place mPlace; // 定位信息

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
        mLinearLayout = findView(R.id.province_change);
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
        setOnClickListener(mLinearLayout);
    }

    private void showLocDialog() {
        mDialog = new BaseHintDialog(ProvinceActivity.this);
        mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
        mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
        mDialog.show();
    }

    //定位
    private void location() {
        mObserver = (isSuccess, gps) -> {
            //返回主线程更新ui
            runOnUIThread(() -> {
                //停止动画 隐藏定位中布局
                mAnimation.stop();
                goneView(getLayoutLocation());
                if (isSuccess) {
                    //定位成功
                    mPlace = gps.getEv(TGps.place);
                    setLocation(mPlace.toString());
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
        switch (v.getId()) {
            case R.id.province_change:
                if (Profile.inst().isLogin() && mPlace != null) {
                    // 返回个人中心页面
                    refresh(RefreshWay.dialog);
                    exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                            .province(mPlace.getString(TPlace.province))
                            .city(mPlace.getString(TPlace.city))
                            .area(mPlace.getString(TPlace.district))
                            .build());
                } else {
                    // 返回注册页面
                    Intent i = new Intent().putExtra(Extra.KData, mPlace);
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (KIdCommit == id) {
            return JsonParser.error(r.getText());
        }else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id== KIdCommit) {
            Result r = (Result) result;
            stopRefresh();
            if (r.isSucceed()) {
                Profile.inst().put(TProfile.province, mPlace.getString(TPlace.province));
                Profile.inst().put(TProfile.city, mPlace.getString(TPlace.city));
                Profile.inst().put(TProfile.zone, mPlace.getString(TPlace.district));
                Profile.inst().saveToSp();
               Intent i = new Intent().putExtra(Extra.KData, mPlace);
                setResult(RESULT_OK, i);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
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
