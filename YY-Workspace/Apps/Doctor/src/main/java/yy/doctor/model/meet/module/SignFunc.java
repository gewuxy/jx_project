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
import yy.doctor.dialog.SignErrDialog;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.ui.activity.meeting.SignActivityRouter;

/**
 * @auther yuansui
 * @since 2017/7/12
 */
public class SignFunc extends BaseFunc implements OnPermissionListener, OnLocationNotify {

    private PermissionOpt mPermission;
    private Gps mGps;

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
        return MeetAPI.toSign(getMeetId(), getModuleId()).build();
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
                SignActivityRouter.create(getMeetId(), getModuleId())
                        .signId(signData.getString(TSign.id))
                        .latitude(mGps.getString(TGps.latitude))
                        .longitude(mGps.getString(TGps.longitude))
                        .route(getContext());
            }
        } else {
            App.showToast(r.getMessage());
        }
    }

    @Override
    protected void attend() {
        if (mPermission.checkPermission(0, Permission.location)) {
            location();
        } else {

        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                location();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                locationError();
            }
            break;
        }
    }

    /**
     * 定位
     */
    private void location() {
        Location.inst().start();
        LocationNotifier.inst().add(this);
    }

    /**
     * 获取定位信息失败
     */
    private void locationError() {
        SignErrDialog dialog = new SignErrDialog(getContext());
        dialog.setLocationListener(v -> attend());
        dialog.show();
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        Location.inst().onDestroy();
        LocationNotifier.inst().remove(this);

        UtilEx.runOnUIThread(() -> {
            if (isSuccess) {
                //定位成功
                mGps = gps;
                super.attend();
            } else {
                //定位失败
                if (getListener() != null) {
                    getListener().onFuncNormal();
                }
                locationError();
            }
        }, ResLoader.getInteger(R.integer.anim_default_duration));
    }

}
