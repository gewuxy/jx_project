package yy.doctor.ui.frag.data;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.ClinicalGuideAdapter;
import yy.doctor.ui.activity.data.ClinicalGuideSearchActivity;
import yy.doctor.ui.activity.data.ClinicalGuideCategoryActivity;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ClinicalGuideFrag extends BaseSRListFrag<String, ClinicalGuideAdapter> {

    private TextView mTvSearch;

    @Override
    public void initData() {
        for (int i = 0; i < 12; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_data_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvSearch = findView(R.id.data_header_tv_search);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.data_header_search_layout);
        mTvSearch.setText(R.string.clinical_guide_search_hint);
    }

    @Override
    public void getDataFromNet() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.data_header_search_layout) {
            startActivity(ClinicalGuideSearchActivity.class);
        }
    }

    @Override
    public void onItemClick(View v, int position) {

        startActivity(ClinicalGuideCategoryActivity.class);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }

}
