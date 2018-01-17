package jx.csp.ui.frag.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.presenter.MeetPresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.frag.base.BaseFrag;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * 首页左右滑动列表的单个frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
@Route
public class MeetSingleFrag extends BaseFrag implements MeetContract.V {

    private NetworkImageView mIvCover;
    private ImageView mIvLive;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvSharePlayback;

    @Arg
    Meet mMeet;

    private MeetContract.P mPresenter;

    @Override
    public void initData() {
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @Override
    public int getContentViewId() {
        return R.layout.frag_main_meet_single;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        mIvCover = findView(R.id.iv_main_cover);
        mTvTitle = findView(R.id.tv_title);
        mIvLive = findView(R.id.main_meet_single_iv_live);
        mTvTime = findView(R.id.tv_total_time);
        mTvSharePlayback = findView(R.id.frag_main_meet_single_tv_share_playback);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.main_meet_single_iv_share);
        setOnClickListener(R.id.main_meet_single_layout);
        setOnClickListener(R.id.main_meet_single_iv_live);

        findView(R.id.main_meet_single_layout).setOnLongClickListener(v -> {
            String courseId = mMeet.getString(TMeet.id);
            Util.deleteMeet(courseId, getContext());
            return true;
        });

        mIvCover.placeHolder(R.drawable.ic_default_main_vp)
                .url(mMeet.getString(TMeet.coverUrl))
                .resize(fit(300), fit(310))
                .load();

        mTvTitle.setText(mMeet.getString(TMeet.title));

        switch (mMeet.getInt(TMeet.playType)) {
            case PlayType.reb: {
                hideView(mIvLive);
                mTvTime.setText(mMeet.getString(TMeet.playTime));
            }
            break;
            case PlayType.live:
            case PlayType.video: {
                long startTime = mMeet.getLong(TMeet.startTime);
                long endTime = mMeet.getLong(TMeet.endTime);
                long serverTime = mMeet.getLong(TMeet.serverTime);
                if (mMeet.getInt(TMeet.playType) == PlayType.video) {
                    showView(mIvLive);
                } else {
                    hideView(mIvLive);
                }
                int liveState = mMeet.getInt(TMeet.liveState);
                if (liveState == LiveState.un_start || startTime > serverTime) {
                    //直播的开始时间转换
                    mTvTime.setText(TimeFormatter.milli(mMeet.getLong(TMeet.startTime), TimeFormat.form_MM_dd_24));
                } else if ((liveState == LiveState.live || liveState == LiveState.stop) && (startTime < serverTime && endTime > serverTime)) {
                    mTvTime.setText(mMeet.getString(TMeet.playTime));
                } else {
                    hideView(mIvLive);
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_meet_single_iv_share: {
                mPresenter.onShareClick(mMeet);
            }
            break;
            case R.id.main_meet_single_layout: {
                mPresenter.onMeetClick(mMeet);
            }
            break;
            case R.id.main_meet_single_iv_live: {
                mPresenter.onLiveClick(mMeet);
            }
            break;
        }
    }

    @Override
    public void onStopRefresh() {

    }

    public void allowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "vp enter p = null");
            return;
        }
        mPresenter.allowEnter();
    }

    public void notAllowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "vp enter p = null");
            return;
        }
        mPresenter.notAllowEnter();
    }

    public void onMeetClick() {
        mPresenter.onMeetClick(mMeet);
    }

    public void showSharePlayback(String id) {
        if (mMeet.getString(TMeet.id).equals(id)) {
            showView(mTvSharePlayback);
        }
    }

    public void goneSharePlayback(String id) {
        if (mMeet.getString(TMeet.id).equals(id)) {
            goneView(mTvSharePlayback);
        }
    }
}
