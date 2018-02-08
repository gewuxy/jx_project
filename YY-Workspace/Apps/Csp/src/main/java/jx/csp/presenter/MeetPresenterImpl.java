package jx.csp.presenter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.MeetContract;
import jx.csp.contact.MeetContract.V;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.dialog.CommonDialog;
import jx.csp.dialog.CommonDialog1;
import jx.csp.dialog.CommonDialog2;
import jx.csp.dialog.CountdownDialog;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Live;
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.livevideo.LiveVideoActivityRouter;
import jx.csp.ui.activity.main.LiveStarActivityRouter;
import jx.csp.ui.activity.record.LiveAudioActivityRouter;
import jx.csp.ui.activity.record.RecordActivityRouter;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class MeetPresenterImpl extends BasePresenterImpl<MeetContract.V> implements MeetContract.P {

    private final int KRecordCheckId = 1;
    private final int KVideoCheckId = 2;

    private Context mContext;
    private Meet mMeet;
    private CountdownDialog mCountdownDialog;
    private String mLiveRoomWsUrl;  // 视频直播的websocket地址
    private boolean mWsClose = false; // web socket 是否已经关闭
    private boolean mIsVideo = false; // 视频还是ppt
    private long mServerTime;
    private String mPushUrl;

    @IntDef({
            LiveType.ppt,
            LiveType.video,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LiveType {
        int ppt = 0; // 课件讲解
        int video = 1; // 视频直播
    }

    public MeetPresenterImpl(V v, Context context) {
        super(v);
        mContext = context;
    }

    @Override
    public void onMeetClick(Meet item) {
        mIsVideo = false;
        mWsClose = false;
        mMeet = item;
        switch (item.getInt(TMeet.playType)) {
            case CourseType.reb: {
                // 判断是否有人在录播中
                checkPpt();
            }
            break;
            case CourseType.ppt_live:
            case CourseType.ppt_video_live: {
                if (mMeet.getInt(TMeet.liveState) == Live.LiveState.un_start) {
                    checkPpt();
                } else {
                    joinPpt(true);
                }
            }
            break;
        }
    }

    @Override
    public void onLiveClick(Meet item) {
        mIsVideo = true;
        mWsClose = false;
        mMeet = item;
        if (mMeet.getInt(TMeet.liveState) == Live.LiveState.un_start) {
            // 会议未开始
            CommonDialog d = new CommonDialog(mContext);
            d.addHintView(View.inflate(mContext, R.layout.layout_live_hint, null));
            d.addButton(R.string.cancel, R.color.text_333, null);
            // 再判断是否有人在直播视频
            d.addButton(R.string.live_start, R.color.text_333, l -> checkVideo());
            d.show();
        } else {
            joinVideo(true);
        }
    }

    @Override
    public void onShareClick(Meet item) {
        mMeet = item;
        ShareDialog shareDialog = new ShareDialog(mContext, mMeet);
        shareDialog.show();
    }

    @Override
    public void allowEnter() {
        if (!mWsClose) {
            mWsClose = true;
            YSLog.d(TAG, "MeetPresenterImpl enter WebSocketServRouter.stop");
            WebSocketServRouter.stop(mContext);
        }
        if (mIsVideo) {
            joinVideo(false);
        } else {
            joinPpt(false);
        }
    }

    @Override
    public void notAllowEnter() {
        if (!mWsClose) {
            mWsClose = true;
            YSLog.d(TAG, "MeetPresenterImpl noEnter WebSocketServRouter.stop");
            WebSocketServRouter.stop(mContext);
        }
        if (mCountdownDialog != null) {
            mCountdownDialog.dismiss();
        }
        showToast(R.string.join_fail);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KRecordCheckId || id == KVideoCheckId) {
            return JsonParser.ev(r.getText(), Scan.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case KRecordCheckId: {
                if (r.isSucceed()) {
                    Scan scan = (Scan) r.getData();
                    mServerTime = scan.getLong(TScan.serverTime);
                    // 是否有人在直播或者录播
                    boolean state = scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(scan.getString(TScan.wsUrl));
                    // 先判断直播时间是否已经到了 录播不需要判断时间  再判断是否有人在录播或者直播
                    // 有人情况下要弹dialog 确定后连websocket
                    if (state) {
                        switch (mMeet.getInt(TMeet.playType)) {
                            case CourseType.reb: {
                                showDialog(ResLoader.getString(R.string.main_record_dialog), scan.getString(TScan.wsUrl));
                            }
                            break;
                            case CourseType.ppt_live:
                            case CourseType.ppt_video_live: {
                                showDialog(ResLoader.getString(R.string.main_live_dialog), scan.getString(TScan.wsUrl));
                            }
                            break;
                        }
                    } else {
                        joinPpt(false);
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
            case KVideoCheckId: {
                if (r.isSucceed()) {
                    Scan scan = (Scan) r.getData();
                    mLiveRoomWsUrl = scan.getString(TScan.wsUrl);
                    mServerTime = scan.getLong(TScan.serverTime);
                    mPushUrl = scan.getString(TScan.pushUrl);
                    // 是否有人在直播
                    boolean state = scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(mLiveRoomWsUrl);
                    if (state) {
                        // 有人情况下要弹dialog 确定后连websocket
                        showDialog(ResLoader.getString(R.string.main_live_dialog), mLiveRoomWsUrl);
                    } else {
                        // 没人在的时候直接进入直播间
                        joinVideo(false);
                    }
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    /**
     * 抢占询问
     *
     * @param hint  抢占ppt或视频
     * @param wsUrl wsUrl
     */
    private void showDialog(String hint, String wsUrl) {
        CommonDialog1 d = new CommonDialog1(mContext);
        d.setTitle(R.string.main_record_continue);
        d.setContent(hint);
        d.addBlackButton(R.string.confirm_continue, view -> {
            WebSocketServRouter.create(wsUrl).route(mContext);
            if (mCountdownDialog == null) {
                // 倒计时结束没有收到websocket默认进入会议
                mCountdownDialog = new CountdownDialog(mContext, 15);
            }
            mCountdownDialog.setOnDismissListener(dialogInterface -> {
                if (!mWsClose) {
                    mWsClose = true;
                    YSLog.d(TAG, "MeetPresenterImpl count down dialog dismiss WebSocketServRouter.stop");
                    WebSocketServRouter.stop(mContext);
                    mCountdownDialog.stopCountDown();
                }
            });
            mCountdownDialog.show();
        });
        d.addBlackButton(R.string.cancel);
        d.show();
    }

    /**
     * 进入ppt
     *
     * @param needCheck 是否需要检查有没有人
     */
    private void joinPpt(boolean needCheck) {
        if (mCountdownDialog != null) {
            mCountdownDialog.dismiss();
        }
        switch (mMeet.getInt(TMeet.playType)) {
            case CourseType.reb: {
                RecordActivityRouter.create(mMeet.getString(TMeet.id)).route(mContext);
            }
            break;
            case CourseType.ppt_live:
            case CourseType.ppt_video_live: {
                if (needCheck) {
                    liveAction(mMeet, l -> checkPpt());
                } else {
                    LiveAudioActivityRouter.create(mMeet.getString(TMeet.id)).route(mContext);
                }
            }
            break;
        }
    }

    /**
     * 进入视频
     *
     * @param needCheck 是否需要检查有没有人
     */
    private void joinVideo(boolean needCheck) {
        if (mCountdownDialog != null) {
            mCountdownDialog.dismiss();
        }
        if (needCheck) {
            // 判断是否有人在直播中
            liveAction(mMeet, l -> checkVideo());
        } else {
            // 立即直播
            LiveVideoActivityRouter.create()
                    .courseId(mMeet.getString(TMeet.id))
                    .startTime(mMeet.getLong(TMeet.startTime))
                    .serverTime(mServerTime)
                    .wsUrl(mLiveRoomWsUrl)
                    .pushUrl(mPushUrl)
                    .liveVideoState(mMeet.getInt(TMeet.liveState))
                    .route(mContext);
        }
    }

    /**
     * 检查Ppt
     */
    private void checkPpt() {
        exeNetworkReq(KRecordCheckId, MeetingAPI.joinCheck(mMeet.getString(TMeet.id), LiveType.ppt).build());
    }

    /**
     * 检查视频
     */
    private void checkVideo() {
        exeNetworkReq(KVideoCheckId, MeetingAPI.joinCheck(mMeet.getString(TMeet.id), LiveType.video).build());
    }

    /**
     * 选择直播操作
     *
     * @param item meet
     */
    private void liveAction(Meet item, @Nullable View.OnClickListener l) {
        int liveState = mMeet.getInt(TMeet.liveState);
        switch (liveState) {
            case Live.LiveState.live:
            case Live.LiveState.stop: {
                // 选择进入直播  中文版和英文版的dialog不一样
                BtnVerticalDialog d = new BtnVerticalDialog(mContext);
                d.setBtnStyle(LinearLayout.VERTICAL);
                d.setTextHint(ResLoader.getString(R.string.choice_contents));
                d.addBlackButton(R.string.live_continue, l);
                // 判断是否需要显示结束直播按钮
                if (item.getBoolean(TMeet.starRateFlag)) {
                    d.addButton(R.string.start_star, R.color.text_e43939, v -> toStart(item));
                } else {
                    d.addButton(R.string.record_live_stop, R.color.text_e43939, v -> toEndMeet(item));
                }
                d.show();
            }
            break;
            case Live.LiveState.to_end:
            case Live.LiveState.end:
            case Live.LiveState.star: {
                LiveStarActivityRouter.create(item).route(mContext);
            }
            break;
        }
    }

    /**
     * 开启星评
     *
     * @param item meet
     */
    private void toStart(Meet item) {
        CommonDialog2 dialog = new CommonDialog2(mContext);
        dialog.setHint(R.string.start_start_hint);
        dialog.addBlackButton(R.string.cancel_over, null);
        dialog.addBlackButton(R.string.over, v1 ->
                LiveStarActivityRouter.create(item)
                        .route(mContext));
        dialog.show();
    }

    /**
     * 结束会议
     *
     * @param item meet
     */
    private void toEndMeet(Meet item) {
        CommonDialog2 dialog = new CommonDialog2(mContext);
        dialog.setHint(R.string.over_meeting);
        dialog.addBlackButton(R.string.cancel_over, null);
        dialog.addBlackButton(R.string.over, v1 ->
                CommonServRouter.create(ReqType.over_live).courseId(item.getString(TMeet.id)).route(mContext));
        dialog.show();
    }

    private void showToast(@StringRes int... ids) {
        App.showToast(ids);
    }

    private void showToast(String str) {
        App.showToast(str);
    }
}
