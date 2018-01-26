package jx.csp.ui.frag.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.main.MeetGridAdapter;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.main.MeetInfo;
import jx.csp.model.main.MeetInfo.TMeetInfo;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.MeetPresenterImpl;
import jx.csp.util.UISetter;
import lib.jx.network.Result;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.frag.base.BaseSRRecyclerFrag;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.ui.other.NavBar;

/**
 * 首页九宫格的frag
 *
 * @auther WangLan
 * @since 2017/10/18
 */
public class MeetGridFrag extends BaseSRRecyclerFrag<Meet, MeetGridAdapter> implements
        IMeetOpt,
        MeetContract.V,
        MultiAdapterEx.OnAdapterClickListener,
        MeetGridAdapter.OnAdapterLongClickListener {

    private OnMeetGridListener mListener;

    private MeetContract.P mPresenter;

    public interface OnMeetGridListener {
        void onMeetRefresh(List<Meet> data);
    }

    @Override
    public void initData() {
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
        getAdapter().setLongClickListener(this);
    }

    @Override
    protected LayoutManager initLayoutManager() {
        return new GridLayoutManager(getContext(), MeetGridAdapter.KSpanCount);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetingAPI.meetingList(getOffset(), getLimit()).build());
    }

    @Override
    public IResult parseNetworkResponse(int id, String text) throws JSONException {
        Result<MeetInfo> info = JsonParser.ev(text, MeetInfo.class);
        MeetInfo meetInfo = info.getData();
        Result<Meet> r = new Result<>();
        r.setCode(info.getCode());
        r.setMessage(info.getMessage());
        if (meetInfo != null) {
            int num = meetInfo.getInt(TMeetInfo.hideCount);
            runOnUIThread(() -> notify(NotifyType.meet_num, num));
            List<Meet> list = meetInfo.getList(TMeetInfo.list);
            r.setData(list);
        }
        return r;
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        if (mListener != null) {
            mListener.onMeetRefresh(getData());
        }
    }

    @Override
    public void onAdapterClick(int position, View v) {
        Meet item = getItem(position);

        switch (v.getId()) {
            case R.id.main_meet_layout: {
                mPresenter.onMeetClick(item);
            }
            break;
            case R.id.main_meet_iv_share: {
                mPresenter.onShareClick(item);
            }
            break;
            case R.id.main_meet_iv_live: {
                mPresenter.onLiveClick(item);
            }
            break;
        }
    }

    @Override
    public void onLongClick(int position, View v) {
        String courseId = getItem(position).getString(TMeet.id);
        UISetter.showDeleteMeet(courseId, getContext());
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_main_empty_footer);
    }

    public void setListener(OnMeetGridListener listener) {
        mListener = listener;
    }

    @Override
    public void setPosition(int position) {
        setSelection(position);
    }

    @Override
    public int getPosition() {
        RecyclerView rv = getScrollableView();
        GridLayoutManager l = (GridLayoutManager) rv.getLayoutManager();
        return l.findFirstVisibleItemPosition();
    }

    @Override
    public void onStopRefresh() {
    }

    @Override
    public void allowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "grid enter p = null");
            return;
        }
        mPresenter.allowEnter();
    }

    @Override
    public void notAllowEnter() {
        if (mPresenter == null) {
            YSLog.d(TAG, "grid noEnter p = null");
            return;
        }
        mPresenter.notAllowEnter();
    }

    @Override
    public void showSharePlayback(String id) {
        for (int i = 0; i < getData().size(); ++i) {
            Meet meet = getItem(i);
            if (meet.getString(TMeet.id).equals(id)) {
                getAdapter().showSharePlayback(i);
            }
        }
    }

    @Override
    public void goneSharePlayback(String id) {
        for (int i = 0; i < getData().size(); ++i) {
            Meet meet = getItem(i);
            if (meet.getString(TMeet.id).equals(id)) {
                getAdapter().goneSharePlayback(i);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (mListener != null) {
            mListener.onMeetRefresh(getData());
        }
    }
}
