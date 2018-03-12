package jx.csp.ui.activity.contribution;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.adapter.contribution.SearchUnitNumAdapter;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.ys.ui.other.NavBar;

/**
 * 搜索投稿单位号
 *
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SearchUnitNumActivity extends BaseListActivity<UnitNum, SearchUnitNumAdapter> {

    private EditText mEtSearch;
    private ImageView mIvClear;
    private TextView mTvSearch;
    private View mEmptyView;

    @Override
    public void initData() {
        for (int i = 0; i < 8; ++i) {
            UnitNum unitNum = new UnitNum();
            unitNum.put(TUnitNum.name, "YaYa医师测试单位号");
            unitNum.put(TUnitNum.search, "单位");
            addItem(unitNum);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_search_unit_num_nav_bar);
        mEtSearch = view.findViewById(R.id.search_unit_num_et);
        mIvClear = view.findViewById(R.id.search_unit_num_iv_clear);
        mTvSearch = view.findViewById(R.id.search_unit_num_tv_search);
        bar.addViewLeft(view, null);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_search_unit_num;
    }

    @Override
    public void findViews() {
        super.findViews();

        mEmptyView = findView(R.id.search_unit_num_empty_layout);
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    showView(mIvClear);
                } else {
                    goneView(mIvClear);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setOnClickListener(mIvClear);
        setOnClickListener(mTvSearch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_unit_num_iv_clear: {
                mEtSearch.setText("");
            }
            break;
            case R.id.search_unit_num_tv_search: {
                showToast("search");
            }
            break;
        }
    }
}
