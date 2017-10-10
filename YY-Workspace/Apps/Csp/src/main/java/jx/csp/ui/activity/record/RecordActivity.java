package jx.csp.ui.activity.record;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.dialog.HintDialog;
import jx.csp.sp.SpUser;
import jx.csp.util.Util;
import jx.csp.view.VoiceLineView;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseVPActivity;

/**
 * 录音页面
 *
 * @author CaiXiang
 * @since 2017/9/30
 */

public class RecordActivity extends BaseVPActivity implements OnPageChangeListener {

    private final int KOne = 1;
    private final int KVpSize = 2; // Vp缓存的数量
    private final int KDuration = 300; // 动画时长
    private final float KScale = 0.11f;

    private TextView mTvTimeRemain;
    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvOnlineNum;
    private TextView mTvRecordState;
    private TextView mTvStartRemain;

    private ImageView mIvVoiceState;
    private ImageView mIvRecordState;
    private VoiceLineView mVoiceLine;

    private long mStartTime;
    private float mLastOffset;
    private int mCurrentPage;

    private int mRecordType;

    @Override
    public void initData() {
        for (int i = 0; i < 20; ++i) {
            add(new RecordFrag());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addTextViewMid("9月30日 14：00");
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
    }

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
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition;
        float realOffset;
        int nextPosition;

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
        YSLog.d(TAG, "onPageSelected position = " + position);
        mCurrentPage = position;
        mTvCurrentPage.setText("" + (mCurrentPage + KOne));
        if (SpUser.inst().isShowSkipToNextPageDialog()) {
            showSkipToNextPageDialog();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 改变view的大小  缩放
     */
    private void viewChange(int position, float offset) {
        View view = getItem(position).getView();
        if (view == null) {
            return;
        }
        float scale = KOne + KScale * offset;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private void showSkipToNextPageDialog() {
        HintDialog dialog = new HintDialog(this);
        View view = inflate(R.layout.dialog_skip_to_next_page);
        dialog.addHintView(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_skip_to_next_page_check_box);
        dialog.addBlackButton(getString(R.string.confirm), v -> {
            if (checkBox.isChecked()) {
                SpUser.inst().saveShowSkipToNextPageDialog();
            }
        });
        dialog.addBlueButton(R.string.cancel);
        dialog.show();
    }
}
