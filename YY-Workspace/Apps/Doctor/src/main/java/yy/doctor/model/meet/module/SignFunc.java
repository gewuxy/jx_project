package yy.doctor.model.meet.module;

import android.content.Context;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.ui.interfaces.impl.PermissionOpt;
import lib.ys.util.UtilEx;
import lib.ys.util.permission.OnPermissionListener;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import yy.doctor.App;
import yy.doctor.R;
import yy.doctor.dialog.LocationDialog;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.meeting.SignActivity;

/**
 * @auther yuansui
 * @since 2017/7/12
 */
public class SignFunc extends BaseFunc implements OnPermissionListener {

    private PermissionOpt mPermission;
    private OnLocationNotify mObserver;
    private Gps mGps;
    private LocationDialog mLocationDialog;

    public SignFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
        mPermission = new PermissionOpt(context, this);
    }

    @Override
    protected CharSequence getText() {
        return getContext().getString(R.string.sign);
    }

    @Override
    protected int getImgResId() {
        return R.drawable.meeting_module_selector_sign;
    }

    @Override
    public int getType() {
        return ModuleType.sign;
    }

    @Override
    protected NetworkReq getNetworkReq() {
        return NetFactory.toSign(getMeetId(), getModuleId());
    }

    @Override
    public Object onNetworkResponse(NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Sign.class);
    }

    @Override
    public void onNetworkSuccess(Object result) {
        Result<Sign> r = (Result<Sign>) result;
        if (r.isSucceed()) {
            Sign signData = r.getData();
            if (signData.getBoolean(TSign.finished)) {
                // 提示已签到
                App.showToast(R.string.signed);
            } else {
                // 未签到去签到
                if (mGps == null) {
                    mGps = new Gps();
                }
                SignActivity.nav(getContext(),
                        getMeetId(),
                        getModuleId(),
                        signData.getString(TSign.id),
                        mGps.getString(TGps.latitude),
                        mGps.getString(TGps.longitude));
            }
        } else {
            App.showToast(r.getError());
        }
    }

    @Override
    protected void attend() {
        if (mPermission.checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
            getSignInfo();
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                getSignInfo();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                showLocationDialog();
            }
            break;
        }
    }

    /**
     * 初始化Dialog
     */
    private void showLocationDialog() {
        mLocationDialog = new LocationDialog(getContext());
        mLocationDialog.setLocationListener(v -> attend());
        mLocationDialog.show();
    }

    /**
     * 签到
     */
    private void getSignInfo() {
        if (getListener() != null) {
            getListener().onFuncLoading(getType(), getModuleId());
        }
        // FIXME: rxjava
        mObserver = (isSuccess, gps) -> {
            UtilEx.runOnUIThread(() -> {
                if (isSuccess) {
                    //定位成功
                    mGps = gps;
                    super.attend();
                } else {
                    //定位失败
                    if (getListener() != null) {
                        getListener().onFuncNormal(getType(), getModuleId());
                    }
                    showLocationDialog();
                }
            }, ResLoader.getInteger(R.integer.anim_default_duration));
            LocationNotifier.inst().remove(mObserver);
            Location.inst().onDestroy();
        };
        Location.inst().start();
        LocationNotifier.inst().add(mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationDialog != null) {
            mLocationDialog.dismiss();
        }
        mObserver = null;
    }
}
