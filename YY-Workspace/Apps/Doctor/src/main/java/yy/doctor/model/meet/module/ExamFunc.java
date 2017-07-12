package yy.doctor.model.meet.module;

import android.content.Context;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.AppEx;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.meeting.ExamIntroActivity;

/**
 * @auther yuansui
 * @since 2017/7/12
 */

public class ExamFunc extends BaseFunc {

    public ExamFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
    }

    @Override
    protected CharSequence getText() {
        return "考试";
    }

    @Override
    protected int getImgResId() {
        return R.drawable.meeting_module_selector_exam;
    }

    @Override
    public int getType() {
        return ModuleType.exam;
    }

    @Override
    protected NetworkReq getNetworkReq() {
        return NetFactory.toExam(getDetail().getString(TMeetDetail.id), getModuleId());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            ExamIntroActivity.nav(getContext(),
                    getDetail().getString(TMeetDetail.id),
                    getModuleId(),
                    getDetail().getString(TMeetDetail.organizer));
        } else {
            AppEx.showToast(r.getError());
        }

        if (getListener() != null) {
            getListener().onFuncNormal(getType(), getModuleId());
        }
    }

}
