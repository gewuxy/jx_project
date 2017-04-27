package yy.doctor.activity.register;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import lib.ys.adapter.MultiGroupAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.decor.DecorViewEx;
import lib.ys.ex.NavBar;
import lib.ys.network.resp.IListResponse;
import lib.ys.view.SideBar;
import lib.ys.view.SideBar.OnTouchLetterListener;
import lib.yy.activity.base.BaseSRGroupListActivity;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.adapter.HospitalAdapter;
import yy.doctor.model.hospital.GroupHospital;
import yy.doctor.model.hospital.Hospital;
import yy.doctor.util.Util;

import static yy.doctor.model.hospital.Hospital.THospital.name;


/**
 * 选择医院的界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

public class HospitalActivity extends BaseSRGroupListActivity<GroupHospital> {

    private static final int KLetterColorNormal = Color.parseColor("#888888");
    private static final int KLetterColorFocus = Color.parseColor("#0882e7");
    private static final long KDuration = 200l;//切换科室动画时长

    private SideBar mSideBar;
    private int mLetterSize;
    private TextView mTvLetter;

    @Override
    public void initData() {
        mLetterSize = fitDp(10);

        //模拟数据
        GroupHospital groupHospital = new GroupHospital();

        for (int i = 0; i < 10; i++) {
            Hospital hospital = new Hospital();
            hospital.put(name, "" + i);
            groupHospital.add(hospital);
        }

        groupHospital.setInitial("A");
        addItem(groupHospital);
        addItem(groupHospital);
        addItem(groupHospital);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "医院", this);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvLetter = findView(R.id.hospital_tv_letter);
        mSideBar = (SideBar) getDecorView().findViewById(R.id.hospital_sb);
        initSideBar();
    }

    @Override
    public void setViews() {
        super.setViews();

        expandAllGroup();

        setRefreshEnable(false);
    }

    /**
     * 初始化SideBar
     */
    private void initSideBar() {
        mSideBar.setTextSize(mLetterSize);
        mSideBar.setColor(KLetterColorNormal);
        mSideBar.setColorFocus(KLetterColorFocus);
        mSideBar.setOnTouchLetterChangeListener(new OnTouchLetterListener() {
            @Override
            public void onTouchLetterChanged(String s) {
                mTvLetter.setText(s);
                mTvLetter.setVisibility(View.VISIBLE);
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvLetter.setVisibility(View.GONE);
                    }
                }, KDuration);
            }
        });
    }

    @Override
    public void setViewState(@DecorViewEx.ViewState int state) {
        super.setViewState(state);
    }

    @Override
    public MultiGroupAdapterEx<GroupHospital, ? extends ViewHolderEx> createAdapter() {
        return new HospitalAdapter();
    }

    @Override
    public void getDataFromNet() {
        //
    }

    @Override
    public IListResponse<GroupHospital> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

    @Override
    public boolean canAutoRefresh() {
        if (BuildConfig.TEST) {
            return false;
        } else {
            return true;
        }
    }
}
