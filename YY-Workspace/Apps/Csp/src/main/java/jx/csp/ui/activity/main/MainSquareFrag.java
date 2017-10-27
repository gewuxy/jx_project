package jx.csp.ui.activity.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import jx.csp.App;
import jx.csp.R;
import jx.csp.adapter.main.SquareAdapter;
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
import jx.csp.ui.activity.main.MainActivity.OnSquareRefreshListener;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivity;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */
public class MainSquareFrag extends BaseSRRecyclerFrag<Square, SquareAdapter>
        implements OnAdapterClickListener, OnDeleteListener {

    private final int KIdGetData = 0;
    private final int KIdDelete = 1;

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


    /*public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition();
        RecyclerView rv = (RecyclerView) getScrollableView();
        getScrollable().smoothScrollToPosition(position);
    }*/

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
                mCourseId = getItem(position).getString(TSquare.id);
                if (getItem(position).getInt(TSquare.playType) == PlayType.reb) {
                    if (getItem(position).getInt(TSquare.playState) == PlayState.record) {
                        // FIXME: 2017/10/26 还要判断两个设备，登录同一个账号
                        HintDialogMain d = new HintDialogMain(getContext());
                        d.setHint(getString(R.string.main_record_dialog));
                        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示五秒倒计时，应该是一个请求,请求成功弹框
                        d.addBlueButton(R.string.confirm_continue, view -> finish());
                        d.addBlueButton(R.string.cancel);
                        d.show();
                    } else {
                        CommonRecordActivityRouter.create(mCourseId).route(getContext());
                    }
                } else {
                    if (getItem(position).getInt(TSquare.liveState) == LiveState.un_start) {
                        App.showToast(R.string.live_not_start);
                    } else if (getItem(position).getInt(TSquare.liveState) == LiveState.live) {
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

//                    LiveRecordActivityRouter.create(mCourseId).route(getContext());
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
                startActivity(LiveRoomActivity.class);
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
                showToast("删除成功");
                //Fixme:还要通知列表删除？
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
        }

    }

}
