package yy.doctor.activity.meeting.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.IRec.RecType;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 单位号和会议的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
public class ResultActivity extends BaseListActivity<IRec, RecAdapter> {

    protected final int KMeeting = 0; // 搜索会议
    protected final int KUnitNum = 1; // 搜索单位号

    protected String mSearchContent; // 搜索内容
    protected EditText mEtSearch; // 搜索框
    protected TextView mTvEmpty;

    protected List<IRec> mMeets;

    protected List<IRec> mUnitNums;
    private boolean mMeetReqIsOK;
    private boolean mUnitNumReqIsOK;

    public static void nav(Context context, String searchContent) {
        Intent i = new Intent(context, MeetingResultActivity.class)
                .putExtra(Extra.KData, searchContent);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_result;
    }

    @Override
    public void initData() {
        mSearchContent = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewLeft(view, null);
        bar.addTextViewRight("搜索", v -> {
            mSearchContent = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(mSearchContent)) {
                showToast("请输入搜索内容");
                return;
            }
            search();
            // FIXME: 2017/6/14 隐藏键盘?
        });
    }

    @Override
    public void setViews() {
        super.setViews();

        if (!TextUtil.isEmpty(mSearchContent)) {
            mEtSearch.setText(mSearchContent);
            search();
        } else {
            searchEmpty();
        }
    }

    /**
     * 搜索为空
     */
    protected void searchEmpty() {

    }

    /**
     * 搜索不为空
     */
    protected void search() {
        mTvEmpty = findView(lib.yy.R.id.empty_footer_tv);
        mTvEmpty.setText(getEmptyText());
        refresh(RefreshWay.embed);
        exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
        exeNetworkReq(KUnitNum, NetFactory.searchUnitNum(mSearchContent));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = null;
        if (id == KMeeting) {
            // 会议
            result = JsonParser.evs(r.getText(), Meeting.class);
            mMeetReqIsOK = result.isSucceed();
            if (mMeetReqIsOK) {
                mMeets = result.getData();
            }
        } else {
            // 单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
            mUnitNumReqIsOK = result.isSucceed();
            if (mUnitNumReqIsOK) {
                mUnitNums = result.getData();
            }
        }
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResult r = (ListResult) result;

        if (id == KMeeting) {
            // 会议
            mMeetReqIsOK = r.isSucceed();
        } else {
            // 单位号 / 推荐单位号
            mUnitNumReqIsOK = r.isSucceed();
        }

        if (mMeetReqIsOK && mUnitNumReqIsOK) {
            setViewState(ViewState.normal);
            if (mUnitNums != null && mUnitNums.size() > 0) {
                addAll(mUnitNums);
            }
            if (mMeets != null && mMeets.size() > 0) {
                addAll(mMeets);
            }
            invalidate();
            mMeetReqIsOK = false;
            mUnitNumReqIsOK = false;
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onItemClick(View v, int position) {
        int type = getAdapter().getItemViewType(position);
        switch (type) {
            case RecType.unit_num: {
                UnitNumDetailActivity.nav(ResultActivity.this, ((UnitNum) getItem(position)).getInt(TUnitNum.id));
            }
            break;

            case RecType.meeting: {
                MeetingDetailsActivity.nav(ResultActivity.this, ((Meeting) getItem(position)).getString(TMeeting.id));
            }
            break;
        }
    }

    @Override
    public View createEmptyView() {
        return inflate(lib.yy.R.layout.layout_empty_footer);
    }

    protected String getEmptyText() {
        return "暂无相关单位号或者会议";
    }

}