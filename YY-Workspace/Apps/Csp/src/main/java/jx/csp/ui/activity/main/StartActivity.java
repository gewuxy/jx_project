package jx.csp.ui.activity.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.StartContract;
import jx.csp.dialog.CommonDialog;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Live;
import jx.csp.presenter.StartPresenterImpl;
import jx.csp.serv.CommonServ;
import jx.csp.serv.CommonServRouter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;

/**
 * 星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
@Route
public class StartActivity extends BaseActivity {

    @Arg
    Meet mMeet;

    private TextView mTvFinish;
    private TextView mTvAll;
    private View mLayoutDefault;
    private View mLayoutDataMatrix;
    private NetworkImageView mIvDataMatrix;
    private View mTvLive;
    private View mLayoutTime;

    private StartContract.P mP;
    private StartContract.V mV;

    @Override
    public void initData() {
        mV = new StartContractViewImpl();
        mP = new StartPresenterImpl(mV, mMeet);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_start;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.drawable.default_ic_close, this);
        boolean start = mMeet.getBoolean(Meet.TMeet.starRateFlag);
        bar.addTextViewMid(start ? R.string.start_comment : R.string.start_finish);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog d = new ShareDialog(StartActivity.this, mMeet);
            d.show();
        });

        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        mLayoutDefault = findView(R.id.start_layout_meet_default);
        mLayoutDataMatrix = findView(R.id.start_layout_data_matrix);
        mLayoutTime = findView(R.id.start_layout_all_time);
        mIvDataMatrix = findView(R.id.start_iv_data_matrix);
        mTvFinish = findView(R.id.start_tv_finish);
        mTvAll = findView(R.id.start_tv_all_time);
        mTvLive = findView(R.id.start_tv_live_end);
    }

    @Override
    public void setViews() {
        mP.setPlayState();

        refresh(AppConfig.RefreshWay.embed);
        mP.getDataFromNet();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_tv_live_end: {
                CommonDialog d = new CommonDialog(this);
                d.addHintView(inflate(R.layout.layout_live_end));
                d.addButton(R.string.cancel, R.color.text_333, null);
                d.addButton(R.string.affirm, R.color.text_333, l -> {
                    CommonServRouter.create(CommonServ.ReqType.over_live)
                            .courseId(mMeet.getString(Meet.TMeet.id))
                            .route(StartActivity.this);
                    showView(mTvFinish);
                    goneView(mTvLive);
                    changeTimeLocation(100);
                });
                d.show();
            }
            break;

        }

    }

    /**
     * 改变时间布局
     *
     * @param dp dp底部
     */
    private void changeTimeLocation(int dp) {
        ViewGroup.LayoutParams params = mLayoutTime.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            p.bottomMargin = fit(dp);
            mLayoutTime.setLayoutParams(p);
        } else if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
            p.bottomMargin = fit(dp);
            mLayoutTime.setLayoutParams(p);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed);
            mP.getDataFromNet();
        }
        return true;
    }

    private class StartContractViewImpl implements StartContract.V {

        @Override
        public void setReb(boolean reb) {
            mTvFinish.setText(reb ? R.string.reb_finish : R.string.live_finish);

            int liveState = mMeet.getInt(Meet.TMeet.liveState);
            if (reb || liveState == Live.LiveState.end) {
                // 录播或直播结束
                showView(mTvFinish);
                goneView(mTvLive);
                changeTimeLocation(100);
            } else {
                goneView(mTvFinish);
                showView(mTvLive);
                changeTimeLocation(136);
                setOnClickListener(mTvLive);
            }
        }

        @Override
        public void onNetworkSuccess(Code c) {
            long serverTime = c.getLong(Code.TCode.serverTime);
            if (serverTime > mMeet.getLong(Meet.TMeet.startTime)) {
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mTvAll.setText(formatTime(serverTime - mMeet.getLong(Meet.TMeet.startTime)));
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
            boolean startStatus = c.getBoolean(Code.TCode.starStatus);
            if (startStatus) {
                mIvDataMatrix.url(c.getString(Code.TCode.startCodeUrl))
                        .listener(new NetworkImageListener() {

                            @Override
                            public void onImageSet(ImageInfo info) {
                                startState(true);
                            }

                            @Override
                            public void onFailure() {
                                startState(false);
                            }
                        })
                        .load();
            } else {
                startState(false);
            }
        }

        @Override
        public void onStopRefresh() {

        }

        @Override
        public void setViewState(int state) {
            StartActivity.this.setViewState(state);
        }

        /**
         * 星评关闭
         *
         * @param flag true 展示二维码, false 展示会讲制作
         */
        private void startState(boolean flag) {
            stopRefresh();
            setViewState(DecorViewEx.ViewState.normal);
            if (flag) {
                goneView(mLayoutDefault);
                showView(mLayoutDataMatrix);
            } else {
                showView(mLayoutDefault);
                goneView(mLayoutDataMatrix);
            }
        }

        /**
         * 格式化时间
         *
         * @param time 时间
         * @return xx'xx"
         */
        private String formatTime(long time) {
            long min = time / TimeUnit.MINUTES.toMillis(1);
            long sec = time % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
            return new StringBuffer()
                    .append(min > 9 ? ConstantsEx.KEmpty : "0")
                    .append(min)
                    .append("'")
                    .append(sec > 9 ? ConstantsEx.KEmpty : "0")
                    .append(sec)
                    .append("\"")
                    .toString();
        }
    }
}
