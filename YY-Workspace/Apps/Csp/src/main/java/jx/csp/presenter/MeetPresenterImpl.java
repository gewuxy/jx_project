package jx.csp.presenter;

import android.content.Context;
import android.support.annotation.StringRes;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.MeetContract;
import jx.csp.contact.MeetContract.V;
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
import jx.csp.ui.activity.liveroom.LiveRoomActivityRouter;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class MeetPresenterImpl extends BasePresenterImpl<MeetContract.V> implements MeetContract.P {

    private final int KJoinRecordCheckRedId = 1;
    private final int KJoinLiveRoomCheckRedId = 2;
    private final int KDeleteReqId = 3;
    private boolean mJoinLiveRoom = false; // 是否进入直播间 为了区分PlayType.video的时候进入的是直播视频还是音频

    private Context mContext;
    private Meet mMeet;
    private WebSocketServRouter mWebSocketServRouter;
    private CountdownDialog mCountdownDialog;

    private String mId;

    public MeetPresenterImpl(V v, Context context) {
        super(v);
        mContext = context;
    }

    @Override
    public void onMeetClick(Meet item) {
        mMeet = item;
        long startTime = item.getLong(TMeet.startTime);
        long endTime = item.getLong(TMeet.endTime);
        long currentTime = System.currentTimeMillis();
        switch (item.getInt(TMeet.playType)) {
            case PlayType.reb: {
                // 先判断是否有人在录播中  请求网络  不需要判断录播状态
                exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id)).build());
            }
            break;
            case PlayType.live: {
                if (startTime > currentTime) {
                    showToast(R.string.live_not_start);
                } else if (startTime < currentTime && endTime > currentTime) {
                    exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id)).build());
                } else {
                    showToast(R.string.live_have_end);
                }
            }
            break;
            case PlayType.video: {
                if (startTime > currentTime) {
                    showToast(R.string.live_not_start);
                } else if (startTime < currentTime && endTime > currentTime) {
                    // 选择进入视频直播还是音频直播
                    CommonDialog2 d = new CommonDialog2(mContext);
                    d.setHint(ResLoader.getString(R.string.choice_contents));
                    d.addGrayButton(R.string.explain_meeting, view -> {
                        // 先判断是否有人在直播音频
                        mJoinLiveRoom = false;
                        exeNetworkReq(KJoinRecordCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id)).build());
                    });
                    d.addBlueButton(R.string.live_video, view -> {
                        // 先判断是否有人在直播视频
                        // mJoinLiveRoom = true;
                        // FIXME: 2017/11/2 视频直播的websocket地址还没有给 暂时直接进入
                        LiveRoomActivityRouter.create()
                                .courseId(item.getString(TMeet.id))
                                .streamId(item.getString(TMeet.id))
                                .title(item.getString(TMeet.title))
                                .startTime(item.getLong(TMeet.startTime))
                                .stopTime(item.getLong(TMeet.endTime))
                                .route(mContext);
                        // exeNetworkReq(KJoinLiveRoomCheckRedId, MeetingAPI.joinCheck(item.getString(TMeet.id)).build());
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
        mId = item.getString(TMeet.id);
        //Fixme:传个假的url
        ShareDialog shareDialog = new ShareDialog(mContext, "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
        shareDialog.setDeleteListener(() -> exeNetworkReq(MeetingAPI.delete(mId).build()));
        shareDialog.setCopyListener(() -> {
            Notifier.inst().notify(NotifyType.copy_duplicate, mId);
            YSLog.d("ooooooooooo", mId + "dddd");
        });
        shareDialog.show();
    }

    @Override
    public void onLiveClick(Meet item) {
        // FIXME: 2017/11/2 视频直播的websocket地址还没有给 暂时直接进入
        LiveRoomActivityRouter.create()
                .courseId(item.getString(TMeet.id))
                .streamId(item.getString(TMeet.id))
                .title(item.getString(TMeet.title))
                .startTime(item.getLong(TMeet.startTime))
                .stopTime(item.getLong(TMeet.endTime))
                .route(mContext);
    }

    @Override
    public void allowJoin() {
        if (mWebSocketServRouter != null) {
            YSLog.d(TAG, "allowJoin WebSocketServRouter.stop");
            mWebSocketServRouter.stop(mContext);
        }
        join();
    }

    @Override
    public void disagreeJoin() {
        if (mWebSocketServRouter != null) {
            YSLog.d(TAG, "disagreeJoin WebSocketServRouter.stop");
            mWebSocketServRouter.stop(mContext);
        }
        if (mCountdownDialog != null) {
            mCountdownDialog.dismiss();
        }
        showToast(R.string.join_fail);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KJoinRecordCheckRedId) {
            return JsonParser.ev(r.getText(), Scan.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        switch (id) {
            case KJoinRecordCheckRedId: {
                Result<Scan> r = (Result<Scan>) result;
                if (r.isSucceed()) {
                    Scan scan = r.getData();
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
            case KJoinLiveRoomCheckRedId: {
                Result<Scan> r = (Result<Scan>) result;
                if (r.isSucceed()) {
                    Scan scan = r.getData();
                    if (scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(scan.getString(TScan.wsUrl))) {
                        // 有人情况下要弹dialog 确定后连websocket
                        showDialog(ResLoader.getString(R.string.main_live_dialog), scan.getString(TScan.wsUrl));
                    } else {
                        // 没人在的时候直接进入直播间
                        LiveRoomActivityRouter.create()
                                .courseId(mMeet.getString(TMeet.id))
                                .streamId(mMeet.getString(TMeet.id))
                                .title(mMeet.getString(TMeet.title))
                                .startTime(mMeet.getLong(TMeet.startTime))
                                .stopTime(mMeet.getLong(TMeet.endTime))
                                .route(mContext);
                    }
                }
            }
            break;
            case KDeleteReqId: {
                Result r = (Result) result;
                if (r.isSucceed()) {
                    showToast(R.string.delete_success);
                    //Fixme:还要通知列表删除？
                } else {
                    onNetworkError(id, r.getError());
                }
            }
            break;
        }
    }

    private void showDialog(String hint, String wsUrl) {
        CommonDialog2 d = new CommonDialog2(mContext);
        d.setHint(hint);
        d.addGrayButton(R.string.confirm_continue, view -> {
            mWebSocketServRouter = WebSocketServRouter.create(wsUrl);
            mWebSocketServRouter.route(mContext);
            if (mCountdownDialog == null) {
                mCountdownDialog = new CountdownDialog(mContext, 5);
            }
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
                CommonRecordActivityRouter.create(mMeet.getString(TMeet.id)).route(mContext);
            }
            break;
            case PlayType.live: {
                LiveRecordActivityRouter.create(mMeet.getString(TMeet.id))
                        .title(mMeet.getString(TMeet.title))
                        .startTime(mMeet.getLong(TMeet.startTime))
                        .stopTime(mMeet.getLong(TMeet.endTime))
                        .route(mContext);
            }
            break;
            case PlayType.video: {
                if (mJoinLiveRoom) {
                    LiveRoomActivityRouter.create()
                            .courseId(mMeet.getString(TMeet.id))
                            .streamId(mMeet.getString(TMeet.id))
                            .title(mMeet.getString(TMeet.title))
                            .startTime(mMeet.getLong(TMeet.startTime))
                            .stopTime(mMeet.getLong(TMeet.endTime))
                            .route(mContext);
                } else {
                    LiveRecordActivityRouter.create(mMeet.getString(TMeet.id))
                            .title(mMeet.getString(TMeet.title))
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
