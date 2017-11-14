package yy.doctor.model.meet.module;

import android.content.Context;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.AppEx;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.ui.activity.meeting.topic.SurveyTopicActivity;

/**
 * 问卷模块
 *
 * @auther yuansui
 * @since 2017/7/12
 */
public class SurveyFunc extends BaseFunc {

    public SurveyFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
    }

    @Override
    protected CharSequence getText() {
        return getContext().getString(R.string.que);
    }

    @Override
    protected int getImgResId() {
        return R.drawable.meeting_module_selector_que;
    }

    @Override
    public int getType() {
        return ModuleType.que;
    }

    @Override
    protected NetworkReq getNetworkReq() {
        return MeetAPI.toSurvey(getMeetId(), getModuleId()).build();
    }

    @Override
    public IResult onNetworkResponse(NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            SurveyTopicActivity.nav(getContext(), getMeetId(), getModuleId());
        } else {
            AppEx.showToast(r.getMessage());
        }
    }
}
