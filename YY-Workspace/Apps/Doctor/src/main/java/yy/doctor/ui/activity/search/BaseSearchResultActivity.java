package yy.doctor.ui.activity.search;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import lib.network.model.NetworkError;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.IRec.RecType;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.model.unitnum.UnitNum.TUnitNum;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivityRouter;
import yy.doctor.ui.activity.meeting.MeetingFolderActivityRouter;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/8/4
 */
public abstract class BaseSearchResultActivity extends BaseSRListActivity<IRec, RecAdapter> {

    @Arg(optional = true)
    String mSearchContent; // 搜索内容

    private EditText mEtSearch;

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

        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        mEtSearch.setText(mSearchContent);
        mEtSearch.setHint(getSearchHint());
        bar.addViewMid(view, null);

        bar.addTextViewRight("搜索", v -> {
            mSearchContent = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(mSearchContent)) {
                showToast("请输入搜索内容");
                return;
            }
            // 键盘显示就隐藏
            if (KeyboardUtil.isActive()) {
                KeyboardUtil.hideFromView(mEtSearch);
            }
            refresh();
        });
    }

    @CallSuper
    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    protected abstract CharSequence getSearchHint();

    @Override
    public void onItemClick(View v, int position) {
        int type = getAdapter().getItemViewType(position);
        switch (type) {
            case RecType.unit_num: {
                UnitNum item = (UnitNum) getItem(position);
                UnitNumDetailActivity.nav(this, item.getInt(TUnitNum.id));
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
                if (getAdapter().getItemViewType(position - 1) == IRec.RecType.unit_num) {
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
}
