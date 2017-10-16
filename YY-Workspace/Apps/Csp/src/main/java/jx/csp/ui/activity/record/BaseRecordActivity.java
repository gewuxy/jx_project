package jx.csp.ui.activity.record;

import android.app.Service;
import android.support.annotation.CallSuper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import jx.csp.view.GestureView;
import jx.csp.view.VoiceLineView;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.yy.ui.activity.base.BaseVPActivity;

/**
 * 录音页面
 *
 * @author CaiXiang
 * @since 2017/9/30
 */

abstract public class BaseRecordActivity extends BaseVPActivity implements OnPageChangeListener {

    protected final int KMicroPermissionCode = 10;
    protected final String KAudioSuffix = ".amr";
    private final int KOne = 1;
    private final int KVpSize = 2; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KVpScale = 0.11f; // vp的缩放比例

    protected TextView mTvNavBar;
    protected TextView mTvTimeRemain;
    protected TextView mTvCurrentPage;
    protected TextView mTvTotalPage;
    protected TextView mTvOnlineNum;
    protected TextView mTvRecordState;
    protected TextView mTvStartRemain;

    protected ImageView mIvVoiceState;
    protected ImageView mIvRecordState;
    protected VoiceLineView mVoiceLine;
    protected View mLayoutOnline;
    protected GestureView mGestureView;

    private float mLastOffset;
    protected int mCurrentPage;

    protected String mMeetingId = "123456";  // 会议id
    protected PhoneStateListener mPhoneStateListener = null;  // 电话状态监听

    @Override
    public void initData() {
        //创建文件夹存放音频
        FileUtil.ensureFileExist(CacheUtil.getAudioCacheDir() + "/" + mMeetingId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        mTvNavBar = bar.addTextViewMid(" ");
        bar.addViewRight(R.drawable.share_ic_share, new OnClickListener() {

            @Override
            public void onClick(View v) {
                showToast("share");
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_record;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTimeRemain = findView(R.id.record_tv_time_remain);
        mTvCurrentPage = findView(R.id.record_tv_current_page);
        mTvTotalPage = findView(R.id.record_tv_total_page);
        mTvOnlineNum = findView(R.id.record_tv_online_num);
        mTvRecordState = findView(R.id.record_tv_state);
        mTvStartRemain = findView(R.id.record_tv_start_remain);
        mIvVoiceState = findView(R.id.record_iv_voice_state);
        mIvRecordState = findView(R.id.record_iv_state);
        mVoiceLine = findView(R.id.record_voice_line);
        mLayoutOnline = findView(R.id.record_online_layout);
        mGestureView = findView(R.id.gesture_view);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mCurrentPage = 0;
        mTvTotalPage.setText("20");
        setOffscreenPageLimit(KVpSize);
        setScrollDuration(KDuration);
        getViewPager().setPageMargin(fitDp(27));
        setOnPageChangeListener(this);
        setOnClickListener(R.id.record_iv_last);
        setOnClickListener(R.id.record_iv_next);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_iv_last: {
                if (mCurrentPage < KOne) {
                    showToast(R.string.first_page);
                    return;
                }
                setCurrentItem(mCurrentPage - KOne);
            }
            break;
            case R.id.record_iv_next: {
                if (mCurrentPage >= 19) {
                    showToast(R.string.last_page);
                    return;
                }
                setCurrentItem(mCurrentPage + KOne);
            }
            break;
            default:
                onClick(v.getId());
                break;
        }
    }

    abstract protected void onClick(int id);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销电话监听
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mPhoneStateListener = null;
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition;
        float realOffset;
        int nextPosition;
        YSLog.d("www", "positionOffset  = " + positionOffset);
        if (mLastOffset > positionOffset) {
            realPosition = position + KOne;
            nextPosition = position;
            realOffset = KOne - positionOffset;
        } else {
            realPosition = position;
            nextPosition = position + KOne;
            realOffset = positionOffset;
        }

        if (nextPosition > getCount() - KOne || realPosition > getCount() - KOne) {
            return;
        }

        viewChange(realPosition, KOne - realOffset);
        viewChange(nextPosition, realOffset);

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        mTvCurrentPage.setText(String.valueOf(mCurrentPage + KOne));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void initPhoneCallingListener() {
        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        YSLog.d(TAG, "call state idle " + state);
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        YSLog.d(TAG, "call state ringing " + state);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        YSLog.d(TAG, "call state off hook " + state);
                        onCallOffHooK();
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    abstract protected void onCallOffHooK();

    /**
     * 改变view的大小  缩放
     */
    private void viewChange(int position, float offset) {
        View view = getItem(position).getView();
        if (view == null) {
            return;
        }
        float scale = KOne + KVpScale * offset;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

}
