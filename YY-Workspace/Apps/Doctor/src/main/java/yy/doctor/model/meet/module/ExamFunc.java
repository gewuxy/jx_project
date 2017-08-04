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
import yy.doctor.ui.activity.meeting.ExamIntroActivityIntent;

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
        return getContext().getString(R.string.exam);
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
        return NetFactory.toExam(getMeetId(), getModuleId());
    }

    @Override
    public Object onNetworkResponse(NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            ExamIntroActivityIntent.create()
                    .meetId(getMeetId())
                    .moduleId(getModuleId())
                    .host(getDetail().getString(TMeetDetail.organizer))
                    .start(getContext());
        } else {
            AppEx.showToast(r.getError());
        }
    }

}
