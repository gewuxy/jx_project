package jx.csp.ui.activity.main;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.contact.StarContract;
import jx.csp.dialog.CommonDialog;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.presenter.star.LiveStarPresenterImpl;
import jx.csp.serv.CommonServ;
import jx.csp.serv.CommonServRouter;
import jx.csp.util.Util;
import lib.network.model.interfaces.IResult;
import lib.ys.fitter.Fitter;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.util.TimeFormatter;
import lib.ys.util.res.ResLoader;

/**
 * 直播星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
@Route
public class LiveStarActivity extends BaseStarActivity {

    private TextView mTvFinishHint; // 结束提示
    private TextView mTvTimeHint; // 演讲提示
    private TextView mTvToFinishTime; // 直播时间
    private TextView mTvFinishTime; // 直播时间

    private View mLayoutFinish;
    private View mLayoutToFinish;

    private View mTvEndLive;
    private long mTime;
    private long mExpireDate = Constants.KInvalidValue;

    @Override
    protected StarContract.P createPresenterImpl() {
        return new LiveStarPresenterImpl(createViewImpl(), mMeet);
    }

    @Override
    protected StarContract.V createViewImpl() {
        return new LiveStarViewImpl();
    }

    @Override
    protected void barBack() {
        if (mLayoutToFinish.getVisibility() == View.VISIBLE) {
            mTvEndLive.performClick();
        } else {
            finish();
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_star_live;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutToFinish = findView(R.id.star_layout_to_finish);
        mLayoutFinish = findView(R.id.star_layout_finish);

        mTvEndLive = findView(R.id.star_tv_live_end);
        mTvFinishHint = findView(R.id.star_tv_finish);
        mTvTimeHint = findView(R.id.star_tv_finish_time_hint);
        mTvFinishTime = findView(R.id.star_tv_finish_all_time);
        mTvToFinishTime = findView(R.id.star_tv_to_finish_all_time);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mTvEndLive);
        ViewGroup.LayoutParams params = mLayoutDefault.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
            p.topMargin = Fitter.dp(165);
            mLayoutDefault.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.star_tv_live_end: {
                CommonDialog d = new CommonDialog(this);
                d.addHintView(inflate(R.layout.layout_live_end));
                d.addButton(R.string.cancel, R.color.text_333, null);
                d.addButton(R.string.affirm, R.color.text_333, l -> {
                    CommonServRouter.create(CommonServ.ReqType.over_live)
                            .courseId(mMeet.getString(TMeet.id))
                            .route(LiveStarActivity.this);
                    setState(LiveState.to_end);
                });
                d.show();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        barBack();
    }

    /**
     * 直播状态对应的界面
     *
     * @param state 直播状态
     */
    private void setState(@LiveState int state) {
        switch (state) {
            case LiveState.un_start:
            case LiveState.live:
            case LiveState.stop:
            case LiveState.star: {
                showView(mLayoutToFinish);
                goneView(mLayoutFinish);
                String time = Util.getSpecialTimeFormat(mTime / TimeUnit.SECONDS.toMillis(1), "'", "\"");
                mTvToFinishTime.setText(time);
            }
            break;
            case LiveState.to_end: {
                goneView(mLayoutToFinish);
                showView(mLayoutFinish);

                String meetTime = Util.getSpecialTimeFormat(mTime / TimeUnit.SECONDS.toMillis(1), ":", Constants.KEmpty);
                mTvTimeHint.setText(String.format(getString(R.string.meet_time), meetTime));

                goneView(mTvFinishTime);

                String finishTime = getFinishTime();
                mTvFinishHint.setText(String.format(getString(R.string.live_end_after), finishTime));
                mTvFinishHint.setTextColor(ResLoader.getColor(R.color.text_ace400));
            }
            break;
            case LiveState.end: {
                goneView(mLayoutToFinish);
                showView(mLayoutFinish);

                mTvTimeHint.setText(R.string.start_all_time);

                String time = Util.getSpecialTimeFormat(mTime / TimeUnit.SECONDS.toMillis(1), "'", "\"");
                mTvFinishTime.setText(time);
                showView(mTvFinishTime);

                mTvFinishHint.setText(R.string.end_live);
                mTvFinishHint.setTextColor(ResLoader.getColor(R.color.text_787c86));
            }
            break;
        }
    }

    public String getFinishTime() {
        if (mExpireDate == Constants.KInvalidValue) {
            return getString(R.string.live_default_finish);
        } else {
            return TimeFormatter.milli(mExpireDate, TimeFormatter.TimeFormat.from_h_to_m_24);
        }
    }

    private class LiveStarViewImpl extends BaseStarViewImpl implements StarContract.V {

        @Override
        public void onNetworkSuccess(int id, IResult r) {
            if (id == StarContract.KReqStar) {
                if (r.isSucceed()) {
                    setViewState(DecorViewEx.ViewState.normal);
                    Code c = (Code) r.getData();
                    if (c == null) {
                        return;
                    }
                    // 讲本时长
                    long serverTime = c.getLong(Code.TCode.serverTime);
                    if (serverTime > mMeet.getLong(TMeet.startTime)) {
                        mTime = serverTime - mMeet.getLong(TMeet.startTime);
                    } else {
                        mTime = 0;
                    }
                    setState(mMeet.getInt(TMeet.liveState));
                    mExpireDate = c.getLong(Code.TCode.expireDate);

                    // 星评二维码
                    boolean startStatus = c.getBoolean(Code.TCode.starStatus);
                    if (startStatus) {
                        // 有星评
                        if (mMeet.getInt(TMeet.liveState) != LiveState.end) {
                            setDataMatrix(c.getString(Code.TCode.startCodeUrl));
                            starState(true);
                        } else {
                            starState(false);
                        }
                    } else {
                        // 没有星评
                        if (mMeet.getInt(TMeet.liveState) != LiveState.end && mMeet.getInt(TMeet.liveState) != LiveState.to_end) {
                            CommonServRouter.create(CommonServ.ReqType.over_live)
                                    .courseId(mMeet.getString(TMeet.id))
                                    .route(LiveStarActivity.this);
                        }
                        starState(false);
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
        }

    }

}
