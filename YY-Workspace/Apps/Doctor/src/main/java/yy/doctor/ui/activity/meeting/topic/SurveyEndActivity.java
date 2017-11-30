package yy.doctor.ui.activity.meeting.topic;

import java.util.ArrayList;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkReq;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import yy.doctor.R;
import yy.doctor.model.meet.topic.Topic;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.ui.activity.meeting.BaseResultActivity;
import yy.doctor.util.Util;

/**
 * 问卷结果界面
 *
 * @author : GuoXuan
 * @since : 2017/6/10
 */
@Route
public class SurveyEndActivity extends BaseResultActivity {

    @Arg(opt = true)
    String mPaperId;

    @Arg(opt = true)
    ArrayList<Topic> mTopics;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.que, this);
    }

    @Override
    protected void getDataFromNet() {
        refresh(RefreshWay.embed);
        NetworkReq r = MeetAPI.submitSurvey()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .paperId(mPaperId)
                .itemJson(Util.chooseToJson(mTopics))
                .build();
        exeNetworkReq(r);
    }

    @Override
    protected void successResult() {
        hideView(mTvResultMsg);
        hideView(mTvWelcome);
        mTvResult.setText(R.string.thank_join);
    }

    @Override
    protected void errorResult(String error) {
    }
}
