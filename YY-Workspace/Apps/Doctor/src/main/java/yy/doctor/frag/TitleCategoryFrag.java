package yy.doctor.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.Constants.TDoctorCategory;
import yy.doctor.adapter.TitleCategoryAdapter;

/**
 * 医生的类别
 *
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TitleCategoryFrag extends BaseListFrag<String, TitleCategoryAdapter> {

    private OnCategoryListener mListener;
    private List<String> mCategories;

    @Override
    public void initData() {
        mCategories = new ArrayList<>();
        TDoctorCategory[] tDoctorCategories = TDoctorCategory.values();
        for (TDoctorCategory tDoctorCategory : tDoctorCategories) {
            mCategories.add(tDoctorCategory.getDoctorCategory());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(fitDp(0));
        setData(mCategories);
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
