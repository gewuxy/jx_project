package jx.doctor.model.meet.module;

import android.content.Context;

import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import jx.doctor.R;
import jx.doctor.model.meet.MeetDetail;
import jx.doctor.model.meet.Submit;
import jx.doctor.model.meet.Submit.TSubmit;
import jx.doctor.model.meet.module.Module.ModuleType;
import jx.doctor.ui.activity.meeting.VideoCategoryActivity;

/**
 * 不用先请求网络数据
 *
 * @auther yuansui
 * @since 2017/7/12
 */
public class VideoFunc extends BaseFunc {

    public VideoFunc(Context context, MeetDetail detail, OnFuncListener l) {
        super(context, detail, l);
    }

    @Override
    protected CharSequence getText() {
        return getContext().getString(R.string.video);
    }

    @Override
    protected int getImgResId() {
        return R.drawable.meeting_module_selector_video;
    }

    @Override
    public int getType() {
        return ModuleType.video;
    }

    @Override
    protected NetworkReq getNetworkReq() {
        return null;
    }

    @Override
    public IResult onNetworkResponse(NetworkResp r) throws Exception {
        return null;
    }

    @Override
    public void onNetworkSuccess(Object result) {
    }

    @Override
    protected void attend() {
        Submit submit = new Submit()
                .put(TSubmit.meetId, getMeetId())
                .put(TSubmit.moduleId, getModuleId());
        VideoCategoryActivity.nav(getContext(), submit, null);
    }
}
