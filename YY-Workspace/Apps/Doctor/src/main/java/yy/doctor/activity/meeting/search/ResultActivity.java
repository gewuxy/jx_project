package yy.doctor.activity.meeting.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.EditText;

import lib.network.model.NetworkResp;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseSRListActivity;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.meet.Meeting;
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
public class ResultActivity extends BaseSRListActivity<IRec, RecAdapter> {

    private final int KRecUnitNum = 0; // 热门单位号
    private final int KUnitNum = 1; // 搜索单位号
    private final int KMeeting = 2; // 搜索会议

    private int mSearchType; // 搜索类型
    private String mSearchContent; // 搜索内容
    private EditText mEtSearch;
    private View mEmpty;

    /**
     * 搜索类型
     */
    @IntDef({
            MeetingResultActivity.SearchType.meeting,
            MeetingResultActivity.SearchType.unit_num,
            MeetingResultActivity.SearchType.all,
    })
    public @interface SearchType {
        int meeting = 0; // 会议
        int unit_num = 1; // 单位号
        int all = 2; // 单位号或会议
    }

    public static void nav(Context context, @MeetingResultActivity.SearchType int searchType, String searchContent) {
        Intent i = new Intent(context, MeetingResultActivity.class)
                .putExtra(Extra.KType, searchType)
                .putExtra(Extra.KData, searchContent);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_result;
    }

    @Override
    public void initData() {
        mSearchType = getIntent().getIntExtra(Extra.KType, MeetingResultActivity.SearchType.all);
        mSearchContent = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewLeft(view, null);
        bar.addTextViewRight("搜索", null);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEmpty = findView(R.id.meeting_result_empty);

        if (mSearchType == MeetingResultActivity.SearchType.meeting) {
            mEtSearch.setHint("搜索会议");
        } else if (mSearchType == MeetingResultActivity.SearchType.unit_num) {
            mEtSearch.setHint("搜索单位号");
        }

        if (TextUtil.isEmpty(mSearchContent)) {
            if (mSearchType == MeetingResultActivity.SearchType.meeting) {
                runOnUIThread(() -> {
                    mEtSearch.requestFocus();
                    KeyboardUtil.showFromView(mEtSearch);
                });
                // 不能下拉
                enableSRRefresh(false);
                setViewState(DecorViewEx.ViewState.normal);
                showView(mEmpty);
            }
        } else {
            mEtSearch.setText(mSearchContent);
        }
    }

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mSearchContent)) {
            if (mSearchType == MeetingResultActivity.SearchType.unit_num) {
                exeNetworkReq(KRecUnitNum, NetFactory.searchRecUnitNum());
            }
        } else {
            switch (mSearchType) {
                case MeetingResultActivity.SearchType.meeting:
                    exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
                    break;
                case MeetingResultActivity.SearchType.all:
                    exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
                case MeetingResultActivity.SearchType.unit_num:
                    exeNetworkReq(KUnitNum, NetFactory.searchUnitNum(mSearchContent));
                    break;
            }
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = null;
        if (id == KMeeting) {
            // 会议
            result = JsonParser.evs(r.getText(), Meeting.class);
//            mBannerReqIsOK
        } else if (id == KUnitNum) {
            // 单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
        } else {
            // 推荐单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
        }
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
//        if (id == KReqIdBanner) {
//            result = evs(r.getText(), Banner.class);
//            mBannerReqIsOK = result.isSucceed();
//            if (mBannerReqIsOK) {
//                mBanners = result.getData();
//            }
//        } else if (id == KReqIdUnitNum) {
//            result = evs(r.getText(), RecUnitNum.class);
//            mUnitNumReqIsOK = result.isSucceed();
//            if (mUnitNumReqIsOK) {
//                mRecUnitNums = result.getData();
//            }
//        }

        setViewState(DecorViewEx.ViewState.normal);
        super.onNetworkSuccess(id, result);
    }

    @Override
    public void onClick(View v) {
        enableSRRefresh(true);
        hideView(mEmpty);
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

            }
            break;
        }
    }

    @Override
    protected String getEmptyText() {
        if (mSearchType == MeetingResultActivity.SearchType.meeting) {
            return "暂无相关会议";
        } else if (mSearchType == MeetingResultActivity.SearchType.unit_num) {
            return "暂无相关单位号";
        } else {
            return "暂无相关会议或单位号";
        }
    }
}