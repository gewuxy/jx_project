package jx.csp.ui.activity.me;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.me.GreenHandsGuide;
import jx.csp.model.me.GreenHandsGuide.TGreenHandsGuide;
import jx.csp.model.me.GreenHandsGuideDetails;
import jx.csp.model.me.GreenHandsGuideDetails.TGreenHandsGuideDetails;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.frag.GreenHandsVideoFrag;
import jx.csp.ui.frag.GreenHandsVideoFrag.videoPlayListener;
import jx.csp.ui.frag.GreenHandsVideoFragRouter;
import jx.csp.ui.frag.record.RecordImgFrag;
import jx.csp.ui.frag.record.RecordImgFragRouter;
import jx.csp.util.CacheUtil;
import jx.csp.util.ScaleTransformer;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;

/**
 * 新手指引页面
 *
 * @author CaiXiang
 * @since 2018/2/3
 */

@Route
public class GreenHandsGuideActivity extends BaseVpActivity implements videoPlayListener {

    private final int KBreathIntervalTime = 1500; // 设置呼吸灯时间间隔
    protected final int KOne = 1;
    private final int KVpSize = 3; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KVpScale = 0.038f; // vp的缩放比例

    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvTime;
    private ImageView mIvAlpha;
    private ImageView mIvState;

    private MediaPlayer mMediaPlayer;
    private AlphaAnimation mAnimationFadeIn;
    private AlphaAnimation mAnimationFadeOut;
    private ScaleTransformer mTransformer;
    protected List<CourseDetail> mCourseDetailList;
    private SparseArray<Integer> mRecordTimeArray;  // 每页ppt录制的音频时间
    private Meet mShareArg;
    private boolean mPlayState = false;  // 播放状态
    private int mPauseProgress = 0;  // 暂停时的位置

    @Arg
    String mCourseId;  // 课程id

