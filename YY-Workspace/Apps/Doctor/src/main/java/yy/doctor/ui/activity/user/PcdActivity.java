package yy.doctor.ui.activity.user;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.bd.location.Gps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.view.ProgressView;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.PcdAdapter;
import yy.doctor.dialog.LocateErrDialog;
import yy.doctor.model.Pcd;
import yy.doctor.model.Pcd.TPcd;
import yy.doctor.model.Place;
import yy.doctor.model.Place.TPlace;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;


/**
 * 省市区
 *
 * @auther yuansui
 * @since 2017/8/11
 */
@Route
public class PcdActivity extends BaseSRListActivity<Pcd, PcdAdapter> implements OnLocationNotify {

    private final int KIdCommit = 1;

    private static final String KLocating = "定位中...";
    private static final String KLocateError = "无法获取当前的位置信息";

    private ProgressView mIvProgress;
    private TextView mTvLocation;

    @Nullable
    @Arg(optional = true)
    Place mLocatePlace; // 定位的地点

    @Arg(optional = true)
    ArrayList<String> mSelects; // 选择的地点

    @Arg(optional = true)
    String mPreId; // 上一级选中的id

    private Place mRetPlace;

    private LocateErrDialog mDialog;


    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "省市区", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mIvProgress = findView(R.id.pcd_header_iv_progress);
        mTvLocation = findView(R.id.pcd_header_tv_location);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.pcd_header_layout_location);
        setAutoLoadMoreEnabled(false);

        //检查有没有定位权限   没有的话直接弹dialog
        if (mPreId == null) {
            if (checkPermission(0, Permission.location)) {
                startLocation();
            }
        } else {
            // 设置位置
            if (mLocatePlace == null) {
                locateError();
            } else {
                locateSuccess(mLocatePlace.getDesc());
            }
        }

        setOnAdapterClickListener((position, v) -> {
            Pcd item = getItem(position);
            if (mSelects == null) {
                mSelects = new ArrayList<>();
            }
            mSelects.add(item.getString(TPcd.name));

            if (item.getInt(TPcd.level) != Pcd.KLevelEnd) {
                PcdActivityRouter.create()
                        .locatePlace(mLocatePlace)
                        .selects(mSelects)
                        .preId(item.getString(TPcd.id))
                        .route(this);
            } else {
                Place place = new Place(mSelects);
                if (Profile.inst().isLogin()) {
                    exeCommitLocationReq(place);
                } else {
                    notify(NotifyType.pcd_selected, place);

                    Intent intent = new Intent()
                            .putExtra(yy.doctor.Extra.KData, place);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        mTvLocation.setText(KLocating);

        LocationNotifier.inst().add(this);
        mIvProgress.start();
        Location.inst().start();
    }

    @Override
    public void getDataFromNet() {
        if (mPreId == null) {
            exeNetworkReq(NetFactory.province());
        } else {
            exeNetworkReq(NetFactory.city(mPreId));
        }
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_pcd_header);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (KIdCommit == id) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pcd_header_layout_location:
                if (Profile.inst().isLogin() && mLocatePlace != null) {
                    // 返回个人中心页面
                    exeCommitLocationReq(mLocatePlace);
                } else {
                    if (mLocatePlace == null) {
                        // 定位失败情况不响应点击
                    } else {
                        // 返回注册页面
                        notify(NotifyType.pcd_selected, mLocatePlace);

                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdCommit) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                Profile.inst().put(mRetPlace);
                Profile.inst().saveToSp();

                stopRefresh();

                notify(NotifyType.pcd_selected, mRetPlace);

                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.pcd_selected) {
            finish();
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        if (result == PermissionResult.granted) {
            startLocation();
        } else {
            locateError();
        }
    }

    /**
     * 定位成功
     *
     * @param location
     */
    private void locateSuccess(String location) {
        mTvLocation.setText(location);

        mIvProgress.stop();
        goneView(mIvProgress);
    }

    /**
     * 定位失败
     */
    private void locateError() {
        mTvLocation.setText(KLocateError);

        mIvProgress.stop();
        mIvProgress.setImageResource(R.mipmap.pcd_ic_locate_err);

        if (mPreId == null) {
            showLocateErrDialog();
        }
    }

    private void showLocateErrDialog() {
        if (mDialog == null) {
            mDialog = new LocateErrDialog(this);
        }
        mDialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startLocation();
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        Location.inst().onDestroy();
        LocationNotifier.inst().remove(this);

        //返回主线程更新ui
        runOnUIThread(() -> {
            //停止动画
            if (isSuccess) {
                //定位成功
                mLocatePlace = new Place(gps);
                locateSuccess(mLocatePlace.getDesc());
            } else {
                //定位失败  显示dialog
                locateError();
            }
        });
    }

    private void exeCommitLocationReq(Place place) {
        mRetPlace = place;

        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                .province(place.getString(TPlace.province))
                .city(place.getString(TPlace.city))
                .area(place.getString(TPlace.district))
                .build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
