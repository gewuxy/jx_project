package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.ys.view.FlowLayout;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.activity.meeting.MeetingSearchResultActivity.SearchType;
import yy.doctor.util.Util;

/**
 * 搜索会议界面
 *
 * @author : GuoXuan
 * @since : 2017/4/26
 */

public class MeetingSearchActivity extends BaseActivity {

    private FlowLayout mFlowLayout;//底部科室列表
    private EditText mEtSearch;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_search;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View v = inflate(R.layout.layout_meeting_nav_bar_search);
        bar.addViewLeft(v, null);
    }

    @Override
    public void findViews() {
        mFlowLayout = findView(R.id.meeting_search_flowlayout);
        mEtSearch = findView(R.id.meeting_search_nav_bar_et);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.meeting_search_tv_unit_num);
        setOnClickListener(R.id.meeting_search_tv_meeting);
        setOnClickListener(R.id.meeting_search_nav_bar_tv);

        setLayout();
    }

    /**
     * FlowLayout的设置
     */
    private void setLayout() {
        List<String> mNames = Util.getSections();//全部科室
        TextView tvSection;
        View view;
        for (int i = 0; i < mNames.size(); i++) {
            final String name = mNames.get(i);
            view = inflate(R.layout.layout_meeting_search_section);
            tvSection = (TextView) view.findViewById(R.id.meeting_search_tv_section);
            tvSection.setText(name);
            view.setOnClickListener(v -> search(name));
            mFlowLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_search_tv_unit_num:
                MeetingSearchResultActivity.nav(MeetingSearchActivity.this, SearchType.unit_num, null);
                break;
            case R.id.meeting_search_tv_meeting:
                MeetingSearchResultActivity.nav(MeetingSearchActivity.this, SearchType.meeting, null);
                break;
            case R.id.meeting_search_nav_bar_tv:
                search(mEtSearch.getText().toString().trim());
                break;
        }
    }

    /**
     * 进行搜索
     */
    private void search(String search) {
        MeetingSearchResultActivity.nav(MeetingSearchActivity.this, SearchType.all, search);
    }
}
