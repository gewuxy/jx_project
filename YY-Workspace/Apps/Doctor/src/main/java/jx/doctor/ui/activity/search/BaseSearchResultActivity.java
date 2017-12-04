package jx.doctor.ui.activity.search;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import lib.network.model.NetworkError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.jx.ui.activity.base.BaseSRListActivity;
import jx.doctor.R;
import jx.doctor.adapter.meeting.RecAdapter;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.meet.Meeting.TMeeting;
import jx.doctor.model.search.IRec;
import jx.doctor.model.search.IRec.RecType;
import jx.doctor.model.unitnum.UnitNum;
import jx.doctor.model.unitnum.UnitNum.TUnitNum;
import jx.doctor.ui.activity.me.unitnum.UnitNumDetailActivityRouter;
import jx.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;
import jx.doctor.ui.activity.meeting.MeetingFolderActivityRouter;
import jx.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/8/4
 */
abstract public class BaseSearchResultActivity extends BaseSRListActivity<IRec, RecAdapter> {

    @Arg(opt = true)
    String mSearchContent; // 搜索内容

    private EditText mEtSearch; // 搜索的输入框

    public EditText getSearchView() {
        return mEtSearch;
    }

    @Override
    public void initData() {
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);

        // 搜索的布局
        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        mEtSearch.setText(mSearchContent);
        mEtSearch.setHint(getSearchHint());
        bar.addViewMid(view, null);

        bar.addTextViewRight(R.string.search, v -> {
            mSearchContent = Util.getEtString(mEtSearch);
            if (TextUtil.isEmpty(mSearchContent)) {
                showToast(R.string.please_input_search_content);
                return;
            }
            if (KeyboardUtil.isActive()) {
                // 键盘显示就隐藏
                KeyboardUtil.hideFromView(mEtSearch);
            }
            refresh(RefreshWay.embed);
        });
    }

    @Override
    public void setViews() {
        super.setViews();

        setRefreshEnabled(false);
    }

    @CallSuper
    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onItemClick(View v, int position) {
        int type = getAdapter().getItemViewType(position); // Item类型
        switch (type) {
            case RecType.unit_num: {
                UnitNum item = (UnitNum) getItem(position);
                UnitNumDetailActivityRouter.create(item.getInt(TUnitNum.id)).route(this);
            }
            break;
            case RecType.meeting: {
                Meeting item = (Meeting) getItem(position);
                MeetingDetailsActivityRouter.create(
                        item.getString(TMeeting.id), item.getString(TMeeting.meetName)
                ).route(this);
            }
            break;
            case RecType.meet_folder: {
                Meeting item = (Meeting) getItem(position);
                MeetingFolderActivityRouter.create(item.getString(TMeeting.id))
                        .title(item.getString(TMeeting.meetName))
                        .num(item.getInt(TMeeting.meetCount))
                        .route(this);
            }
            break;
            case RecType.more: {
                if (position - 1 < 0) {
                    return;
                }
                if (getAdapter().getItemViewType(position - 1) == RecType.unit_num) {
                    UnitNumResultActivityRouter.create()
                            .searchContent(mSearchContent)
                            .route(this);
                } else {
                    MeetingResultActivityRouter.create()
                            .searchContent(mSearchContent)
                            .route(this);
                }
            }
            break;
        }
    }

    /**
     * 搜索框的提示语
     */
    abstract protected CharSequence getSearchHint();
}