    @Override
    public void initData() {
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + File.separator + mCourseId);
        mRecordTimeArray = new SparseArray<>();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_base_record_nav_bar);
        mTvCurrentPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_current_page);
        mTvTotalPage = view.findViewById(R.id.layout_base_record_nav_bar_tv_total_page);
        bar.addViewMid(view);
        bar.addViewRight(R.drawable.green_hands_guide_ic_share, v -> {
            if (mShareArg != null) {
                ShareDialog dialog = new ShareDialog(this, mShareArg, false);
                dialog.show();
            }
        });
        Util.addDivider(bar);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_green_hands_guide;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTime = findView(R.id.green_hands_guide_tv_time);
        mIvState = findView(R.id.green_hands_guide_iv_state);
        mIvAlpha = findView(R.id.green_hands_guide_iv_state_alpha);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(mIvState);
        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mTransformer = new ScaleTransformer(KVpScale, Util.calcVpOffset(getViewPager().getPaddingLeft(), getViewPager().getWidth()));
                setPageTransformer(false, mTransformer);
                removeOnGlobalLayoutListener(this);
            }
        });
        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText(String.valueOf(position + KOne));
                mTvTime.setText(Util.getSpecialTimeFormat(mRecordTimeArray.get(position), ":", ""));
                mPauseProgress = 0;
                if (mPlayState) {
                    // 判断是视频还是图片
                    Fragment f = getItem(position);
                    if (f instanceof RecordImgFrag) {
                        startPlayAudio(mPauseProgress);
                    } else {
                        GreenHandsVideoFrag frag = (GreenHandsVideoFrag) getItem(position);
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                        frag.startPlay();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mAnimationFadeIn = new AlphaAnimation(0.2f, 1.0f);
        mAnimationFadeIn.setDuration(KBreathIntervalTime);
        mAnimationFadeIn.setFillAfter(false);  //动画结束后不保持状态
        mAnimationFadeOut = new AlphaAnimation(1.0f, 0.2f);
        mAnimationFadeOut.setDuration(KBreathIntervalTime);
        mAnimationFadeOut.setFillAfter(false);
        mAnimationFadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (mPlayState) {
                    mIvAlpha.startAnimation(mAnimationFadeOut);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });
        mAnimationFadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (mPlayState) {
                    mIvAlpha.startAnimation(mAnimationFadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        refresh(RefreshWay.dialog);
        exeNetworkReq(MeetingAPI.greenHandsGuide(mCourseId).build());
    }

    @Override
    public void onClick(View v) {
        //判断当前页是图片还是视频
        Fragment f = getItem(getCurrPosition());
        if (f instanceof RecordImgFrag) {
            if (mPlayState) {
                mIvState.setSelected(false);
                pausePlayAudio();
                stopAnim();
            } else {
                mIvState.setSelected(true);
                startPlayAudio(mPauseProgress);
                startAnim();
            }
        } else {
            GreenHandsVideoFrag frag = (GreenHandsVideoFrag) getItem(getCurrPosition());
            if (mPlayState) {
                mIvState.setSelected(false);
                mPlayState = false;
                frag.stopPlay();
                stopAnim();
            } else {
                mIvState.setSelected(true);
                mPlayState = true;
                frag.startPlay();
                startAnim();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mPlayState) {
            Fragment f = getItem(getCurrPosition());
            if (f instanceof RecordImgFrag) {
                pausePlayAudio();
            } else {
                GreenHandsVideoFrag frag = (GreenHandsVideoFrag) getItem(getCurrPosition());
                frag.stopPlay();
            }
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), GreenHandsGuide.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            GreenHandsGuide res = (GreenHandsGuide) r.getData();
            GreenHandsGuideDetails audioCourse = res.get(TGreenHandsGuide.audioCourse);

            mShareArg = new Meet();
            mShareArg.put(TMeet.id, audioCourse.getString(TGreenHandsGuideDetails.id));
            mShareArg.put(TMeet.title, audioCourse.getString(TGreenHandsGuideDetails.title));

            mCourseDetailList = audioCourse.getList(TGreenHandsGuideDetails.details);

            if (mCourseDetailList != null && mCourseDetailList.size() > 0) {
                mShareArg.put(TMeet.coverUrl, mCourseDetailList.get(0).getString(TCourseDetail.imgUrl));
                mTvTotalPage.setText(String.valueOf(mCourseDetailList.size()));
                for (int i = 0; i < mCourseDetailList.size(); ++i) {
                    CourseDetail courseDetail = mCourseDetailList.get(i);

                    // 判断是视频还是图片
                    if (TextUtil.isEmpty(courseDetail.getString(TCourseDetail.videoUrl))) {
                        RecordImgFrag frag = RecordImgFragRouter
                                .create(courseDetail.getString(TCourseDetail.imgUrl))
                                .audioFilePath(CacheUtil.getExistAudioFilePath(mCourseId, courseDetail.getInt(TCourseDetail.id)))
                                .audioUrl(courseDetail.getString(TCourseDetail.audioUrl))
                                .route();
                        add(frag);
                    } else {
                        GreenHandsVideoFrag frag = GreenHandsVideoFragRouter.create(
                                courseDetail.getString(TCourseDetail.videoUrl),
                                courseDetail.getString(TCourseDetail.imgUrl)).route();
                        frag.setVideoPlayListener(this);
                        add(frag);
                    }

                    if (TextUtil.isNotEmpty(courseDetail.getString(TCourseDetail.duration))) {
                        int duration = courseDetail.getInt(TCourseDetail.duration);
                        mRecordTimeArray.put(i, duration);
                    } else {
                        mRecordTimeArray.put(i, 0);
                    }
                }
            }
            invalidate();
            // 一进来自动播放
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getItem(0) instanceof RecordImgFrag) {
                        mIvState.setSelected(true);
                        startPlayAudio(mPauseProgress);
                        startAnim();
                    } else {
                        GreenHandsVideoFrag frag = (GreenHandsVideoFrag) getItem(getCurrPosition());
                        mIvState.setSelected(true);
                        mPlayState = true;
                        frag.startPlay();
                        startAnim();
                    }
                    removeOnGlobalLayoutListener(this);
                }
            });
        } else {
            super.onNetworkSuccess(id, r);
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public void videoPlayCompletion() {
        if ((getCurrPosition() + KOne) == mCourseDetailList.size()) {
            mPlayState = false;
            mIvState.setSelected(false);
            stopAnim();
        } else {
            setCurrPosition(getCurrPosition() + KOne, true);
        }
    }

    private void startPlayAudio(int progress) {
        File soundFile = new File(CacheUtil.getExistAudioFilePath(mCourseId, mCourseDetailList.get(getCurrPosition()).getInt(TCourseDetail.id)));
        if (!soundFile.exists()) {
            showToast(R.string.play_fail);
            return;
        }
        if (progress == 0) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(soundFile.getAbsolutePath());
                //同步准备
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mPlayState = true;
                mPauseProgress = 0;
                mMediaPlayer.setOnCompletionListener(mp -> {
                    mPauseProgress = 0;
                    if ((getCurrPosition() + KOne) == mCourseDetailList.size()) {
                        mPlayState = false;
                        mIvState.setSelected(false);
                        stopAnim();
                    } else {
                        setCurrPosition(getCurrPosition() + KOne, true);
                    }
                });
            } catch (IOException e) {
                showToast(R.string.play_fail);
            }
        } else {
            mMediaPlayer.seekTo(progress);
            mMediaPlayer.start();
            mPlayState = true;
        }
    }

    private void pausePlayAudio() {
        mPlayState = false;
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mPauseProgress = mMediaPlayer.getCurrentPosition();
                mMediaPlayer.pause();
            }
        }
    }

    private void startAnim() {
        showView(mIvAlpha);
        mIvAlpha.startAnimation(mAnimationFadeIn);
    }

    private void stopAnim() {
        mIvAlpha.clearAnimation();
        goneView(mIvAlpha);
    }
}
