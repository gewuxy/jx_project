package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.MeetingSingleDialog;
import yy.doctor.model.exam.Intro;
import yy.doctor.model.exam.Intro.TIntro;

/**
 * @author : GuoXuan
 * @since : 2017/4/27
 */

public class ExamTopicActivity extends BaseTopicActivity {

    private static final int KTextSizeDp = 16;
    private static final int KFiveMin = 300;

    private final int KXClose = 2;//X秒后自动关闭

    private TextView mTvTime;
    private int mUseTime;//做题时间
    private DisposableSubscriber<Long> mSub;
    private MeetingSingleDialog mCloseDialog;//离考试结束的提示框
    private MeetingSingleDialog mSubmitDialog;

    public static void nav(Context context, String meetId, String moduleId, Intro intro) {
        Intent i = new Intent(context, ExamTopicActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId)
                .putExtra(Extra.KData, intro);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        super.initData();
        mUseTime = mIntro.getInt(TIntro.usetime) * 60;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);
        mTvLeft.setText("考试");
        //默认显示,外加倒计时
        mTvTime = new TextView(ExamTopicActivity.this);
        mTvTime.setText(timeParse(mUseTime));
        mTvTime.setGravity(Gravity.CENTER);
        mTvTime.setTextColor(Color.WHITE);
        mTvTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, KTextSizeDp);
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .take(mUseTime + 1)
                .map(aLong -> mUseTime - aLong) // 转换成倒数的时间
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
        bar.addViewMid(mTvTime);
    }

    @Override
    protected void topicCaseVisibility(boolean showState) {
        super.topicCaseVisibility(showState);
        if (getTopicCaseShow()) {
            mTvLeft.setText("题目");
            goneView(mTvTime);
        } else {
            mTvLeft.setText("考试");
            showView(mTvTime);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
        if (mCloseDialog != null) {
            if (mCloseDialog.isShowing()) {
                mCloseDialog.dismiss();
            }
            mCloseDialog = null;
        }
        if (mSubmitDialog != null) {
            if (mSubmitDialog.isShowing()) {
                mSubmitDialog.dismiss();
            }
            mSubmitDialog = null;
        }
    }

    /**
     * 把时间格式化为xx:xx:xx
     *
     * @param useTime
     * @return
     */
    private String timeParse(int useTime) {
        StringBuffer sb = new StringBuffer();
        int hour = useTime / 3600;
        if (hour < 10) {
            sb.append(0);
        }
        sb.append(hour).append(":");
        int min = useTime / 60 % 60;
        if (min < 10) {
            sb.append(0);
        }
        sb.append(min).append(":");
        int sec = useTime % 60;
        if (sec < 10) {
            sb.append(0);
        }
        sb.append(sec);
        return sb.toString();
    }

    private DisposableSubscriber<Long> createSub() {
        mSub = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(@NonNull Long aLong) {
                mTvTime.setText(timeParse(aLong.intValue()));
                // 剩余5分钟
                if (aLong == KFiveMin) {
                    mCloseDialog = new MeetingSingleDialog(ExamTopicActivity.this) {
                        @Override
                        public void close(Long aLong) {
                            setTvSecondaryHint(aLong + getString(R.string.exam_xs_close));
                        }
                    }
                            .setTvMainHint(getString(R.string.exam_five_min))
                            .setTvSecondaryHint(KXClose + getString(R.string.exam_xs_close));
                    mCloseDialog.start(KXClose);
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }

            @Override
            public void onComplete() {
                mSubmitDialog = new MeetingSingleDialog(ExamTopicActivity.this)
                        .setTvMainHint(getString(R.string.exam_end))
                        .setTvSecondaryHint(getString(R.string.exam_submit));
                mSubmitDialog.setOnClickListener(v -> {
                    mSubmitDialog.dismiss();
                    submit();
                });
                mSubmitDialog.show();
            }
        };
        return mSub;
    }

}
