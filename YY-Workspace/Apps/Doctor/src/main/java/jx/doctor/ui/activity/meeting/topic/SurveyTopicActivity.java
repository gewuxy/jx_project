package jx.doctor.ui.activity.meeting.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.meet.topic.TopicIntro;
import jx.doctor.model.meet.topic.TopicPaper.TTopicPaper;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.sp.SpApp;
import jx.doctor.util.Util;
import lib.ys.util.view.LayoutUtil;

/**
 * 问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */
public class SurveyTopicActivity extends BaseTopicActivity {

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
                        View view = inflate(R.layout.layout_topic_hint);
                        ImageView ivS = view.findViewById(R.id.topic_hint_iv_scroll);
                        ImageView ivC = view.findViewById(R.id.topic_hint_iv_check);
                        ivS.setImageResource(R.drawable.que_topic_scroll);
                        ivC.setImageResource(R.drawable.que_topic_check);
                        view.setOnClickListener(v -> goneView(v));
                        getWindow().addContentView(view, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
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
