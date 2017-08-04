package yy.doctor.ui.activity.search;

import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.EditText;

import lib.annotation.Extra;
import lib.network.model.err.NetError;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.meeting.RecAdapter;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity;
import yy.doctor.ui.activity.meeting.MeetingDetailsActivity;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/8/4
 */
public abstract class BaseSearchResultActivity extends BaseSRListActivity<IRec, RecAdapter> implements OnAdapterClickListener {

    @Extra(optional = true)
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
        bar.addViewLeft(view, null);

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
    public void findViews() {
        super.findViews();

        setOnAdapterClickListener(this);
    }

    @CallSuper
    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    protected abstract CharSequence getSearchHint();

    @Override
    public final void onAdapterClick(int position, View v) {
        int type = getAdapter().getItemViewType(position);
        switch (type) {
            case IRec.RecType.unit_num: {
                UnitNumDetailActivity.nav(BaseSearchResultActivity.this, ((UnitNum) getItem(position)).getInt(UnitNum.TUnitNum.id));
            }
            break;

            case IRec.RecType.meeting: {
                MeetingDetailsActivity.nav(BaseSearchResultActivity.this, ((Meeting) getItem(position)).getString(Meeting.TMeeting.id), ((Meeting) getItem(position)).getString(Meeting.TMeeting.meetName));
            }
            break;

            case IRec.RecType.meet_folder: {
                showToast("文件夹");
                //MeetingDetailsActivity.nav(BaseSearchResultActivity.this, ((Meeting) getItem(position)).getString(Meeting.TMeeting.id), ((Meeting) getItem(position)).getString(Meeting.TMeeting.meetName));
            }
            break;

            case IRec.RecType.more: {
                if (getAdapter().getItemViewType(position - 1) == IRec.RecType.unit_num) {
                    UnitNumResultActivityIntent
                            .create()
                            .searchContent(mSearchContent)
                            .start(BaseSearchResultActivity.this);
                } else {
                    MeetingResultActivityIntent
                            .create()
                            .searchContent(mSearchContent)
                            .start(BaseSearchResultActivity.this);
                }
            }
            break;
        }
    }
}
