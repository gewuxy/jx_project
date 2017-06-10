package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.CommonTwoDialog;
import yy.doctor.dialog.CommonTwoDialog.OnLayoutListener;
import yy.doctor.frag.meeting.exam.TopicFrag;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.exam.Intro.TIntro;
import yy.doctor.model.meet.exam.Paper.TPaper;
import yy.doctor.model.meet.exam.Topic;
import yy.doctor.model.meet.exam.Topic.TTopic;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 问卷
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class QueTopicActivity extends BaseTopicActivity {

    private CommonTwoDialog mSubDialog;
    private TextView mBarRight;

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, QueTopicActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);
        mTvLeft.setText("问卷");
        mBarRight = bar.addTextViewRight("提交", v -> lastTopic(mAllTopics.size() - mCount));
    }

    @Override
    protected void topicCaseVisibility(boolean showState) {
        super.topicCaseVisibility(showState);
        if (getTopicCaseShow()) {
            mTvLeft.setText("题目");
            goneView(mBarRight);
        } else {
            mTvLeft.setText("问卷");
            showView(mBarRight);
        }
    }

    @Override
    public void setViews() {
        super.setViews();
        refresh(RefreshWay.embed);
        exeNetworkReq(NetFactory.toSurvey(mMeetId, mModuleId));
    }

    @Override
    protected void submit() {
        showToast("提交重写");
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Intro.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Intro> r = (Result<Intro>) result;
        setViewState(ViewState.normal);
        if (r.isSucceed()) {
            mIntro = r.getData();
            mPaper = mIntro.getEv(TIntro.paper);
            mPaperId = mPaper.getString(TPaper.id);
            mAllTopics = mPaper.getList(TPaper.questions);

            TopicFrag topicFrag = null;
            int size = mAllTopics.size();

            LogMgr.d(TAG, "onNetworkSuccess: all = " + size);

            for (int i = 0; i < size; i++) {
                topicFrag = new TopicFrag();
                Topic topic = mAllTopics.get(i);
                topicFrag.setTopic(topic);
                //最后一题
                if (i == mAllTopics.size() - 1) {
                    topicFrag.isLast();
                }
                topicFrag.setOnNextListener(v -> {
                    getAnswer(mAllTopics);
                    if (getCurrentItem() < size - 1) {
                        setCurrentItem(getCurrentItem() + 1);
                    } else {
                        lastTopic(size - mCount);
                    }
                });
                add(topicFrag);
                invalidate();
            }

            if (size != 0) {
                String topicId = mAllTopics.get(0).getString(TTopic.sort);
                String topic = topicId + "/" + size;
                mTvAll.setText(topic);
                mTvNavAll.setText(topic);
            }
            setGv();
        } else {
            showToast(r.getError());
        }

    }

    @Override
    protected void lastTopic(int noFinish) {
        //考试时间未完
        if (mSubDialog == null) {
            mSubDialog = new CommonTwoDialog(QueTopicActivity.this);
        }
        if (noFinish > 0) {
            //还有没作答
            mSubDialog.setTvMainHint("还有" + noFinish + "题未完成")
                    .setTvSecondaryHint("是否确认提交问卷?");
        } else {
            //全部作答完了
            mSubDialog.setTvMainHint("确定提交问卷?")
                    .hideSecond();
        }
        mSubDialog.mTvLeft(getString(R.string.exam_submit_sure))
                .mTvRight(getString(R.string.exam_continue))
                .setLayoutListener(new OnLayoutListener() {
                    @Override
                    public void leftClick(View v) {
                        submit();
                    }

                    @Override
                    public void rightClick(View v) {

                    }
                });
        mSubDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubDialog != null) {
            if (mSubDialog.isShowing()) {
                mSubDialog.dismiss();
            }
            mSubDialog = null;
        }
    }

}
