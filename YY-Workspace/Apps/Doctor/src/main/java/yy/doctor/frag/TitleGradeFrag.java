package yy.doctor.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseListFrag;
import yy.doctor.Constants.TDoctorGrade;
import yy.doctor.adapter.TitleGradeAdapter;

/**
 * 医生职称级别
 *
 * @author CaiXiang
 * @since 2017/5/24
 */

public class TitleGradeFrag extends BaseListFrag<String, TitleGradeAdapter> {

    private OnGradeListener mListener;
    private List<String> mGrades;

    @Override
    public void initData() {
        mGrades = new ArrayList<>();
        TDoctorGrade[] tDoctorGrades = TDoctorGrade.values();
        for (TDoctorGrade tDoctorGrade : tDoctorGrades) {
            mGrades.add(tDoctorGrade.getDoctorGrade());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(fitDp(0));
        setData(mGrades);
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
