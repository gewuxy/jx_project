package jx.doctor.ui.activity.meeting.play;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import inject.annotation.router.Route;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.ui.activity.meeting.play.contract.RebContact;
import jx.doctor.ui.activity.meeting.play.presenter.RebPresenterImpl;
import jx.doctor.ui.frag.meeting.course.PicCourseFrag;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Util;
import lib.ys.ui.other.NavBar;

/**
 * 录播界面
 *
 * @auther : GuoXuan
 * @since : 2017/10/30
 */
@Route
public class RebActivity extends BasePptActivity<RebContact.View, RebContact.Presenter> {

    private final int KCodeReq = 10;

    // 音频时长
    private View mLayoutMedia;
    private SeekBar mSbMedia;
    private TextView mTvMedia;
    private ImageView mOverView;

    private boolean mScroll;

    private boolean mCompletion;

    @Override
    public void initData() {
        super.initData();

        mScroll = false;
        mCompletion = false;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ppt_reb;
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);

        ViewGroup viewGroup = bar.addViewRight(R.drawable.nav_bar_ic_preview, l ->
                mP.toOverview(this, mUnitNum, KCodeReq));
        mOverView = Util.getBarView(viewGroup, ImageView.class);
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutMedia = findView(R.id.ppt_reb_layout_media_time);
        mSbMedia = findView(R.id.ppt_reb_sb_media);
        mTvMedia = findView(R.id.ppt_reb_tv_media);
    }

    @Override
    public void setViews() {
        super.setViews();

        mSbMedia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mScroll) {
                    NetPlayer.inst().setProgressPlay(progress);
                    mP.changeTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mScroll = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mScroll = false;
            }

        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.play_nav_iv_control: {
                if (mFragPpt.getCurrPosition() == mFragPpt.getCount() - 1 && mCompletion) {
                    mP.setProgress(0);
                    mCompletion = false;
                }
            }
            break;
        }
    }

    @NonNull
    @Override
    protected RebContact.View createV() {
        return new RebViewImpl();
    }

    @NonNull
    @Override
    protected RebContact.Presenter createP(RebContact.View view) {
        return new RebPresenterImpl(view);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mV.onPlayState(false);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        mP.playMedia(position);
        mediaTime(position);
        mSbMedia.setProgress(0);
        if (!mIvControl.isSelected()) {
            mIvControl.setSelected(true);
        }
        if (position == mFragPpt.getCount() - 1 && mFragPpt.getItem(position) instanceof PicCourseFrag) {
            mV.onPlayState(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KCodeReq && resultCode == RESULT_OK && data != null) {
            int index = data.getIntExtra(Extra.KData, 0);
            mFragPpt.setCurrPosition(index, false);
            if (!(mFragPpt.getItem(index) instanceof PicCourseFrag)) {
                mV.onPlayState(true);
            }
        }
    }

    private void mediaTime(int position) {
        if (mFragPpt.getItem(position) instanceof PicCourseFrag) {
            goneView(mLayoutMedia);
        } else {
            showView(mLayoutMedia);
        }
    }

    private class RebViewImpl extends BasePptViewImpl implements RebContact.View {

        @Override
        public void onNetworkSuccess(PPT ppt) {
            super.onNetworkSuccess(ppt);

            mP.playMedia(0);
            mediaTime(0);
        }

        @Override
        public void portrait() {
            super.portrait();

            showView(mOverView);
        }

        @Override
        public void landscape() {
            super.landscape();

            goneView(mOverView);
        }

        @Override
        public void playProgress(String time, int progress) {
            mSbMedia.setProgress(progress);
            mTvMedia.setText(time);
            int currPosition = mFragPpt.getCurrPosition();
            if (progress == NetPlayer.KMaxProgress && currPosition == mFragPpt.getCount() - 1
                    && mFragPpt.getItem(currPosition) instanceof PicCourseFrag) {
                mV.onPlayState(false);
            }
        }

        @Override
        public void setTime(String time) {
            mTvMedia.setText(time);
        }

        @Override
        public void recordProgress() {
            mP.setProgress(mSbMedia.getProgress());
        }

        @Override
        public void completion() {
            int currPosition = mFragPpt.getCurrPosition();
            if (currPosition == mFragPpt.getCount() - 1 && !(mFragPpt.getItem(currPosition) instanceof PicCourseFrag)) {
                onPlayState(false);
            }
            mP.setProgress(0);
            mCompletion = true;
        }
    }

}
