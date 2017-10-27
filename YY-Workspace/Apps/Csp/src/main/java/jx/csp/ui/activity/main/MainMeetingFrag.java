package jx.csp.ui.activity.main;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.App;
import jx.csp.R;
import jx.csp.dialog.HintDialogMain;
import jx.csp.dialog.ShareDialog;
import jx.csp.dialog.ShareDialog.OnDeleteListener;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.liveroom.LiveRoomActivity;
import jx.csp.ui.activity.record.LiveRecordActivity;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */
@Route
public class MainMeetingFrag extends BaseFrag implements OnDeleteListener {

    private NetworkImageView mIvCover;
    private ImageView mIvLive;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvCurrentPage;
    private TextView mTvTotalPage;
    private TextView mTvState;
    private View mVDivider;

    @Arg
    Square mSquare;
    private String mCourseId;

    @IntDef({
            StateType.otherState,
            StateType.living,
            StateType.playing,

    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface StateType {
        int otherState = 0; // 其他状态
        int living = 1; // 直播中
        int playing = 2; // 录播中
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_main_slide;
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
                .url(mSquare.getString(TSquare.coverUrl))
                .load();
        mTvTitle.setText(mSquare.getString(TSquare.title));
        mTvTotalPage.setText(mSquare.getString(TSquare.pageCount));

        if (mSquare.getInt(TSquare.playType) == PlayType.reb) {
            mTvTime.setText(mSquare.getString(TSquare.playTime));
            mTvCurrentPage.setText(mSquare.getString(TSquare.playPage));

            if (mSquare.getInt(TSquare.playState) == PlayState.un_start) {
                mTvState.setText(R.string.record);
            } else if (mSquare.getInt(TSquare.playState) == PlayState.record) {
                mTvState.setText(R.string.on_record);
            } else {
               /* goneView(mTvCurrentPage);
                goneView(mVDivider);*/
                goneView(mTvState);
            }
            goneView(mIvLive);
        } else {
            mTvCurrentPage.setText(mSquare.getString(TSquare.livePage));

            if (mSquare.getInt(TSquare.liveState) == LiveState.un_start) {
                mTvState.setText(R.string.solive);

                //直播的开始时间转换
                Date d = new Date(Long.parseLong(mSquare.getString(TSquare.startTime)));
                SimpleDateFormat data = new SimpleDateFormat("MM月dd日 HH:mm");
                mTvTime.setText(data.format(d));
            } else if (mSquare.getInt(TSquare.liveState) == LiveState.live) {
                mTvState.setText(R.string.on_solive);
                mTvTime.setText(mSquare.getString(TSquare.playTime));
            } else {
               /* goneView(mTvCurrentPage);
                goneView(mVDivider);*/
                mTvState.setText(R.string.on_solive);
//                goneView(mIvLive);
                mTvTime.setText(mSquare.getString(TSquare.playTime));
            }
        }

        setOnClickListener(R.id.iv_share);
        setOnClickListener(R.id.main_slide_layout);
        setOnClickListener(R.id.iv_live);
    }

    public int getType() {
        if (mSquare.getInt(TSquare.liveState) == LiveState.live) {
            return StateType.living;
        } else if (mSquare.getInt(TSquare.playState) == PlayState.record) {
            return StateType.playing;
        } else {
            return StateType.otherState;
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
                mCourseId = mSquare.getString(TSquare.id);
                if (mSquare.getInt(TSquare.playType) == PlayType.reb) {
                    if (mSquare.getInt(TSquare.playState) == PlayState.record) {
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                        HintDialogMain d = new HintDialogMain(getContext());
                        d.setHint(getString(R.string.main_record_dialog));
                        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                        d.addBlueButton(R.string.confirm_continue, view -> finish());
                        d.addBlueButton(R.string.cancel);
                        d.show();
                    }
//                    CommonRecordActivityRouter.create(mCourseId).route(getContext());
                } else {
                    if (mSquare.getInt(TSquare.playType) == PlayType.live) {
                        App.showToast(R.string.live_not_start);
                    } else if (mSquare.getInt(TSquare.liveState) == LiveState.live) {
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                        HintDialogMain d = new HintDialogMain(getContext());
                        d.setHint(getString(R.string.choice_contents));
                        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                        d.addBlueButton(R.string.explain_meeting, view -> startActivity(LiveRecordActivity.class));
                        d.addBlueButton(R.string.live_video, view -> startActivity(LiveRoomActivity.class));
                        d.show();
                    } else {
                        App.showToast(R.string.live_have_end);
                    }
                   /* LiveRecordActivityRouter.create(mCourseId).route(getContext());
                    MainSlideFragRouter.create(mCourseId).route();*/
                }
            }
            break;
            case R.id.iv_live: {
                startActivity(LiveRoomActivity.class);
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
            showToast("删除成功");
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
