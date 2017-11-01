package jx.csp.ui.frag.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.TextView;

import jx.csp.App;
import jx.csp.R;
import jx.csp.adapter.main.MeetGridAdapter;
import jx.csp.dialog.HintDialogMain;
import jx.csp.dialog.ShareDialog;
import jx.csp.dialog.ShareDialog.OnDeleteListener;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.liveroom.LiveRoomActivityRouter;
import jx.csp.ui.activity.main.MainActivity.OnSquareRefreshListener;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import jx.csp.view.CircleProgressView;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.yy.dialog.BaseDialog;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * 首页九宫格的frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
public class MeetGridFrag extends BaseSRRecyclerFrag<Meet, MeetGridAdapter>
        implements OnAdapterClickListener, OnDeleteListener {

    private final int KIdGetData = 0;
    private final int KIdDelete = 1;

    private CircleProgressView mProgressBar;
    private TextView mTvFiveSecond;
    private OnSquareRefreshListener mListener;
    private String mCourseId;

    public void setListener(OnSquareRefreshListener listener) {
        mListener = listener;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_main_recycler;
    }

    public int getPosition() {
        RecyclerView rv = (RecyclerView) getScrollableView();
        GridLayoutManager l = (GridLayoutManager) rv.getLayoutManager();
        return l.findFirstVisibleItemPosition();
    }

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KIdGetData, MeetingAPI.meetingList().pageNum(getOffset()).pageSize(getLimit()).build());
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        if (mListener != null) {
            mListener.onSquareRefresh(getData());
        }

        getAdapter().setOnAdapterClickListener(this);
    }

    @Override
    public void onSwipeRefreshAction() {
    }

    @Override
    public int getOffset() {
        return 1;
    }

    @Override
    public void onAdapterClick(int position, View v) {
        switch (v.getId()) {
            case R.id.main_square_layout: {
                mCourseId = getItem(position).getString(TMeet.id);
                if (getItem(position).getInt(TMeet.playType) == PlayType.reb) {
                    if (getItem(position).getInt(TMeet.playState) == PlayState.record) {
//                        CommonRecordActivityRouter.create(mCourseId).route(getContext());
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                        showHintDialog(getString(R.string.main_record_dialog));
                    } else {
                        CommonRecordActivityRouter.create(mCourseId).route(getContext());
                    }
                } else if (getItem(position).getInt(TMeet.playType) == PlayType.live) {
                    if (getItem(position).getInt(TMeet.liveState) == LiveState.un_start) {
                        showToast(R.string.live_not_start);
                    } else if (getItem(position).getInt(TMeet.liveState) == LiveState.live) {
//                        LiveRecordActivityRouter.create(mCourseId).route(getContext());
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号,等后台返回，如果是两个设备，执行下，如果不是，则执行上
                        showHintDialog(getString(R.string.main_live_dialog));
                    } else {
                        showToast(R.string.live_have_end);
                        LiveRecordActivityRouter.create(mCourseId).route(getContext());
                    }
                } else {
                    if (getItem(position).getInt(TMeet.liveState) == LiveState.un_start) {
                        showToast(R.string.live_not_start);
                    } else if (getItem(position).getInt(TMeet.liveState) == LiveState.live) {
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                        HintDialogMain d = new HintDialogMain(getContext());
                        d.setHint(getString(R.string.choice_contents));
                        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                        d.addGrayButton(R.string.explain_meeting, view -> {
//                            LiveRecordActivityRouter.create(mCourseId).route(getContext());
                            // FIXME: 2017/10/30 两个设备分别选择，执行上，选择同一个，执行下
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
                }
            }
            break;
            case R.id.iv_square_share: {
                //Fixme:传个假的url
                ShareDialog shareDialog = new ShareDialog(getContext(), "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
                shareDialog.setDeleteListener(this);
                shareDialog.show();
            }
            break;
            case R.id.iv_square_live: {
                LiveRoomActivityRouter.create(mCourseId).route(getContext());
            }
            break;
        }
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_main_footer);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.ready);
    }

    @Override
    public void delete() {
        exeNetworkReq(KIdDelete, MeetingAPI.delete(mCourseId).build());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdDelete) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast(R.string.delete_success);
                //Fixme:还要通知列表删除？
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
        }

    }

    public void showHintDialog(String hint) {
        HintDialogMain d = new HintDialogMain(getContext());
        d.setHint(hint);
        d.addGrayButton(R.string.confirm_continue, view -> {
            BaseDialog dialog = new BaseDialog(getContext()) {

                @Override
                public void initData() {
                }

                @NonNull
                @Override
                public int getContentViewId() {
                    return R.layout.dialog_main_five_second;
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
