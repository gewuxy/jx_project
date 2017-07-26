package yy.doctor.ui.frag;

import android.view.View;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.frag.base.BaseListFrag;
import yy.doctor.adapter.TitleGradeAdapter;
import yy.doctor.model.Title;
import yy.doctor.model.Title.TTitle;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

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
       /* mGrades = new ArrayList<>();
        TDoctorGrade[] tDoctorGrades = TDoctorGrade.values();
        for (TDoctorGrade tDoctorGrade : tDoctorGrades) {
            mGrades.add(tDoctorGrade.getDoctorGrade());
        }*/
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();
        setDividerHeight(fitDp(0));
//        setData(mGrades);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(0, NetFactory.title());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Title.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        Result<Title> r = (Result<Title>) result;
        if (r.isSucceed()) {
            Title data = r.getData();
            List<String> list = data.getList(TTitle.grade);
            for (String s : list) {
                showToast(s);
            }
        }
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
