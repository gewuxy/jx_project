package jx.doctor.ui.frag.user;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.jx.ui.frag.base.BaseListFrag;
import jx.doctor.adapter.user.TitleGradeAdapter;

/**
 * 医生职称级别
 *
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TitleGradeFrag extends BaseListFrag<String, TitleGradeAdapter> {

    private OnGradeListener mListener;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(fit(0));
        setBackgroundColor(Color.parseColor("#eaeaea"));
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mListener != null) {
            mListener.onGradeSelected(position, getItem(position));
        }
        getAdapter().setSelectItem(position);
    }

    public interface OnGradeListener {
        void onGradeSelected(int position, String grade);
    }

    public void setGradeListener(OnGradeListener l) {
        mListener = l;
    }
}
