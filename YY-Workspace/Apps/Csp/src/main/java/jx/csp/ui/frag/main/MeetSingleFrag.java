package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.App;
import jx.csp.R;
import jx.csp.dialog.CommonDialog2;
import jx.csp.dialog.ShareDialog;
import jx.csp.dialog.ShareDialog.OnDeleteListener;
import jx.csp.model.def.MeetState;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.liveroom.LiveRoomActivityRouter;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import jx.csp.view.CircleProgressView;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.dialog.BaseDialog;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * 首页左右滑动列表的单个frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
@Route
public class MeetSingleFrag extends BaseFrag implements OnDeleteListener {

    private NetworkImageView mIvCover;
    private ImageView mIvLive;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvState;
    private View mVDivider;

    private CircleProgressView mProgressBar;
    private TextView mTvFiveSecond;

    @Arg
    Meet mMeet;
    private String mCourseId;

    @Override
    public void initData() {
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
        mIvLive = findView(R.id.iv_live);
        mTvTime = findView(R.id.tv_total_time);
        mTvCurrentPage = findView(R.id.tv_current_page);
        mTvTotalPage = findView(R.id.tv_total_page);
        mTvState = findView(R.id.tv_state);
        mVDivider = findView(R.id.slide_divider);
    }

    @Override
    public void setViews() {
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

        setOnClickListener(R.id.iv_share);
        setOnClickListener(R.id.main_slide_layout);
        setOnClickListener(R.id.iv_live);
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
            case R.id.iv_share: {
                //Fixme:传个假的url
                ShareDialog shareDialog = new ShareDialog(getContext(), "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
                shareDialog.setDeleteListener(this);
                shareDialog.show();
            }
            break;
            case R.id.main_slide_layout: {
                mCourseId = mMeet.getString(TMeet.id);
                if (mMeet.getInt(TMeet.playType) == PlayType.reb) {
                    if (mMeet.getInt(TMeet.playState) == PlayState.record) {
                        // CommonRecordActivityRouter.create(mCourseId).route(getContext());
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                        showHintDialog(getString(R.string.main_record_dialog));
                    } else {
                        CommonRecordActivityRouter.create(mCourseId).route(getContext());
                    }
                } else if (mMeet.getInt(TMeet.playType) == PlayType.live) {
                    if (mMeet.getInt(TMeet.liveState) == LiveState.un_start) {
                        App.showToast(R.string.live_not_start);
                    } else if (mMeet.getInt(TMeet.liveState) == LiveState.live) {
                        LiveRecordActivityRouter.create(mCourseId).route(getContext());
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                        showHintDialog(getString(R.string.main_live_dialog));
                    } else {
                        App.showToast(R.string.live_have_end);
                    }
                } else {
                    if (mMeet.getInt(TMeet.liveState) == LiveState.un_start) {
                        App.showToast(R.string.live_not_start);
                    } else if (mMeet.getInt(TMeet.liveState) == LiveState.live) {
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                        CommonDialog2 d = new CommonDialog2(getContext());
                        d.setHint(getString(R.string.choice_contents));
                        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                        d.addBlueButton(R.string.explain_meeting, view -> {
//                            LiveRecordActivityRouter.create(mCourseId).route(getContext());
                            // FIXME: 2017/10/30 两个设备分别选择，执行上，选择同一个，执行下,怎么判断是分别选择还是选择同一个
                            showHintDialog(getString(R.string.main_live_dialog));
                        });
                        d.addBlueButton(R.string.live_video, view -> {
//                            LiveRoomActivityRouter.create(mCourseId).route(getContext());
                            // FIXME: 2017/10/30 两个设备分别选择，执行上，选择同一个，执行下
                            showHintDialog(getString(R.string.main_live_dialog));
                        });
                        d.show();
                    } else {
                        App.showToast(R.string.live_have_end);
                    }
                    MeetVPFragRouter.create(mCourseId).route();
                }
            }
            break;
            case R.id.iv_live: {
                LiveRoomActivityRouter.create(mCourseId).route(getContext());
            }
            break;
        }
    }

    @Override
    public void delete() {
        exeNetworkReq(MeetingAPI.delete(mCourseId).build());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast(R.string.delete_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    public void showHintDialog(String hint) {
        CommonDialog2 d = new CommonDialog2(getContext());
        d.setHint(hint);
        d.addGrayButton(R.string.confirm_continue, view -> {
            BaseDialog dialog = new BaseDialog(getContext()) {

                @Override
                public void initData() {
                }

                @NonNull
                @Override
                public int getContentViewId() {
                    return R.layout.dialog_count_down;
                }

                @Override
                public void findViews() {
                    mProgressBar = findView(R.id.v_meeting_detail_progress);
                    mTvFiveSecond = findView(R.id.tv_five_second);
                }

                @Override
                public void setViews() {
                    mProgressBar.setProgress(0);
                    // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示TV五秒倒计时，websocket,如果a拒绝，则提示进入失败
//                  showView(mTvFiveSecond);
                }
            };
            dialog.show();
        });
        d.addBlueButton(R.string.cancel);
        d.show();
    }
}
