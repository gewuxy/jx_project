package yy.doctor.model.meet.module;

import android.content.Context;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import yy.doctor.R;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;

/**
 * @auther yuansui
 * @since 2017/7/12
 */

public class SignFunc extends BaseFunc {


    public SignFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
    }

    @Override
    protected CharSequence getText() {
        return "签到";
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
    public void onClick() {

    }

    @Override
    protected NetworkReq getNetworkReq() {
        return null;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

    }
}
