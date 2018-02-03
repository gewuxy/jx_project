package jx.csp.ui.activity.main;

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
import jx.csp.contact.StarContract;
import jx.csp.dialog.CommonDialog;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.Live;
import jx.csp.presenter.StarPresenterImpl;
import jx.csp.serv.CommonServ;
import jx.csp.serv.CommonServRouter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.config.AppConfig;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * 星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
@Route
public class StarActivity extends BaseActivity {

    @Arg
    Meet mMeet;

    private TextView mTvFinish;
    private TextView mTvAll;
    private View mLayoutDefault;
    private View mLayoutDataMatrix;
    private NetworkImageView mIvDataMatrix;
    private View mTvLive;
    private View mLayoutTime;
    private View mBarLeft;

    private StarContract.P mP;

    @Override
    public void initData() {
        StarContract.V v = new StarContractViewImpl();
        mP = new StarPresenterImpl(v, mMeet);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_star;
    }

    @Override
    public void initNavBar(NavBar bar) {
        mBarLeft = bar.addViewLeft(R.drawable.default_ic_close, null, l -> {
            if (mTvLive.getVisibility() == View.VISIBLE) {
                mTvLive.performClick();
            } else {
                finish();
            }
        });
        boolean start = mMeet.getBoolean(Meet.TMeet.starRateFlag);
        bar.addTextViewMid(start ? R.string.start_comment : R.string.start_finish);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            ShareDialog d = new ShareDialog(StarActivity.this, mMeet, true);
            d.show();
        });

        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        mLayoutDefault = findView(R.id.star_layout_meet_default);
        mLayoutDataMatrix = findView(R.id.star_layout_data_matrix);
        mLayoutTime = findView(R.id.star_layout_all_time);
        mIvDataMatrix = findView(R.id.star_iv_data_matrix);
        mTvFinish = findView(R.id.star_tv_finish);
        mTvAll = findView(R.id.star_tv_all_time);
        mTvLive = findView(R.id.star_tv_live_end);
    }

    @Override
    public void setViews() {
        mP.setPlayState();

        getDecorView().setBackgroundResource(R.color.app_nav_bar_bg);
        refresh(AppConfig.RefreshWay.embed);
        mP.getDataFromNet();
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
                            .courseId(mMeet.getString(Meet.TMeet.id))
                            .route(StarActivity.this);
                    noStar();
                    starState(false);
                });
                d.show();
            }
            break;

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

    @Override
    public void onBackPressed() {
        mBarLeft.performClick();
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

    /**
     * 星评关闭
     *
     * @param flag true 展示二维码, false 展示会讲制作
     */
    private void starState(boolean flag) {
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
     * 无星评状态
     */
    private void noStar() {
        showView(mTvFinish);
        goneView(mTvLive);
        changeTimeLocation(100);
    }

    private class StarContractViewImpl implements StarContract.V {

        @Override
        public void setReb(boolean reb) {
            mTvFinish.setText(reb ? R.string.reb_finish : R.string.live_finish);

            int liveState = mMeet.getInt(Meet.TMeet.liveState);
            if (reb || liveState == Live.LiveState.end) {
                // 录播或直播结束
                noStar();
            } else {
                goneView(mTvFinish);
                showView(mTvLive);
                changeTimeLocation(136);
                setOnClickListener(mTvLive);
            }
        }

        @Override
        public void onNetworkSuccess(Code c) {
            // 讲本时长
            String time = null;
            if (mMeet.getInt(Meet.TMeet.playType) == Course.CourseType.reb) {
                String pTime = mMeet.getString(Meet.TMeet.playTime);
                // 录播
                if (TextUtil.isNotEmpty(pTime)) {
                    time = pTime;
                }
            } else {
                // 直播
                long serverTime = c.getLong(Code.TCode.serverTime);
                if (serverTime > mMeet.getLong(Meet.TMeet.startTime)) {
                    long t = serverTime - mMeet.getLong(Meet.TMeet.startTime);
                    time = Util.getSpecialTimeFormat(t / TimeUnit.SECONDS.toMillis(1), "'", "\"");
                }
            }
            if (TextUtil.isEmpty(time)) {
                time = Util.getSpecialTimeFormat(0, "'", "\"");
            }
            String timeFinal = time;
            addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mTvAll.setText(timeFinal);
                    removeOnGlobalLayoutListener(this);
                }

            });

            // 星评二维码
            boolean startStatus = c.getBoolean(Code.TCode.starStatus);
            if (startStatus) {
                // 有星评
                if (mMeet.getInt(Meet.TMeet.liveState) != Live.LiveState.end) {
                    mIvDataMatrix.url(c.getString(Code.TCode.startCodeUrl))
                            .listener(new NetworkImageListener() {

                                @Override
                                public void onImageSet(ImageInfo info) {
                                    starState(true);
                                }

                                @Override
                                public void onFailure() {
                                    starState(false);
                                }
                            })
                            .load();
                } else {
                    starState(false);
                }
            } else {
                // 没有星评
                if (mMeet.getInt(Meet.TMeet.liveState) != Live.LiveState.end) {
                    CommonServRouter.create(CommonServ.ReqType.over_live)
                            .courseId(mMeet.getString(Meet.TMeet.id))
                            .route(StarActivity.this);
                }
                noStar();
                starState(false);
            }
        }

        @Override
        public void onStopRefresh() {

        }

        @Override
        public void setViewState(int state) {
            StarActivity.this.setViewState(state);
        }

    }
}
