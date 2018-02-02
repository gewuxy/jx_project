package jx.csp.ui.frag.main;

import android.view.View;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.main.MeetAdapter;
import jx.csp.constant.FiltrateType;
import jx.csp.contact.MeetContract;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.main.MeetInfo;
import jx.csp.model.main.MeetInfo.TMeetInfo;
import jx.csp.model.meeting.Live;
import jx.csp.model.meeting.Record;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.presenter.MeetPresenterImpl;
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

    private MeetContract.P mPresenter;
    private View mViewEmptyPpt;
    private View mViewEmptyPhoto;

    @FiltrateType
    private int mFiltrateType;
    private List<Meet> mAllMeets; // 全部数据
    private List<Meet> mPhotoMeets;
    private List<Meet> mPptMeets;

    public void setFiltrateType(@FiltrateType int type) {
        mFiltrateType = type;
        if (mFiltrateType == FiltrateType.ppt) {
            showView(mViewEmptyPpt);
            goneView(mViewEmptyPhoto);
        } else {
            goneView(mViewEmptyPpt);
            showView(mViewEmptyPhoto);
        }
        removeAll();
        invalidate();
        switch (mFiltrateType) {
            case FiltrateType.ppt: {
                nativeSetDate(new ArrayList<>(mPptMeets));
            }
            break;
            case FiltrateType.photo: {
                nativeSetDate(new ArrayList<>(mPhotoMeets));
            }
            break;
            case FiltrateType.all:
            default: {
                nativeSetDate(new ArrayList<>(mAllMeets));
            }
            break;
        }
        invalidate();
    }

    @Override
    public void initData() {
        mFiltrateType = FiltrateType.all;
        mPresenter = new MeetPresenterImpl(this, getContext());
    }

    @Override
    public void initNavBar(NavBar bar) {
        // no nav bar
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
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
            List<Meet> meets = meetInfo.getList(TMeetInfo.list);
            changeAll(meets);
            switch (mFiltrateType) {
                case FiltrateType.ppt: {
                    r.setData(new ArrayList<>(mPptMeets));
                }
                break;
                case FiltrateType.photo: {
                    r.setData(new ArrayList<>(mPhotoMeets));
                }
                break;
                case FiltrateType.all:
                default: {
                    r.setData(new ArrayList<>(mAllMeets));
                }
                break;
            }
        }
        return r;
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
        View viewEmpty = inflate(R.layout.layout_meet_empty_foot);
        mViewEmptyPpt = viewEmpty.findViewById(R.id.empty_footer_ppt);
        mViewEmptyPhoto = viewEmpty.findViewById(R.id.empty_footer_photo);
        return viewEmpty;
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
    protected String getEmptyText() {
        return getString(R.string.ready);
    }

    /**
     * 用{@link MeetFrag#setAllMeets}
     */
    @Deprecated
    @Override
    public void setData(List<Meet> list) {
    }

    private void nativeSetDate(List<Meet> list) {
        super.setData(list);
    }

    public void setAllMeets(List<Meet> meets) {
        changeAll(meets);
    }

    public List<Meet> getAllMeets() {
        return mAllMeets;
    }

    private void changeAll(List<Meet> meets) {
        if (meets != null) {
            mAllMeets = new ArrayList<>();
            mPhotoMeets = new ArrayList<>();
            mPptMeets = new ArrayList<>();
            for (Meet meet : meets) {
                switch (meet.getType()) {
                    case FiltrateType.ppt: {
                        mPptMeets.add(meet);
                        mAllMeets.add(meet);
                    }
                    break;
                    case FiltrateType.photo: {
                        mPhotoMeets.add(meet);
                        mAllMeets.add(meet);
                    }
                    break;
                }
            }
        } else {
            mAllMeets = new ArrayList<>();
            mPhotoMeets = new ArrayList<>();
            mPptMeets = new ArrayList<>();
        }
    }

    @Override
    public void onNotify(int type, Object data) {
        switch (type) {
            case NotifyType.delete_meeting_success: {
                if (data instanceof String) {
                    String str = (String) data;
                    YSLog.d(TAG, str + "删除接收通知");
                    for (Meet meet : mAllMeets) {
                        if (Integer.valueOf(str) == meet.getInt(TMeet.id)) {
                            mAllMeets.remove(meet);
                            changeAll(mAllMeets);
                            setFiltrateType(mFiltrateType);
                            break;
                        }
                    }
                }
            }
            break;
            case NotifyType.over_live: {
                if (data instanceof String) {
                    String id = (String) data;
                    for (Meet meet : mAllMeets) {
                        if (meet.getString(TMeet.id).equals(id)) {
                            meet.put(TMeet.liveState, Live.LiveState.end);
                            meet.put(TMeet.playState, Record.PlayState.end);
                            changeAll(mAllMeets);
                            setFiltrateType(mFiltrateType);
                            break;
                        }
                    }
                }
            }
            break;
            case NotifyType.start_live: {
                if (data instanceof String) {
                    String id = (String) data;
                    for (Meet meet : mAllMeets) {
                        if (meet.getString(TMeet.id).equals(id)) {
                            meet.put(TMeet.liveState, Live.LiveState.live);
                            changeAll(mAllMeets);
                            setFiltrateType(mFiltrateType);
                            break;
                        }
                    }
                }
            }
            break;
            case NotifyType.total_time: {
                if (data instanceof Meet) {
                    Meet m = (Meet) data;
                    String id = m.getString(TMeet.id);
                    String time = m.getString(TMeet.playTime);
                    for (Meet meet : mAllMeets) {
                        if (meet.getString(TMeet.id).equals(id)) {
                            meet.put(TMeet.playTime, time);
                            changeAll(mAllMeets);
                            setFiltrateType(mFiltrateType);
                            break;
                        }
                    }
                }
            }
            break;
        }
    }
}
