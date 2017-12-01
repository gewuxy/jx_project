package jx.doctor.ui.frag.user;

import android.os.Bundle;
import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseListFrag;
import jx.doctor.adapter.user.SectionCategoryAdapter;
import jx.doctor.model.me.Section;
import jx.doctor.model.me.Section.TSection;

/**
 * 科室隶属(一级)
 *
 * @auther GuoXuan
 * @since 2017/5/15
 */

public class SectionCategoryFrag extends BaseListFrag<Section, SectionCategoryAdapter> {

    private OnCategoryListener mListener;

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(fit(0));
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
