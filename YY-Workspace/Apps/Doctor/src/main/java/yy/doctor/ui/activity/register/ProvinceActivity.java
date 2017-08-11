package yy.doctor.ui.activity.register;

import android.graphics.drawable.AnimationDrawable;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
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

    private AnimationDrawable mAnimation;
    private BaseHintDialog mDialog;
    private OnLocationNotify mObserver;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.province, this);
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
        if (checkPermission(0, Permission.location)) {
            location();
        }

        //item点击事件
        setOnAdapterClickListener((position, v) -> {
            Pcd item = getItem(position);
            CityActivityIntent.create()
                    .provinceId(item.getString(TPcd.id))
                    .province(item.getString(TPcd.name))
                    .pcdDesc(getLocation())
                    .place(mPlace)
                    .start(this);
        });
    }

    private void showLocDialog() {
        if (mDialog == null) {
            mDialog = new BaseHintDialog(ProvinceActivity.this);
            mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
            mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
        }
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

//    @Override
//    public void onNotify(@NotifyType int type, Object data) {
//
//        if (type == NotifyType.province_finish) {
//            finish();
//        }
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.province_change:
//                if (Profile.inst().isLogin() && mPlace != null) {
//                    // 返回个人中心页面
//                    refresh(RefreshWay.dialog);
//                    exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
//                            .province(mPlace.getString(TPlace.province))
//                            .city(mPlace.getString(TPlace.city))
//                            .area(mPlace.getString(TPlace.district))
//                            .build());
//                } else {
//                    // 返回注册页面
//                    Intent i = new Intent().putExtra(Extra.KData, mPlace);
//                    setResult(RESULT_OK, i);
//                    finish();
//                }
//                break;
//        }
//    }

//    @Override
//    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
//        if (KIdCommit == id) {
//            return JsonParser.error(r.getText());
//        }else {
//            return super.onNetworkResponse(id, r);
//        }
//    }
//
//    @Override
//    public void onNetworkSuccess(int id, Object result) {
//        if (id== KIdCommit) {
//            Result r = (Result) result;
//            stopRefresh();
//            if (r.isSucceed()) {
//                Profile.inst().put(TProfile.province, mPlace.getString(TPlace.province));
//                Profile.inst().put(TProfile.city, mPlace.getString(TPlace.city));
//                Profile.inst().put(TProfile.zone, mPlace.getString(TPlace.district));
//                Profile.inst().saveToSp();
//               Intent i = new Intent().putExtra(Extra.KData, mPlace);
//                setResult(RESULT_OK, i);
//                finish();
//            } else {
//                onNetworkError(id, r.getError());
//            }
//        } else {
//            super.onNetworkSuccess(id, result);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        if (result == PermissionResult.granted) {
            location();
        } else {
            YSLog.d("www", "location fail");
            //停止动画 隐藏定位中布局  显示无法定位布局  显示dialog
            mAnimation.stop();
            goneView(getLayoutLocation());
            setLocation(null);
            showLocDialog();
        }
    }
}
