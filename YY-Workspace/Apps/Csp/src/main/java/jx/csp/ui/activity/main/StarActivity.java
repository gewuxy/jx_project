package jx.csp.ui.activity.main;

import android.content.Intent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import inject.annotation.network.Descriptor;
import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.BuildConfig;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.contact.StarContract;
import jx.csp.dialog.CommonDialog;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.BgMusicThemeInfo;
import jx.csp.model.meeting.BgMusicThemeInfo.TBgMusicThemeInfo;
import jx.csp.model.meeting.Code;
import jx.csp.model.meeting.Course;
import jx.csp.model.meeting.Live;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.network.NetworkApi;
import jx.csp.presenter.StarPresenterImpl;
import jx.csp.serv.CommonServ;
import jx.csp.serv.CommonServRouter;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.ChoiceThemeActivityRouter;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
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

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    @Arg
    Meet mMeet;

    @Arg(opt = true)
    BgMusicThemeInfo mBgMusicThemeInfo;

    private TextView mTvAll;
    private View mLayoutDefault;
    private View mLayoutDataMatrix;
    private NetworkImageView mIvDataMatrix;
    private View mTvLive;
    private View mBarLeft;
    private View mLayoutRecord;  // 录播才显示的布局
    private View mLayoutBgMusic;  // 已经选择了的背景音乐的音乐名称/时长
    private ImageView mIvHaveMusic;  // 已经有背景音乐显示的图片
    private TextView mTvBgMusic;

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
        bar.addTextViewMid(start ? R.string.start_comment : R.string.finish);
        bar.addViewRight(R.drawable.green_hands_guide_ic_share, v -> {
            ShareDialog d = new ShareDialog(StarActivity.this, mMeet);
            d.show();
        });

        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        mLayoutDefault = findView(R.id.star_layout_meet_default);
        mLayoutDataMatrix = findView(R.id.star_layout_data_matrix);
        mIvDataMatrix = findView(R.id.star_iv_data_matrix);
        mTvAll = findView(R.id.star_tv_all_time);
        mTvLive = findView(R.id.star_tv_live_end);
        mLayoutRecord = findView(R.id.star_layout_preview_theme_music);
        mLayoutBgMusic = findView(R.id.star_layout_bg_music);
        mIvHaveMusic = findView(R.id.star_iv_add_music);
        mTvBgMusic = findView(R.id.star_tv_choose_bg_music);
    }

    @Override
    public void setViews() {
        mP.setPlayState();

        getDecorView().setBackgroundResource(R.color.app_nav_bar_bg);
        refresh(RefreshWay.embed);
        mP.getDataFromNet();

        // 判断是否已经有背景音乐
        if (mBgMusicThemeInfo != null && TextUtil.isNotEmpty(mBgMusicThemeInfo.getString(TBgMusicThemeInfo.name))) {
            long time = mBgMusicThemeInfo.getLong(TBgMusicThemeInfo.duration);
            showView(mLayoutBgMusic);
            showView(mIvHaveMusic);
            setOnClickListener(R.id.star_iv_close_bg_music_layout);
            mTvBgMusic.setText(mBgMusicThemeInfo.getString(TBgMusicThemeInfo.name) + " " + Util.getSpecialTimeFormat(time, "'", "''"));
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
                            .courseId(mMeet.getString(Meet.TMeet.id))
                            .route(StarActivity.this);
                    liveEndNoStar();
                    starState(false);
                });
                d.show();
            }
            break;
            case R.id.star_tv_preview: {
                CommonWebViewActivityRouter.create(getPreviewUrl())
                        .name(getString(R.string.preview))
                        .route(this);
            }
            break;
            case R.id.star_tv_add_theme: {
                ChoiceThemeActivityRouter.create(mMeet.getString(TMeet.id), mMeet.getString(TMeet.coverUrl)).route(this);
            }
            break;
            case R.id.star_tv_add_music: {
                if (mBgMusicThemeInfo != null) {
                    if (TextUtil.isEmpty(mBgMusicThemeInfo.getString(TBgMusicThemeInfo.musicId))) {
                        SelectBgMusicActivityRouter.create()
                                .courseId(mMeet.getString(TMeet.id))
                                .route(this, 0);
                    } else {
                        SelectBgMusicActivityRouter.create()
                                .courseId(mMeet.getString(TMeet.id))
                                .musicId(mBgMusicThemeInfo.getInt(TBgMusicThemeInfo.musicId))
                                .route(this, 0);
                    }
                } else {
                    SelectBgMusicActivityRouter.create()
                            .courseId(mMeet.getString(TMeet.id))
                            .route(this, 0);
                }
            }
            break;
            case R.id.star_iv_close_bg_music_layout: {
                // 删除背景音乐
                mP.deleteBgMusic();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        // 返回的背景音乐信息
        if (data != null) {
            long time = Long.parseLong(data.getStringExtra(Extra.KLimit));
            showView(mLayoutBgMusic);
            showView(mIvHaveMusic);
            mTvBgMusic.setText(data.getStringExtra(Extra.KData) + " " + Util.getSpecialTimeFormat(time, "'", "''"));
            setOnClickListener(R.id.star_iv_close_bg_music_layout);
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
     * 录播无星评状态
     */
    private void rebNoStar() {
        goneView(mTvLive);
        showView(mLayoutRecord);
        setOnClickListener(R.id.star_tv_preview);
        setOnClickListener(R.id.star_tv_add_theme);
        setOnClickListener(R.id.star_tv_add_music);
    }

    private String getPreviewUrl() {
        String previewUrl;
        // 拼接加密字符串
        LangType type = SpApp.inst().getLangType(); // 系统语言
        YSLog.d(TAG, "app app_type = " + type);
        // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
        @AppType int appType;  // 国内版 国外版
        if (Util.checkAppCn()) {
            appType = AppType.inland;
        } else {
            appType = AppType.overseas;
        }

        StringBuilder paramBuffer = new StringBuilder();
        paramBuffer.append("id=")
                .append(mMeet.getString(TMeet.id))
                .append("&_local=")
                .append(type.define())
                .append("&abroad=")
                .append(appType);
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        String http = BuildConfig.DEBUG_NETWORK ? des.hostDebuggable() : des.host();
        try {
            previewUrl = http + "meeting/share?signature=" + URLEncoder.encode(Util.encode(KDesKey, paramBuffer.toString()), Constants.KEncoding_utf8);
            YSLog.d(TAG, "PreviewUrl = " + previewUrl);
        } catch (UnsupportedEncodingException e) {
            YSLog.e(TAG, "shareSignature", e);
            previewUrl = http + "meeting/share?signature=";
        }
        return previewUrl;
    }

    /**
     * 直播无星评状态
     */
    private void liveEndNoStar() {
        goneView(mTvLive);
        goneView(mLayoutRecord);
    }

    private class StarContractViewImpl implements StarContract.V {

        @Override
        public void setReb(boolean reb) {
            int liveState = mMeet.getInt(Meet.TMeet.liveState);
            if (reb) {
                // 录播
                rebNoStar();
                // 判断有没有背景音乐
                return;
            }
            if (liveState == LiveState.end) {
                // 直播结束
                liveEndNoStar();
            } else {
                showView(mTvLive);
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
                rebNoStar();
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
                    mIvDataMatrix.url(c.getString(Code.TCode.startCodeUrl)).load();
                    starState(true);
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
                starState(false);
            }
        }

        @Override
        public void deleteBgMusicSuccess() {
            goneView(mLayoutBgMusic);
            goneView(mIvHaveMusic);
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
