package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import lib.ys.LogMgr;
import lib.ys.ui.other.NavBar;
import lib.ys.view.FlowLayout;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
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
        setOnClickListener(R.id.meeting_search_tv_number);
        setOnClickListener(R.id.meeting_search_tv_meeting);
        setOnClickListener(R.id.meeting_search_nav_bar_tv);

        setFl();
    }

    /**
     * FlowLayout的设置
     */
    private void setFl() {
        List<String> mNames = Util.getSections();//全部科室
        TextView tvSection;
        View view;
        for (int i = 0; i < mNames.size(); i++) {
            final String name = mNames.get(i);
            view = inflate(R.layout.layout_meeting_search_section);
            tvSection = (TextView) view.findViewById(R.id.meeting_search_tv_section);
            tvSection.setText(name);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:具体的点击事件
                    showToast(name);
                }
            });
            mFlowLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_search_tv_number:
                showToast("公众号");
                break;
            case R.id.meeting_search_tv_meeting:
                showToast("会议");
                break;
            case R.id.meeting_search_nav_bar_tv:
                search();
                break;
            default:
                break;
        }
    }

    /**
     * 进行搜索
     */
    private void search() {
        String searchStr = mEtSearch.getText().toString().trim();
        LogMgr.e("搜索", searchStr);
    }
}
