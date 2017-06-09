package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;

import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.search.IRec;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 搜索结果界面
 *
 * @author : GuoXuan
 * @since : 2017/5/3
 */

public class MeetingSearchResultActivity extends BaseSRListActivity<IRec, RecAdapter> {

    private final int KRecUnitNum = 0; // 热门单位号
    private final int KUnitNum = 1; // 搜索单位号
    private final int KMeeting = 2; // 搜索会议

    private int mSearchType; // 搜索类型
    private String mSearchContent; // 搜索内容

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
        Intent i = new Intent(context, MeetingSearchResultActivity.class)
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
        bar.addViewLeft(view, null);
        bar.addTextViewRight("搜索", null);
    }

    @Override
    public void getDataFromNet() {
        if (!TextUtil.isEmpty(mSearchContent)) {

        } else {
            switch (mSearchType) {
                case SearchType.meeting:
                    break;
                case SearchType.unit_num:
                    exeNetworkReq(KRecUnitNum, NetFactory.searchRecUnitNum());
                    break;
                case SearchType.all:
                    break;

            }
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        LogMgr.d(TAG, r.getText());
        return super.onNetworkResponse(id, r);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
    }
}
