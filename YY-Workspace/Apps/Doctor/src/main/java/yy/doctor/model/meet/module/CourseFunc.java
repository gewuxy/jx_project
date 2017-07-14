package yy.doctor.model.meet.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import yy.doctor.R;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;

/**
 * @auther yuansui
 * @since 2017/7/12
 */

public class CourseFunc extends BaseFunc {


    public CourseFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
    }

    @Override
    protected View initLayout(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.layout_meeting_module_course, null);
    }

    @Override
    protected CharSequence getText() {
        return null;
    }

    @Override
    protected int getImgResId() {
        return 0;
    }

    @Override
    public int getType() {
        return ModuleType.ppt;
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
