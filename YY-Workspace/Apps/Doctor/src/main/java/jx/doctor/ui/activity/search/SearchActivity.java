package jx.doctor.ui.activity.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.util.Util;

/**
 * 搜索单位号和会议
 *
 * @author : GuoXuan
 * @since : 2017/4/26
 */
public class SearchActivity extends BaseActivity {

    private EditText mEtSearch;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_search;
    }

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);

        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewMid(view, null);

        bar.addTextViewRight(R.string.search, v -> {
            String search = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(search)) {
                showToast(R.string.please_input_search_content);
                return;
            }
            if (KeyboardUtil.isActive()) {
                // 键盘显示就隐藏
                KeyboardUtil.hideFromView(mEtSearch);
            }
            SearchResultActivityRouter.create().searchContent(search).route(this);
        });
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.meeting_search_tv_unit_num);
        setOnClickListener(R.id.meeting_search_tv_meeting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_search_tv_unit_num: {
                UnitNumResultActivityRouter
                        .create()
                        .searchContent(ConstantsEx.KEmpty)
                        .route(this);
            }
            break;
            case R.id.meeting_search_tv_meeting: {
                MeetingResultActivityRouter
                        .create()
                        .searchContent(ConstantsEx.KEmpty)
                        .route(this);
            }
            break;
        }
    }

}
