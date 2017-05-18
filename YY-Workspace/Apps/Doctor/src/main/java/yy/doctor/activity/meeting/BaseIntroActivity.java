package yy.doctor.activity.meeting;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TimeUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.exam.Intro;
import yy.doctor.network.JsonParser;

/**
 * 考试/问卷介绍界面
 *
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public abstract class BaseIntroActivity extends BaseActivity {

    protected String mMeetId;//会议ID
    protected String mModuleId;//模块ID

    protected TextView mTvTitle;  //试卷名称
    protected TextView mTvHost;   //主办方
    protected TextView mTvCount;  //总题数
    protected TextView mTvTime;   //考试时间

    @StringDef({
            TimeFormat.simple_ymd,
            TimeFormat.from_y_to_m_24,
            TimeFormat.from_h_to_m_24,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeFormat {
        String simple_ymd = "yyyy/MM/dd"; // 2016-08-21
        String from_y_to_m_24 = "yyyy/MM/dd HH:mm"; // 24h
        String from_h_to_m_24 = "HH:mm";
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_base_intro;
    }

    @Override
    public void findViews() {
        mTvTitle = findView(R.id.base_intro_tv_title);
        mTvHost = findView(R.id.base_intro_tv_host);
        mTvCount = findView(R.id.base_intro_tv_count);
        mTvTime = findView(R.id.base_intro_tv_time);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.exam_intro_tv_start);

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
        }

        refresh(RefreshWay.embed);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Intro.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result<Intro> r = (Result<Intro>) result;
        if (r.isSucceed()) {
            Intro intro = r.getData();
            Intro.setIntro(intro);
            success(intro);
        } else {
            showToast(r.getError());
        }

    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    /**
     * 按格式显示起始结束时间
     */
    protected String getTime(long startTime, long endTime) {
        StringBuilder time = null;
        String startDate = TimeUtil.formatMilli(startTime, TimeFormat.simple_ymd);
        String endDate = TimeUtil.formatMilli(endTime, TimeFormat.simple_ymd);
        if (startDate.equals(endDate)) {
            //同一天
            time = new StringBuilder(startDate)
                    .append(" ")
                    .append(TimeUtil.formatMilli(startTime, TimeFormat.from_h_to_m_24))
                    .append("~")
                    .append(TimeUtil.formatMilli(endTime, TimeFormat.from_h_to_m_24));
        } else {
            //不在同一天
            time = new StringBuilder()
                    .append(TimeUtil.formatMilli(startTime, TimeFormat.from_y_to_m_24))
                    .append("~")
                    .append(TimeUtil.formatMilli(endTime, TimeFormat.from_y_to_m_24));
        }
        return time.toString();
    }

    protected abstract void success(Intro intro);
}
