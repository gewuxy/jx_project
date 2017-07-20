package yy.doctor.ui.activity.data;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.data.DrugListAdapter;
import yy.doctor.util.Util;

/**
 * 药品搜索
 *
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugSearchActivity extends BaseListActivity<String, DrugListAdapter>{

    private LinearLayout mLayoutEmpty;   // 一进来是显示的布局 什么都没有
    private View mLayoutSearchEmpty;  // 搜索结果为空的布局
    protected String mSearchContent; // 搜索内容
    protected EditText mEtSearch; // 搜索框

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        bar.addViewLeft(view, null);
        bar.addTextViewRight(R.string.search, v -> {
            mSearchContent = mEtSearch.getText().toString().trim();
            if (TextUtil.isEmpty(mSearchContent)) {
                showToast(R.string.please_input_search_content);
                return;
            }
            // 键盘收取
            if (KeyboardUtil.isActive()) {
                KeyboardUtil.hideFromView(mEtSearch);
            }
            search();
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_result;
    }

    @Nullable
    @Override
    public View createEmptyView() {
        return null;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutEmpty = findView(R.id.meeting_result_layout_empty);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtSearch.setHint(R.string.drug_list_search_hint);
    }

    private void search() {
        //refresh(RefreshWay.embed);
        String str = mEtSearch.getText().toString().trim();
        removeAll(); // 搜索前清空之前的数据集

        // FIXME: 2017/7/17 为了测试搜索结果为空
        if (str.equals("666")) {
            if (mLayoutSearchEmpty == null) {
                mLayoutSearchEmpty = inflate(R.layout.layout_empty_footer);
                TextView tv = (TextView) mLayoutSearchEmpty.findViewById(R.id.empty_footer_tv);
                tv.setText(R.string.search_result_is_empty);
                mLayoutEmpty.addView(mLayoutSearchEmpty);
            }
            showView(mLayoutEmpty);
        } else {
            goneView(mLayoutEmpty);
            for (int i = 0; i < 5; ++i) {
                addItem(i + "");
            }
        }
        invalidate();
    }

}
