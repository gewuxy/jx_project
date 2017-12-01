package jx.doctor.ui.frag.user;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseListFrag;
import jx.doctor.adapter.user.TitleCategoryAdapter;

/**
 * 医生的类别
 *
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TitleCategoryFrag extends BaseListFrag<String, TitleCategoryAdapter> {

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
        setBackgroundColor(Color.parseColor("#f0f0f0"));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            mListener.onCategorySelected(position, getItem(position));
        }
    }

    public interface OnCategoryListener {
        void onCategorySelected(int position, String name);
    }

    public void setCategoryListener(OnCategoryListener l) {
        mListener = l;
    }

}
