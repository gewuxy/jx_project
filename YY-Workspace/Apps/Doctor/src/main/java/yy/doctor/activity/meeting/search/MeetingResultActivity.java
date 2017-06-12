package yy.doctor.activity.meeting.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.EditText;

import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseSRListActivity;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 搜索结果界面
 *
 * @author : GuoXuan
 * @since : 2017/5/3
 */
public class MeetingResultActivity extends BaseSRListActivity<IRec, RecAdapter> {

    private final int KRecUnitNum = 0; // 热门单位号
    private final int KUnitNum = 1; // 搜索单位号
    private final int KMeeting = 2; // 搜索会议

    private int mSearchType; // 搜索类型
    private String mSearchContent; // 搜索内容
    private EditText mEtSearch;

    /**
     * 搜索类型
     */
    @IntDef({
            SearchType.meeting,
            SearchType.unit_num,
            SearchType.all,
    })
    public @interface SearchType {
        int meeting = 0; // 会议
        int unit_num = 1; // 单位号
        int all = 2; // 单位号或会议
    }

    public static void nav(Context context, @SearchType int searchType, String searchContent) {
        Intent i = new Intent(context, MeetingResultActivity.class)
                .putExtra(Extra.KType, searchType)
                .putExtra(Extra.KData, searchContent);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mSearchType = getIntent().getIntExtra(Extra.KType, SearchType.all);
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

        if (mSearchType == SearchType.meeting) {
            mEtSearch.setHint("搜索会议");
        } else if (mSearchType == SearchType.unit_num) {
            mEtSearch.setHint("搜索单位号");
        }

        if (TextUtil.isEmpty(mSearchContent)) {
            if (mSearchType == SearchType.meeting) {
                runOnUIThread(() -> {
                    mEtSearch.requestFocus();
                    KeyboardUtil.showFromView(mEtSearch);
                });
                // 不能下拉
                enableSRRefresh(false);
            }
        } else {
            mEtSearch.setText(mSearchContent);
        }
    }

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mSearchContent)) {
            if (mSearchType == SearchType.unit_num) {
                exeNetworkReq(KRecUnitNum, NetFactory.searchRecUnitNum());
            }
        } else {
            switch (mSearchType) {
                case SearchType.meeting:
                    exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
                    break;
                case SearchType.all:
                    exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
                case SearchType.unit_num:
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
        } else if (id == KUnitNum) {
            // 单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
        } else {
            // 推荐单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
        }
        LogMgr.d(TAG, "onNetworkResponse:" + r.getText());
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        super.onNetworkSuccess(id, result);
    }

    @Override
    public void onClick(View v) {
        enableLocalRefresh(true);
    }
}
