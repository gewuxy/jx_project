package yy.doctor.ui.frag.user;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseListFrag;
import yy.doctor.adapter.user.SectionCategoryAdapter;
import yy.doctor.model.me.Section;
import yy.doctor.model.me.Section.TSection;

/**
 * 科室隶属(一级)
 *
 * @auther GuoXuan
 * @since 2017/5/15
 */

public class SectionCategoryFrag extends BaseListFrag<Section, SectionCategoryAdapter> {

    private OnCategoryListener mListener;

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(fitDp(0));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            String category = getItem(position).getString(TSection.category);
            mListener.onCategorySelected(position, category);
        }
        getAdapter().setSelectItem(position);
    }

    public interface OnCategoryListener {
        void onCategorySelected(int position, String name);
    }

    public void setCategoryListener(OnCategoryListener l) {
        mListener = l;
    }
}
