package yy.doctor.ui.activity.search;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.view.FlowLayout;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 搜索单位号和会议
 *
 * @author : GuoXuan
 * @since : 2017/4/26
 */
public class SearchActivity extends BaseActivity {

    private FlowLayout mFlowLayout;//底部科室列表
    private EditText mEtSearch;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_search;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewLeft(view, null);
        bar.addTextViewRight("搜索", v -> {
            String search = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(search)) {
                showToast("请输入搜索内容");
                return;
            }
            ResultActivity.nav(SearchActivity.this, search);
        });
    }

    @Override
    public void findViews() {
        mFlowLayout = findView(R.id.meeting_search_flowlayout);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.meeting_search_tv_unit_num);
        setOnClickListener(R.id.meeting_search_tv_meeting);

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
            view.setOnClickListener(v -> MeetingResultActivity.nav(SearchActivity.this, name));
            mFlowLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_search_tv_unit_num:
                UnitNumResultActivity.nav(SearchActivity.this, null);
                break;
            case R.id.meeting_search_tv_meeting:
                MeetingResultActivity.nav(SearchActivity.this, null);
                break;
        }
    }

}
