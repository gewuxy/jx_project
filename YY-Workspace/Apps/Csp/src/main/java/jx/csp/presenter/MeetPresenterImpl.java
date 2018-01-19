package jx.csp.presenter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.MeetContract;
import jx.csp.contact.MeetContract.V;
import jx.csp.dialog.BtnVerticalDialog;
import jx.csp.dialog.CommonDialog2;
import jx.csp.dialog.CountdownDialog;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.livevideo.LiveVideoActivityRouter;
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

    private final int KJoinRecordCheckRedId = 1;
    private final int KJoinLiveVideoCheckRedId = 2;
    private boolean mJoinLiveVideo = false; // 是否进入直播间 为了区分PlayType.video的时候进入的是直播视频还是音频

    private Context mContext;
    private Meet mMeet;
    private CountdownDialog mCountdownDialog;
    private String mLiveRoomWsUrl;  // 视频直播的websocket地址
    private boolean mWsClose = false; // web socket 是否已经关闭
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
        mWsClose = false;
        mMeet = item;
        switch (item.getInt(TMeet.playType)) {
            case PlayType.reb: {
                // 只需要判断是否有人在录播中  请求网络  不根据录播状态判断
                exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
            }
            break;
            case PlayType.live: {
                if (item.getInt(TMeet.liveState) == LiveState.live || item.getInt(TMeet.liveState) == LiveState.stop) {
                    // 选择进入视频直播还是音频直播  中文版和英文版的dialog不一样
                    BtnVerticalDialog d = new BtnVerticalDialog(mContext);
                    d.setBtnStyle(LinearLayout.VERTICAL);
                    d.setTextHint(ResLoader.getString(R.string.choice_contents));
                    d.addBlackButton(R.string.continue_live, view -> {
                        // 先判断是否有人在直播音频
                        exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
                    });
                    d.addButton(R.string.record_live_stop, R.color.text_e43939, v ->
                    {
                        CommonDialog2 dialog = new CommonDialog2(mContext);
                        dialog.setHint(R.string.over_meeting);
                        dialog.addBlackButton(R.string.over, v1 ->
                                CommonServRouter.create(ReqType.over_live).courseId(item.getString(TMeet.id)).route(mContext));
                        dialog.addBlackButton(R.string.cancel_over);
                        dialog.show();
                    });
                    d.show();
                } else {
                    exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
                }
            }
            break;
            case PlayType.video: {
                // 选择进入视频直播还是音频直播  中文版和英文版的dialog不一样
                BtnVerticalDialog d = new BtnVerticalDialog(mContext);
                d.setBtnStyle(LinearLayout.VERTICAL);
                d.setTextHint(ResLoader.getString(R.string.choice_contents));
                d.addBlackButton(R.string.explain_meeting, view -> {
                    // 先判断是否有人在直播音频
                    mJoinLiveVideo = false;
                    exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
                });
                d.addBlackButton(R.string.live_video, view -> {
                    // 先判断是否有人在直播视频
                    mJoinLiveVideo = true;
                    exeNetworkReq(KJoinLiveVideoCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.video).build());
                });
                // 判断是否需要显示结束直播按钮
                if (item.getInt(TMeet.liveState) == LiveState.live || item.getInt(TMeet.liveState) == LiveState.stop) {
                    d.addButton(R.string.record_live_stop, R.color.text_e43939, v -> {
                        CommonDialog2 dialog = new CommonDialog2(mContext);
                        dialog.setHint(R.string.over_meeting);
                        dialog.addBlackButton(R.string.over, v1 ->
                                CommonServRouter.create(ReqType.over_live).courseId(item.getString(TMeet.id)).route(mContext));
                        dialog.addBlackButton(R.string.cancel_over);
                        dialog.show();
                    });
                }
                d.show();
            }
            break;
        }
    }

    @Override
    public void onShareClick(Meet item) {
        mMeet = item;
        ShareDialog shareDialog = new ShareDialog(mContext, mMeet);
        shareDialog.show();
    }

    @Override
    public void onLiveClick(Meet item) {
        // 先判断会议是否已经开始，再判断是否有人在直播视频
        mWsClose = false;
        mMeet = item;
        mJoinLiveVideo = true;
        exeNetworkReq(KJoinLiveVideoCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.video).build());
    }

    @Override
    public void allowEnter() {
        if (!mWsClose) {
            mWsClose = true;
            YSLog.d(TAG, "MeetPresenterImpl enter WebSocketServRouter.stop");
            WebSocketServRouter.stop(mContext);
        }
        join();
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
        if (id == KJoinRecordCheckRedId || id == KJoinLiveVideoCheckRedId) {
            return JsonParser.ev(r.getText(), Scan.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case KJoinRecordCheckRedId: {
                if (r.isSucceed()) {
                    Scan scan = (Scan) r.getData();
                    mServerTime = scan.getLong(TScan.serverTime);
                    long startTime = mMeet.getLong(TMeet.startTime);
                    long endTime = mMeet.getLong(TMeet.endTime);
                    // 是否有人在直播或者录播
                    boolean state = scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(scan.getString(TScan.wsUrl));
                    // 先判断直播时间是否已经到了 录播不需要判断时间  再判断是否有人在录播或者直播
                    // 有人情况下要弹dialog 确定后连websocket
                    switch (mMeet.getInt(TMeet.playType)) {
                        case PlayType.reb: {
                            if (state) {
                                showDialog(ResLoader.getString(R.string.main_record_dialog), scan.getString(TScan.wsUrl));
                            } else {
                                // 没人在的时候直接进入
                                join();
                            }
                        }
                        break;
                        case PlayType.live:
                        case PlayType.video: {
                            if (startTime > mServerTime) {
                                showToast(R.string.live_not_start);
                            } else if (startTime < mServerTime && endTime > mServerTime) {
                                if (state) {
                                    showDialog(ResLoader.getString(R.string.main_live_dialog), scan.getString(TScan.wsUrl));
                                } else {
                                    join();
                                }
                            } else {
                                showToast(R.string.live_have_end);
                            }
                        }
                        break;
                    }
                } else {
                    showToast(r.getError().getMessage());
                }
            }
            break;
            case KJoinLiveVideoCheckRedId: {
                if (r.isSucceed()) {
                    Scan scan = (Scan) r.getData();
                    mLiveRoomWsUrl = scan.getString(TScan.wsUrl);
                    mServerTime = scan.getLong(TScan.serverTime);
                    mPushUrl = scan.getString(TScan.pushUrl);
                    long startTime = mMeet.getLong(TMeet.startTime);
                    long endTime = mMeet.getLong(TMeet.endTime);
                    // 是否有人在直播
                    boolean state = scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(mLiveRoomWsUrl);
                    if (startTime > mServerTime) {
                        showToast(R.string.live_not_start);
                    } else if (startTime < mServerTime && endTime > mServerTime) {
                        if (state) {
                            // 有人情况下要弹dialog 确定后连websocket
                            showDialog(ResLoader.getString(R.string.main_live_dialog), mLiveRoomWsUrl);
                        } else {
                            // 没人在的时候直接进入直播间
                            LiveVideoActivityRouter.create()
                                    .courseId(mMeet.getString(TMeet.id))
                                    .startTime(mMeet.getLong(TMeet.startTime))
                                    .stopTime(mMeet.getLong(TMeet.endTime))
                                    .serverTime(mServerTime)
                                    .wsUrl(mLiveRoomWsUrl)
                                    .pushUrl(mPushUrl)
                                    .route(mContext);
                        }
                    } else {
                        showToast(R.string.live_have_end);
                    }
                } else {
                    showToast(r.getError().getMessage());
                }
            }
            break;
        }
    }

    private void showDialog(String hint, String wsUrl) {
        CommonDialog2 d = new CommonDialog2(mContext);
        d.setHint(hint);
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

    private void join() {
        if (mCountdownDialog != null) {
            mCountdownDialog.dismiss();
        }
        switch (mMeet.getInt(TMeet.playType)) {
            case PlayType.reb: {
                RecordActivityRouter.create(mMeet.getString(TMeet.id),
                        mMeet.getString(TMeet.coverUrl),
                        mMeet.getString(TMeet.title))
                        .route(mContext);
            }
            break;
            case PlayType.live: {
                LiveAudioActivityRouter.create(mMeet.getString(TMeet.id),
                        mMeet.getString(TMeet.coverUrl),
                        mMeet.getString(TMeet.title))
                        .startTime(mMeet.getLong(TMeet.startTime))
                        .stopTime(mMeet.getLong(TMeet.endTime))
                        .route(mContext);
            }
            break;
            case PlayType.video: {
                if (mJoinLiveVideo) {
                    LiveVideoActivityRouter.create()
                            .courseId(mMeet.getString(TMeet.id))
                            .startTime(mMeet.getLong(TMeet.startTime))
                            .stopTime(mMeet.getLong(TMeet.endTime))
                            .serverTime(mServerTime)
                            .wsUrl(mLiveRoomWsUrl)
                            .pushUrl(mPushUrl)
                            .route(mContext);
                } else {
                    LiveAudioActivityRouter.create(mMeet.getString(TMeet.id),
                            mMeet.getString(TMeet.coverUrl),
                            mMeet.getString(TMeet.title))
                            .startTime(mMeet.getLong(TMeet.startTime))
                            .stopTime(mMeet.getLong(TMeet.endTime))
                            .route(mContext);
                }
            }
            break;
        }
    }

    private void showToast(@StringRes int... ids) {
        App.showToast(ids);
    }

    private void showToast(String str) {
        App.showToast(str);
    }
}
