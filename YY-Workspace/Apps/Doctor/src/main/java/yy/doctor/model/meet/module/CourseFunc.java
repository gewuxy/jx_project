package yy.doctor.model.meet.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.yy.network.Result;
import yy.doctor.App;
import yy.doctor.R;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.ui.activity.meeting.MeetingCourseActivity;

/**
 * 微课模块
 *
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
    protected NetworkReq getNetworkReq() {
        return MeetAPI.toCourse(getMeetId(), getModuleId()).build();
    }

    @Override
    public Object onNetworkResponse(NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    protected void onNetworkSuccess(Object result) {
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {

            PPT ppt = r.getData();
            if (ppt == null) {
                App.showToast(R.string.course_no);
                return;
            }

            CourseInfo course = ppt.getEv(TPPT.course);
            if (course == null) {
                App.showToast(R.string.course_no);
                return;
            }

            List details = course.getList(TCourseInfo.details);
            if (details == null || details.size() == 0) {
                App.showToast(R.string.course_no);
            } else {
                MeetingCourseActivity.nav(getContext(), getMeetId(), getModuleId());
            }
        } else {
            App.showToast(r.getMessage());
        }
    }
}
