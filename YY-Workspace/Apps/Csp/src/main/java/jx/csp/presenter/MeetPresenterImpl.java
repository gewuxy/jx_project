package jx.csp.presenter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

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
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.livevideo.LiveVideoActivityRouter;
import jx.csp.ui.activity.record.LiveAudioActivityRouter;
import jx.csp.ui.activity.record.RecordActivityRouter;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.contract.BasePresenterImpl;

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
        long startTime = item.getLong(TMeet.startTime);
        long endTime = item.getLong(TMeet.endTime);
        long currentTime = System.currentTimeMillis();
        switch (item.getInt(TMeet.playType)) {
            case PlayType.reb: {
                // 只需要判断是否有人在录播中  请求网络  不根据录播状态判断
                exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
            }
            break;
            case PlayType.live: {
                if (startTime > currentTime) {
                    showToast(R.string.live_not_start);
                } else if (startTime < currentTime && endTime > currentTime) {
                    exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
                } else {
                    showToast(R.string.live_have_end);
                }
            }
            break;
            case PlayType.video: {
                if (startTime > currentTime) {
                    showToast(R.string.live_not_start);
                } else if (startTime < currentTime && endTime > currentTime) {
                    // 选择进入视频直播还是音频直播  中文版和英文版的dialog不一样
                    BtnVerticalDialog d = new BtnVerticalDialog(mContext);
                    d.setTextHint(ResLoader.getString(R.string.choice_contents));
                    d.addBlackButton(R.string.explain_meeting, view -> {
                        // 先判断是否有人在直播音频
                        mJoinLiveVideo = false;
                        exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.ppt).build());
                    });
                    d.addBlueButton(R.string.live_video, view -> {
                        // 先判断是否有人在直播视频
                        mJoinLiveVideo = true;
                        exeNetworkReq(KJoinLiveVideoCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.video).build());
                    });
                    d.show();
                } else {
                    showToast(R.string.live_have_end);
                }
            }
            break;
        }
    }

    @Override
    public void onShareClick(Meet item) {
        mMeet = item;
        ShareDialog shareDialog = new ShareDialog(mContext,
                item.getString(TMeet.id),
                item.getString(TMeet.title),
                item.getString(TMeet.coverUrl));
        shareDialog.show();
    }

    @Override
    public void onLiveClick(Meet item) {
        // 先判断会议是否已经开始，再判断是否有人在直播视频
        mWsClose = false;
        mMeet = item;
        long startTime = item.getLong(TMeet.startTime);
        long endTime = item.getLong(TMeet.endTime);
        long currentTime = System.currentTimeMillis();
        if (startTime > currentTime) {
            showToast(R.string.live_not_start);
        } else if (startTime < currentTime && endTime > currentTime) {
            mJoinLiveVideo = true;
            exeNetworkReq(KJoinLiveVideoCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id), LiveType.video).build());
        } else {
            showToast(R.string.live_have_end);
        }
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
                    if (scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(scan.getString(TScan.wsUrl))) {
                        // 有人情况下要弹dialog 确定后连websocket
                        switch (mMeet.getInt(TMeet.playType)) {
                            case PlayType.reb: {
                                showDialog(ResLoader.getString(R.string.main_record_dialog), scan.getString(TScan.wsUrl));
                            }
                            break;
                            case PlayType.live: {
                                showDialog(ResLoader.getString(R.string.main_live_dialog), scan.getString(TScan.wsUrl));
                            }
                            break;
                            case PlayType.video: {
                                showDialog(ResLoader.getString(R.string.main_live_dialog), scan.getString(TScan.wsUrl));
                            }
                            break;
                        }
                    } else {
                        // 没人在的时候直接进入
                        join();
                    }
                }
            }
            break;
            case KJoinLiveVideoCheckRedId: {
                if (r.isSucceed()) {
                    Scan scan = (Scan) r.getData();
                    mLiveRoomWsUrl = scan.getString(TScan.wsUrl);
                    if (scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(mLiveRoomWsUrl)) {
                        // 有人情况下要弹dialog 确定后连websocket
                        showDialog(ResLoader.getString(R.string.main_live_dialog), mLiveRoomWsUrl);
                    } else {
                        // 没人在的时候直接进入直播间
                        LiveVideoActivityRouter.create()
                                .courseId(mMeet.getString(TMeet.id))
                                .streamId(mMeet.getString(TMeet.id))
                                .title(mMeet.getString(TMeet.title))
                                .startTime(mMeet.getLong(TMeet.startTime))
                                .stopTime(mMeet.getLong(TMeet.endTime))
                                .wsUrl(mLiveRoomWsUrl)
                                .route(mContext);
                    }
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
        d.addBlueButton(R.string.cancel);
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
                            .streamId(mMeet.getString(TMeet.id))
                            .title(mMeet.getString(TMeet.title))
                            .startTime(mMeet.getLong(TMeet.startTime))
                            .stopTime(mMeet.getLong(TMeet.endTime))
                            .wsUrl(mLiveRoomWsUrl)
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
}
