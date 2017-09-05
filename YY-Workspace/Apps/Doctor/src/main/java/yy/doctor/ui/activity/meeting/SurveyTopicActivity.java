package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
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
    public void initData() {
        super.initData();

        notify(NotifyType.study_start);
    }

    @Override
    public void setViews() {
        super.setViews();

        refresh(RefreshWay.embed);
        exeNetworkReq(MeetAPI.toSurvey(mMeetId, mModuleId).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Intro.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Intro> r = (Result<Intro>) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            mIntro = r.getData();

            if (mIntro != null) {
                initFrag();
                invalidate();
            }

            // 第一次进入问卷时提示
            if (SpApp.inst().isFirstQue()) {
                addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mTopicPopup = new TopicPopup(SurveyTopicActivity.this);
                        mTopicPopup.setCheck(R.mipmap.que_popup_check);
                        mTopicPopup.setSlide(R.mipmap.que_popup_slide);
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
                .paperId(mPaper.getString(TPaper.id))
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
