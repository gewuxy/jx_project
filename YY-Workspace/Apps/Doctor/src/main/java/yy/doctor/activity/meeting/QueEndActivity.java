package yy.doctor.activity.meeting;

import java.util.List;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import yy.doctor.Extra;
import yy.doctor.model.meet.exam.Answer;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 问卷结果界面
 *
 * @author : GuoXuan
 * @since : 2017/6/10
 */
public class QueEndActivity extends BaseResultActivity {

    private String mPaperId;
    private List<Answer> mAnswers;

    @Override
    public void initData() {
        super.initData();
        mPaperId = getIntent().getStringExtra(Extra.KPaperId);
        mAnswers = (List<Answer>) getIntent().getSerializableExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "问卷", this);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.submitSur()
                .meetId(mMeetId)
                .moduleId(mModuleId)
                .paperId(mPaperId)
                .items(mAnswers)
                .builder());
    }


    @Override
    protected void successResult() {
        hideView(mTvResultMsg);
        hideView(mTvWelcome);
        mTvResult.setText("感谢您的参与!");
    }

    @Override
    protected void errorResult(String error) {

    }
}
