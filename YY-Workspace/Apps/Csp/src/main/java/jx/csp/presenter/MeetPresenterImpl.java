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
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.liveroom.LiveRoomActivityRouter;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import lib.ys.util.res.ResLoader;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class MeetPresenterImpl extends BasePresenterImpl<MeetContract.V> implements MeetContract.P {

    private Context mContext;

    public MeetPresenterImpl(V v, Context context) {
        super(v);
        mContext = context;
    }

    @Override
    public void onMeetClick(Meet item) {
        if (item.getInt(TMeet.playType) == PlayType.reb) {
            if (item.getInt(TMeet.playState) == PlayState.record) {
                // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                showHintDialog(ResLoader.getString(R.string.main_record_dialog));
            } else {
                CommonRecordActivityRouter.create(item.getString(TMeet.id)).route(mContext);
            }
        } else if (item.getInt(TMeet.playType) == PlayType.live) {
            if (item.getInt(TMeet.liveState) == LiveState.un_start) {
                showToast(R.string.live_not_start);
            } else if (item.getInt(TMeet.liveState) == LiveState.live) {
//                        LiveRecordActivityRouter.create(mCourseId).route(getContext());
                // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                showHintDialog(ResLoader.getString(R.string.main_live_dialog));
            } else {
                showToast(R.string.live_have_end);
                LiveRecordActivityRouter.create(item.getString(TMeet.id)).route(mContext);
            }
        } else {
            if (item.getInt(TMeet.liveState) == LiveState.un_start) {
                showToast(R.string.live_not_start);
            } else if (item.getInt(TMeet.liveState) == LiveState.live) {
                // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                CommonDialog2 d = new CommonDialog2(mContext);
                d.setHint(ResLoader.getString(R.string.choice_contents));
                // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                d.addGrayButton(R.string.explain_meeting, view -> {
//                            LiveRecordActivityRouter.create(mCourseId).route(getContext());
                    // FIXME: 2017/10/30 两个设备分别选择，执行上，选择同一个，执行下
                    showHintDialog(ResLoader.getString(R.string.main_live_dialog));
                });
                d.addBlueButton(R.string.live_video, view -> {
//                            LiveRoomActivityRouter.create(mCourseId).route(getContext());
                    // FIXME: 2017/10/30 两个设备分别选择，执行上，选择同一个，执行下
                    showHintDialog(ResLoader.getString(R.string.main_live_dialog));
                });
                d.show();
            } else {
                showToast(R.string.live_have_end);
            }
        }
    }

    @Override
    public void onShareClick(Meet item) {
        //Fixme:传个假的url
        ShareDialog shareDialog = new ShareDialog(mContext, "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
        shareDialog.setDeleteListener(() -> exeNetworkReq(MeetingAPI.delete(item.getString(TMeet.id)).build()));
        shareDialog.show();
    }

    @Override
    public void onLiveClick(Meet item) {
        LiveRoomActivityRouter.create(item.getString(TMeet.id)).route(mContext);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast(R.string.delete_success);
            //Fixme:还要通知列表删除？
        } else {
            onNetworkError(id, r.getError());
        }
    }

    private void showHintDialog(String hint) {
        CommonDialog2 d = new CommonDialog2(mContext);
        d.setHint(hint);
        d.addGrayButton(R.string.confirm_continue, view -> new CountdownDialog(mContext).show());
        d.addBlueButton(R.string.cancel);
        d.show();
    }

    private void showToast(@StringRes int... ids) {
        App.showToast(ids);
    }
}
