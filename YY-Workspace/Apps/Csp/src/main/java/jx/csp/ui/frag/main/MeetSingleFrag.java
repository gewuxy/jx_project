package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.MeetState;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.presenter.MeetPresenterImpl;
import lib.ys.ConstantsEx;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;

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
    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvState;

    @Arg
    Meet mMeet;

    public Meet getMeet() {
        return mMeet;
    }

    private MeetContract.P mPresenter;

    @Override
    public void initData() {
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @NonNull
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
        mTvCurrentPage = findView(R.id.tv_current_page);
        mTvTotalPage = findView(R.id.tv_total_page);
        mTvState = findView(R.id.tv_state);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.main_meet_single_iv_share);
        setOnClickListener(R.id.main_meet_single_layout);
        setOnClickListener(R.id.main_meet_single_iv_live);

        mIvCover.placeHolder(R.drawable.ic_default_record)
                .url(mMeet.getString(TMeet.coverUrl))
                .load();

        mTvTitle.setText(mMeet.getString(TMeet.title));
        mTvTotalPage.setText(mMeet.getString(TMeet.pageCount));

        long startTime = mMeet.getLong(TMeet.startTime);
        long stopTime = mMeet.getLong(TMeet.endTime);
        long currentTime = System.currentTimeMillis();
        switch (mMeet.getInt(TMeet.playType)) {
            case PlayType.reb: {
                mTvTime.setText(mMeet.getString(TMeet.playTime));
                mTvCurrentPage.setText(mMeet.getString(TMeet.playPage));
                mTvState.setText(R.string.on_record);
                goneView(mIvLive);
            }
            break;
            case PlayType.live: {
                goneView(mIvLive);
            }
            case PlayType.video: {
                mTvCurrentPage.setText(mMeet.getString(TMeet.livePage));
                if (startTime > currentTime) {
                    mTvState.setText(R.string.solive);
                    //直播的开始时间转换
                    Date d = new Date(Long.parseLong(mMeet.getString(TMeet.startTime)));
                    SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
                    mTvTime.setText(data.format(d));
                } else if (startTime < currentTime && stopTime > currentTime) {
                    mTvState.setText(R.string.on_solive);
                    mTvTime.setText(mMeet.getString(TMeet.playTime));
                } else {
                    mTvState.setText(ConstantsEx.KEmpty);
                    mTvTime.setText(mMeet.getString(TMeet.playTime));
                }
            }
            break;
        }
    }

    public int getType() {
        if (mMeet.getInt(TMeet.liveState) == LiveState.live) {
            return MeetState.living;
        } else if (mMeet.getInt(TMeet.playState) == PlayState.record) {
            return MeetState.playing;
        } else {
            return MeetState.other;
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

    public void enter() {
        if (mPresenter == null) {
            return;
        }
        mPresenter.allowJoin();
    }

    public void noEnter() {
        if (mPresenter == null) {
            return;
        }
        mPresenter.disagreeJoin();
    }

    public void onMeetClick() {
        mPresenter.onMeetClick(mMeet);
    }
}
