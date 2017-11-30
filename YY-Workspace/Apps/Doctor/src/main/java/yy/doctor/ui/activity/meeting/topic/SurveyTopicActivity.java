package yy.doctor.ui.activity.meeting.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.topic.TopicIntro;
import yy.doctor.model.meet.topic.TopicPaper.TTopicPaper;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.MeetAPI;
import yy.doctor.popup.TopicPopup;
import yy.doctor.sp.SpApp;
import yy.doctor.util.Util;

/**
 * 问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */
public class SurveyTopicActivity extends BaseTopicActivity {

    private TopicPopup mTopicPopup;

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, SurveyTopicActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        bar.addTextViewMid(R.string.que);

        bar.addTextViewRight(R.string.submit, v -> {
            if (mTopics != null && mTopics.size() > 0) {
                toSubmit(mTopics.size() - mCount);
            }
        });
    }

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        notify(NotifyType.study_start);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        exeNetworkReq(MeetAPI.toSurvey(mMeetId, mModuleId).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), TopicIntro.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            mTopicIntro = (TopicIntro) r.getData();

            if (mTopicIntro != null) {
                initFrag();
                invalidate();
            }

            // 第一次进入问卷时提示
            if (SpApp.inst().isFirstQue()) {
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mTopicPopup = new TopicPopup(SurveyTopicActivity.this);
                        mTopicPopup.setCheck(R.drawable.que_popup_check);
                        mTopicPopup.setSlide(R.drawable.que_popup_slide);
                        mTopicPopup.showAtLocation(getNavBar(), Gravity.CENTER, 0, 0);
                        SpApp.inst().noFirstQue();
                        removeOnGlobalLayoutListener(this);
                    }
                });
            }

            initFirstGv();
        } else {
            setViewState(ViewState.error);
            showToast(r.getMessage());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    protected void submit() {
        if (Util.noNetwork()) {
            return;
        }
        SurveyEndActivityRouter.create(mMeetId, mModuleId)
                .paperId(mTopicPaper.getString(TTopicPaper.id))
                .topics(mTopics)
                .route(this);
        finish();
    }

    @Override
    protected String submitHint(int noFinish) {
        if (noFinish > 0) {
            //还有没作答
            return String.format(getString(R.string.que_submit_hint_no_finish), noFinish);
        } else {
            //全部作答完了
            return getString(R.string.que_submit_hint_finish);
        }
    }

    @Override
    protected String getExitHint() {
        return "问卷正在进行中，如果退出，您的答题将失效";
    }
}
