package jx.csp.ui.frag.main;

import android.support.annotation.CallSuper;
import android.view.View;

import org.json.JSONException;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.main.MeetAdapter;
import jx.csp.constant.FiltrateType;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.main.MeetInfo;
import jx.csp.model.main.MeetInfo.TMeetInfo;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.MeetPresenterImpl;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.UISetter;
import lib.jx.network.Result;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.frag.base.BaseSRListFrag;
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
public class MeetFrag<A extends MeetAdapter> extends BaseSRListFrag<Meet, A> implements
        IMeetOpt,
        MeetContract.V,
        MultiAdapterEx.OnAdapterClickListener,
        MeetAdapter.OnAdapterLongClickListener {

    private OnMeetGridListener mListener;

    private MeetContract.P mPresenter;
    private View mViewEmpty; // 只是加到footer里(自己控制显示隐藏)
    private View mViewEmptyPpt;
    private View mViewEmptyPhoto;

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
            case R.id.meet_item_no_divider: {
                mPresenter.onMeetClick(item);
            }
            break;
            case R.id.meet_item_iv_share: {
                mPresenter.onShareClick(item);
            }
            break;
            case R.id.meet_item_iv_live: {
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
        // FIXME
        mViewEmpty = inflate(R.layout.layout_meet_empty_foot);
        mViewEmptyPpt = mViewEmpty.findViewById(R.id.empty_footer_ppt);
        mViewEmptyPhoto = mViewEmpty.findViewById(R.id.empty_footer_photo);
        return mViewEmpty;
    }

    public void setListener(OnMeetGridListener listener) {
        mListener = listener;
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
    public void onDataSetChanged() {
        if (MainActivity.mFiltrateType == FiltrateType.ppt) {
            showView(mViewEmptyPpt);
            goneView(mViewEmptyPhoto);
        } else {
            goneView(mViewEmptyPpt);
            showView(mViewEmptyPhoto);
        }

        super.onDataSetChanged();
/*
        if (getData() == null || getData().isEmpty()) {
            hideFooterView();
            showView(mViewEmpty);
        } else {
            for (Meet meet : getData()) {
                if (meet.getType() == MainActivity.mFiltrateType || MainActivity.mFiltrateType == FiltrateType.all) {
                    showFooterView();
                    goneView(mViewEmpty);
                    return;
                }
            }
            hideFooterView();
            showView(mViewEmpty);
        }*/
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.ready);
    }

    @CallSuper
    @Override
    public void invalidate() {
        super.invalidate();

        if (mListener != null) {
            mListener.onMeetRefresh(getData());
        }
    }

    /**
     * 不通知其他刷新
     */
    public void thisRefresh() {
        super.invalidate();
    }
}
