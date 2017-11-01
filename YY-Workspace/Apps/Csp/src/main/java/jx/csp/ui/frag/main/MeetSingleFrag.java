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
import jx.csp.contact.MeetContract;
import jx.csp.model.def.MeetState;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.presenter.MeetPresenterImpl;
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
    private View mVDivider;

    @Arg
    Meet mMeet;

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
        mVDivider = findView(R.id.slide_divider);
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

        if (mMeet.getInt(TMeet.playType) == PlayType.reb) {
            mTvTime.setText(mMeet.getString(TMeet.playTime));
            mTvCurrentPage.setText(mMeet.getString(TMeet.playPage));

            if (mMeet.getInt(TMeet.playState) == PlayState.un_start) {
                mTvState.setText(R.string.record);
            } else if (mMeet.getInt(TMeet.playState) == PlayState.record) {
                mTvState.setText(R.string.on_record);
            } else {
                goneView(mTvState);
            }
            goneView(mIvLive);
        } else {
            mTvCurrentPage.setText(mMeet.getString(TMeet.livePage));

            if (mMeet.getInt(TMeet.liveState) == LiveState.un_start) {
                mTvState.setText(R.string.solive);

                //直播的开始时间转换
                Date d = new Date(Long.parseLong(mMeet.getString(TMeet.startTime)));
                SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
                mTvTime.setText(data.format(d));
            } else if (mMeet.getInt(TMeet.liveState) == LiveState.live) {
                mTvState.setText(R.string.on_solive);
                mTvTime.setText(mMeet.getString(TMeet.playTime));
            } else {
                mTvState.setText(R.string.on_solive);
                mTvTime.setText(mMeet.getString(TMeet.playTime));
            }
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
}