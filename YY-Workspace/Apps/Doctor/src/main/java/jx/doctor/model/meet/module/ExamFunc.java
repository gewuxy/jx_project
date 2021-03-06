package jx.doctor.model.meet.module;

import android.content.Context;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.AppEx;
import lib.jx.network.Result;
import jx.doctor.R;
import jx.doctor.model.meet.MeetDetail;
import jx.doctor.model.meet.MeetDetail.TMeetDetail;
import jx.doctor.model.meet.module.Module.ModuleType;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.ui.activity.meeting.topic.ExamIntroActivityRouter;

/**
 * 考试模块
 *
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
        return MeetAPI.toExam(getMeetId(), getModuleId()).build();
    }

    @Override
    public IResult onNetworkResponse(NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            ExamIntroActivityRouter.create()
                    .meetId(getMeetId())
                    .moduleId(getModuleId())
                    .host(getDetail().getString(TMeetDetail.organizer))
                    .route(getContext());
        } else {
            AppEx.showToast(r.getMessage());
        }
    }

}
