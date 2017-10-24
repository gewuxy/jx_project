package jx.csp.ui.activity.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import jx.csp.R;
import jx.csp.adapter.main.SquareAdapter;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Square;
import jx.csp.model.main.Square.TSquare;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.ui.activity.main.MainActivity.OnSquareRefreshListener;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivityRouter;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRRecyclerFrag;

/**
 * @auther WangLan
 * @since 2017/10/18
 */
public class MainSquareFrag extends BaseSRRecyclerFrag<Square, SquareAdapter> implements OnAdapterClickListener {

    private OnSquareRefreshListener mListener;

    public void setListener(OnSquareRefreshListener listener) {
        mListener=listener;
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

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetingAPI.meetingList().pageNum(getOffset()).pageSize(getLimit()).build());
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
        switch (v.getId()){
            case  R.id.main_square_layout :{
                String courseId = getItem(position).getString(TSquare.id);
                if (getItem(position).getInt(TSquare.playType) == PlayType.reb) {
                    CommonRecordActivityRouter.create(courseId).route(getContext());
                } else {
                    LiveRecordActivityRouter.create(courseId).route(getContext());
                }
            }
            break;
            case R.id.iv_square_share:{
                //Fixme:传个假的url
                ShareDialog shareDialog = new ShareDialog(getContext(), "http://blog.csdn.net/happy_horse/article/details/51164262", "哈哈");
                shareDialog.show();
            }
            break;
        }
    }
}
